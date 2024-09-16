package app.cardcapture.s3.service;

import app.cardcapture.ai.common.AiImage;
import app.cardcapture.ai.common.repository.AiImageRepository;
import app.cardcapture.common.dto.ErrorCode;
import app.cardcapture.common.dto.ImageDto;
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
    private final AiImageRepository aiImageRepository;

    public S3Service(AmazonS3 amazonS3,
                     @Value("${cloud.aws.s3.bucket}") String bucket,
                     @Value("${cloud.aws.region.static}") String region,
        AiImageRepository aiImageRepository) {
        this.amazonS3 = amazonS3;
        this.bucket = bucket;
        this.region = region;
        this.aiImageRepository = aiImageRepository;
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

    public ImageDto uploadImageFromUrl(String path, String imageUrl, String fileName,
        String revisedPrompt, String extension, User user) {
        URL url = convertToURL(imageUrl);

        HttpURLConnection connection = getHttpURLConnection(url);

        try (InputStream inputStream = connection.getInputStream()) {
            byte[] imageBytes = IOUtils.toByteArray(inputStream);

            try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(imageBytes)) {
                ObjectMetadata metadata = new ObjectMetadata();
                metadata.setContentLength(imageBytes.length);
                metadata.setContentType(connection.getContentType());

                amazonS3.putObject(bucket+path, fileName+user.getId()+"."+extension, byteArrayInputStream, metadata);

                //TODO: ImageDto 각 메서드가 응답할 수 있는 범위로 분리하기
                AiImage aiImage = new AiImage();
                aiImage.setPrompt(revisedPrompt);
                AiImage savedAiImage = aiImageRepository.save(aiImage);


                return new ImageDto(
                    bucket+path,
                    fileName+user.getId()+"."+extension,
                    String.format("https://%s.s3.%s.amazonaws.com"+path+"/%s", bucket, "ap-northeast-2", fileName+user.getId()+"."+extension),
                    imageBytes,
                    savedAiImage.getPrompt(),
                    savedAiImage.getId());
            }
        } catch (IOException e) {
            throw new BusinessLogicException(ErrorCode.IMAGE_URL_READ_FAILED);
        }
    }

    private HttpURLConnection getHttpURLConnection(URL url) {
        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            return connection;
        } catch (IOException e) {
            throw new BusinessLogicException(ErrorCode.IMAGE_URL_READ_FAILED);
        }
    }

    private URL convertToURL(String imageUrl) {
        try {
            return new URL(imageUrl);
        } catch (MalformedURLException e) {
            throw new BusinessLogicException(ErrorCode.MALFORMED_IMAGE_URL);
        }
    }

    public String uploadImageFromByte(byte[] removed, String path, String fileName, String extension, User user) {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(removed)) {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(removed.length);
            metadata.setContentType("image/png");

            amazonS3.putObject(bucket+path, fileName+user.getId()+"."+extension, byteArrayInputStream, metadata);
            return String.format("https://%s.s3.%s.amazonaws.com"+path+"/%s", bucket, "ap-northeast-2", fileName+user.getId()+"."+extension);
        } catch (IOException e) {
            throw new BusinessLogicException(ErrorCode.IMAGE_RAW_READ_FAILED);
        }
    }
}