package app.cardcapture.ai.openai.service;

import app.cardcapture.template.dto.PromptRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.image.Image;
import org.springframework.ai.image.ImageMessage;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.openai.OpenAiImageModel;
import org.springframework.ai.openai.OpenAiImageOptions;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OpenAiImageService {
    private final OpenAiImageModel openAiImageModel;

    public Image generateImage(PromptRequestDto promptRequestDto) {
        // ImageMessage 생성
        String instruction = "give me an illustration\n" +
                "to promote "+ promptRequestDto.purpose() +
                "The background color is all white\n" +
                "\n" +
                 "'"+ String.join("," , promptRequestDto.phraseRequestDto().phrases()) + "'" + // TODO: 이 부분도 번역시킬지 AI적으로 고민
                "That's what I'm trying to promote\n" +
                //"the key word is " + promptRequestDto.phraseRequestDto().firstEmphasis() + " and " +promptRequestDto.phraseRequestDto().secondEmphasis() +
                "\n" +
                "There's no any letters in the image\n" +
                "Draw a cute illustration\n" +
                "I want the color to be more pastel";
        System.out.println("instruction = " + instruction);
        ImageMessage imageMessage = new ImageMessage(instruction);

        // ImagePrompt 생성
        OpenAiImageOptions openAiImageOptions = OpenAiImageOptions.builder()
                .withModel("dall-e-3")
                .withN(1)
                .withQuality("hd")
                .withResponseFormat("url") // b64_json은 뭘까?
                .withUser(String.valueOf(2L))
                .withWidth(1024)
                .withHeight(1024)
                .build();

        ImagePrompt imagePrompt = new ImagePrompt(List.of(imageMessage), openAiImageOptions);

        // 이미지 생성 요청
        ImageResponse response = openAiImageModel.call(imagePrompt);

        // 응답에서 이미지 URL 또는 base64 반환
        System.out.println("response.toString() = " + response.toString());
        String imageUrl = response.getResult().getOutput().getUrl();

        // TODO: 지금은 1시간 유효한 gpt의 image url을 주고있는데, 내가 직접 s3에 저장해서 url 주는 방식으로 바꾸자
        return response.getResult().getOutput();
    }
}
