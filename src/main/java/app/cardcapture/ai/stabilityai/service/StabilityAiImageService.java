package app.cardcapture.ai.stabilityai.service;

import app.cardcapture.ai.openai.config.StabilityAiConfig;
import app.cardcapture.ai.openai.config.StabilityAiImageConfig;
import app.cardcapture.common.exception.BusinessLogicException;
import app.cardcapture.s3.service.S3Service;
import app.cardcapture.user.domain.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
@Slf4j
@RequiredArgsConstructor
public class StabilityAiImageService {

    private final StabilityAiConfig stabilityAiConfig;
    private final StabilityAiImageConfig stabilityAiImageConfig;
    private final S3Service s3Service;
    private final RestClient restClient;

    public String removeBackgroundAndSaveToUrl(byte[] imageBytes, String fileName, User user) {
        ByteArrayResource imageResource = new ByteArrayResource(imageBytes) {
            @Override
            public String getFilename() {
                return fileName + stabilityAiImageConfig.getExtension();
            }
        };

        MultipartBodyBuilder bodyBuilder = new MultipartBodyBuilder();
        bodyBuilder.part("image", imageResource);

        byte[] removed = restClient.post()
            .uri("https://api.stability.ai/v2beta/stable-image/edit/remove-background")
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .header(HttpHeaders.AUTHORIZATION, "Bearer "+stabilityAiConfig.getApiKey())
            .header(HttpHeaders.ACCEPT, "image/*")
            .body(bodyBuilder.build())
            .retrieve()
            .onStatus(HttpStatusCode::isError, (request, response) -> {
                log.error("Failed to remove background: " + response.getStatusText());
                throw new BusinessLogicException("Failed to remove background",
                    HttpStatus.INTERNAL_SERVER_ERROR);
            })
            .body(byte[].class);
        System.out.println("removed = " + removed);

        return s3Service.uploadImageFromByte(
            removed,
            stabilityAiImageConfig.getRemovedBackgroundImageFilePath(),
            fileName+"_removed",
            stabilityAiImageConfig.getExtension(),
            user);
    }
}
