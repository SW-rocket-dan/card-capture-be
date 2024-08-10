package app.cardcapture.s3.service;

import app.cardcapture.common.exception.BusinessLogicException;
import app.cardcapture.user.domain.entity.User;
import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.util.IOUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Service
public class S3Service {

    private final AmazonS3 amazonS3;
    private final String bucket;
    private final String region;

    public S3Service(AmazonS3 amazonS3,
                     @Value("${cloud.aws.s3.bucket}") String bucket,
                     @Value("${cloud.aws.region.static}") String region) {
        this.amazonS3 = amazonS3;
        this.bucket = bucket;
        this.region = region;
    }

    public String generatePresignedUrl(String dirName, String fileName, String extension) {
        String uuid = UUID.randomUUID().toString();
        String uniqueFileName = uuid + "_" + fileName.replaceAll("\\s", "_") + "." + extension;
        String objectKey = dirName + "/" + uniqueFileName;

        Date expiration = new Date();
        long expTimeMillis = expiration.getTime();
        expTimeMillis += 1000 * 60 * 10; // 10분 유효 기간
        expiration.setTime(expTimeMillis);

        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest(bucket, objectKey)
                        .withMethod(HttpMethod.PUT)
                        .withExpiration(expiration);

        URL url = amazonS3.generatePresignedUrl(generatePresignedUrlRequest);
        return url.toString();
    }

    public void deleteFile(String fileName) {
        try {
            String decodedFileName = URLDecoder.decode(fileName, "UTF-8");
            log.info("Deleting file from S3: " + decodedFileName);
            amazonS3.deleteObject(bucket, decodedFileName);
        } catch (UnsupportedEncodingException e) {
            log.error("Error while decoding the file name: {}", e.getMessage());
        }
    }

    public void deleteFileByUrl(String fileUrl) {
        String fileName = extractFileNameFromUrl(fileUrl);
        deleteFile(fileName);
    }

    public String extractFileUrl(String presignedUrl) {
        return presignedUrl.split("\\?")[0];
    }

    private String extractFileNameFromUrl(String fileUrl) {
        String bucketUrlPattern = String.format("https://%s.s3.%s.amazonaws.com/", bucket, region);
        if (fileUrl.startsWith(bucketUrlPattern)) {
            return fileUrl.substring(bucketUrlPattern.length());
        } else {
            throw new IllegalArgumentException("Invalid S3 URL: " + fileUrl);
        }
    }

    public String uploadImageFromUrl(String path, String imageUrl, String fileName, String extension, User user) {
        URL url = convertToURL(imageUrl);

        HttpURLConnection connection = getHttpURLConnection(imageUrl, url);

        try (InputStream inputStream = connection.getInputStream()) {
            byte[] imageBytes = IOUtils.toByteArray(inputStream);

            try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(imageBytes)) {
                ObjectMetadata metadata = new ObjectMetadata();
                metadata.setContentLength(imageBytes.length);
                metadata.setContentType(connection.getContentType());

                amazonS3.putObject(bucket+path, fileName+"."+extension+user.getId(), byteArrayInputStream, metadata);
                return String.format("https://%s.s3.%s.amazonaws.com/poster/%s",
                        bucket, "ap-northeast-2", fileName+"."+extension+user.getId());
            }
        } catch (IOException e) {
            throw new BusinessLogicException("Error while reading the image from the URL: " + imageUrl, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private HttpURLConnection getHttpURLConnection(String imageUrl, URL url) {
        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            return connection;
        } catch (IOException e) {
            throw new BusinessLogicException("Error while connecting to the image URL: " + imageUrl, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private URL convertToURL(String imageUrl) {
        try {
            return new URL(imageUrl);
        } catch (MalformedURLException e) {
            throw new BusinessLogicException("Invalid image URL: " + imageUrl, HttpStatus.BAD_REQUEST);
        }
    }
}