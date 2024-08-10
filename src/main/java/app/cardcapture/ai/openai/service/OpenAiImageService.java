package app.cardcapture.ai.openai.service;

import app.cardcapture.ai.common.AiModel;
import app.cardcapture.ai.openai.config.OpenAiImageConfig;
import app.cardcapture.common.utils.StringUtils;
import app.cardcapture.s3.service.S3Service;
import app.cardcapture.user.domain.entity.User;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.image.ImageMessage;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.openai.OpenAiImageModel;
import org.springframework.ai.openai.OpenAiImageOptions;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@Slf4j
@RequiredArgsConstructor
public class OpenAiImageService {

    private final OpenAiImageModel openAiImageModel;
    private OpenAiImageConfig openAiImageConfig;
    private final S3Service s3Service;

    public String getFileNamePrefix(
        String openAiImageUrl
    ) {
        String uniqueImageParameter = openAiImageConfig.getUniqueImageParameter(); // TODO: 이런 상수값 어떻게 관리하고 코드에서 사용하는게 좋을지 너무너무 궁금합니다!!!
        return extractUniqueImageParameter(openAiImageUrl, uniqueImageParameter);
    }

    private String extractUniqueImageParameter(
        String openAiImageUrl,
        String uniqueImageParameter // TODO: 이런 거 줄바꿈 어떻게 해야할까요??
    ) {
        URI uri = URI.create(openAiImageUrl);
        URI extractedUri = UriComponentsBuilder.fromUri(uri)
            .replaceQueryParam(uniqueImageParameter)
            .build(true)
            .toUri();
        return extractedUri.toString();
    }

    public String generateImageAndSaveToUrl(
        int count,
        User user,
        ImageMessage imageMessage
    ) {
        ImagePrompt imagePrompt = makeImagePrompt(
            imageMessage,
            openAiImageConfig.getAiModel(),
            count,
            user);
        ImageResponse imageResponse = openAiImageModel.call(imagePrompt);
        log.info("imageResponse.toString() = " + imageResponse.toString());

        return saveImageToS3Url(user, imageResponse);
    }

    public String saveImageToS3Url(
        User user,
        ImageResponse imageResponse
    ) {
        String openAiImageUrl = imageResponse.getResult().getOutput().getUrl();
        String fileNamePrefix = getFileNamePrefix(openAiImageUrl);
        String fileName = StringUtils.makeUniqueFileName(fileNamePrefix);

        return s3Service.uploadImageFromUrl(
            openAiImageConfig.getBackgroundImageFilePath(),
            openAiImageUrl,
            fileName,
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
