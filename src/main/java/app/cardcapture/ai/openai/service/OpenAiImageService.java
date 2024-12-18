package app.cardcapture.ai.openai.service;

import app.cardcapture.ai.common.AiModel;
import app.cardcapture.ai.openai.config.OpenAiImageConfig;
import app.cardcapture.common.dto.ErrorCode;
import app.cardcapture.common.dto.ImageDto;
import app.cardcapture.common.exception.BusinessLogicException;
import app.cardcapture.common.utils.StringUtils;
import app.cardcapture.s3.service.S3Service;
import app.cardcapture.user.domain.entity.User;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.image.ImageMessage;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.openai.OpenAiImageModel;
import org.springframework.ai.openai.OpenAiImageOptions;
import org.springframework.ai.openai.metadata.OpenAiImageGenerationMetadata;
import org.springframework.ai.retry.NonTransientAiException;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class OpenAiImageService {

    private final OpenAiImageModel openAiImageModel;
    private final OpenAiImageConfig openAiImageConfig;
    private final S3Service s3Service;

    public ImageDto generateImageAndSave(
        int count,
        AiModel aiModel,
        User user,
        ImageMessage imageMessage
    ) {
        ImagePrompt imagePrompt = makeImagePrompt(
            imageMessage,
            aiModel,
            count,
            user);

        try {
            ImageResponse imageResponse = openAiImageModel.call(imagePrompt);
            log.info("imageResponse.toString() = " + imageResponse.toString());
            return saveImageToS3Url(user, imageResponse);
        } catch (NonTransientAiException e) {
            throw new BusinessLogicException(ErrorCode.SERVER_ERROR);
        }

    }

    public ImageDto saveImageToS3Url(
        User user,
        ImageResponse imageResponse
    ) {
        String openAiImageUrl = imageResponse.getResult().getOutput().getUrl();
        String fileName = StringUtils.makeUniqueFileName();

        OpenAiImageGenerationMetadata openAiImageGenerationMetadata = (OpenAiImageGenerationMetadata) imageResponse.getResults()
            .get(0).getMetadata();
        String revisedPrompt = openAiImageGenerationMetadata.getRevisedPrompt();

        log.info("openAiImageGenerationMetadata.getRevisedPrompt() = "
            + openAiImageGenerationMetadata.getRevisedPrompt());

        return s3Service.uploadImageFromUrl(
            openAiImageConfig.getBackgroundImageFilePath(),
            openAiImageUrl,
            fileName,
            revisedPrompt,
            openAiImageConfig.getExtension(),
            user);
    }

    public ImagePrompt makeImagePrompt(
        ImageMessage imageMessage,
        AiModel aiModel,
        int count,
        User user
    ) {
        OpenAiImageOptions openAiImageOptions = OpenAiImageOptions.builder()
            .withModel(aiModel.getApiName())
            .withN(count)
            .withQuality(openAiImageConfig.getQuality())
            .withResponseFormat(openAiImageConfig.getResponseFormat())
            .withUser(String.valueOf(user.getId()))
            .withWidth(openAiImageConfig.getWidth())
            .withHeight(openAiImageConfig.getHeight())
            .build();

        ImagePrompt imagePrompt = new ImagePrompt(List.of(imageMessage), openAiImageOptions);
        return imagePrompt;
    }
}
