package app.cardcapture.s3.service;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.UnsupportedEncodingException;
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
}