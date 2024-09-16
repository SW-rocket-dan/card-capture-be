package app.cardcapture.ai.openai.service;

import app.cardcapture.ai.common.AiImage;
import app.cardcapture.ai.common.AiModel;
import app.cardcapture.ai.common.repository.AiImageRepository;
import app.cardcapture.ai.common.service.AiInstructionGenerator;
import app.cardcapture.ai.openai.dto.AiImageChangeReqeustDto;
import app.cardcapture.ai.openai.dto.AiImageChangeResponseDto;
import app.cardcapture.ai.stabilityai.service.StabilityAiImageService;
import app.cardcapture.common.dto.ErrorCode;
import app.cardcapture.common.dto.ImageDto;
import app.cardcapture.common.exception.BusinessLogicException;
import app.cardcapture.template.dto.PosterMainImageDto;
import app.cardcapture.template.dto.PromptRequestDto;
import app.cardcapture.user.domain.entity.User;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.image.ImageMessage;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OpenAiFacadeService {

    private final OpenAiImageService openAiImageService;
    private final OpenAiTextService openAiTextService;
    private final OpenAiChatModel openAiChatModel;
    private final AiInstructionGenerator aiInstructionGenerator;
    private final StabilityAiImageService stabilityAiImageService;
    private final AiImageRepository aiImageRepository;

    public PosterMainImageDto generateImage(
        PromptRequestDto promptRequestDto,
        int count,
        User user
    ) {
        ImageMessage imageMessage = makeImageMessage(promptRequestDto, user);
        ImageDto originalImage = openAiImageService.generateImageAndSave(
            count,
            promptRequestDto.model(),
            user,
            imageMessage);
        log.info("originalImage = " + originalImage.toString());

        String removedBackgroundImageUrl = stabilityAiImageService.removeBackgroundAndSaveToUrl(
            originalImage.raw(), originalImage.name(), user);

        return new PosterMainImageDto(
            originalImage.aiImageId(),
            originalImage.url(),
            removedBackgroundImageUrl);   // TODO: 이름을 어떻게 통일할지??? Template, Poster
        // TODO: 비동기(리액티브)를 하는게 좋을까요? 이유: IO가 5번 (유나, openai, s3, stability, s3) + DB IO
    } // 템플릿 요청 저장(임시 장바구니)   /  openai 보내

    private ImageMessage makeImageMessage(PromptRequestDto promptRequestDto, User user) {
        String untranslatedPurpose = promptRequestDto.purpose();
        String color = promptRequestDto.color();
        List<String> untranslatedPhrases = promptRequestDto.phraseDetails().phrases();
        String boundPhrases = String.join(", ", untranslatedPhrases);

        String purpose = openAiTextService.translateToEnglish(untranslatedPurpose, user);
        String phrases = openAiTextService.translateToEnglish(boundPhrases, user);

        String instruction = aiInstructionGenerator.makeTemplateBackgroundImageInstruction(
            purpose,
            color,
            phrases);
        ImageMessage imageMessage = new ImageMessage(instruction);
        return imageMessage;
    }

    public ImageDto generateBackgroundImage(String originalImageUrl, String message, User user) {
        // Spring ai로 현재 이미지의 prompt 불러오든, 이미지 저장할 때부터 prompt 저장해놓든 하고
        String originalImagePrompt = "기존에 이런 이미지가 있어."
            + "Create a soft, dreamy background with a gradient of pastel pink and blue. Include various sizes of translucent circles and sparkles throughout the image to give a whimsical, ethereal effect.";
        String changeRequestInstruction = originalImagePrompt + "여기서 조금 더" + message + "느낌을 더해줘";
        ImageMessage changeImageMessage = new ImageMessage(changeRequestInstruction);

        ImageDto changedImage = openAiImageService.generateImageAndSave(
            1,
            AiModel.DALL_E_3,
            user,
            changeImageMessage
        );

        return changedImage;
    }

    public AiImageChangeResponseDto changeAiImage(AiImageChangeReqeustDto aiImageChangeReqeustDto,
        User user) {
        AiImage aiImage = aiImageRepository.findById(aiImageChangeReqeustDto.aiImageId())
            .orElseThrow(
                () -> new BusinessLogicException(ErrorCode.IMAGE_RETREIVAL_FAILED));

        ImageMessage changeImageMessage = new ImageMessage(
            "기존에 이런 이미지가 있어." + aiImage.getPrompt() + "여기서 조금 더" + aiImageChangeReqeustDto.message()
                + "느낌을 더해줘");
        ImageDto changedImage = openAiImageService.generateImageAndSave(
            1,
            AiModel.DALL_E_3,
            user,
            changeImageMessage
        );

        return new AiImageChangeResponseDto(
            changedImage.aiImageId(),
            changedImage.url());
    }

    public String generateText(PromptRequestDto promptRequestDto, int count, User user) {
        PosterMainImageDto mainImage = generateImage(promptRequestDto, count, user);

        ImageDto changedBackgroundImage = generateBackgroundImage(
            "블링블링이미지url",
            List.of("초록색", "노란색", "파란색", "하얀색", "분홍색", "보라색").get(new Random().nextInt(6)),
            user);

        String instruction = aiInstructionGenerator.makeTemplateEditorTextInstruction(
            promptRequestDto,
            mainImage,
            changedBackgroundImage);

        return instruction;

        /*OpenAiChatOptions openAiChatOptions = OpenAiChatOptions.builder()
            .withModel(OpenAiApi.ChatModel.GPT_4_O)
            .withN(1)
            .withResponseFormat(new OpenAiApi.ChatCompletionRequest.ResponseFormat(
                "json_object")) // 생성 결과물이 json임을 보장합니다
            .withUser(String.valueOf(user.getId()))
            .build();

        Prompt prompt = new Prompt(instruction, openAiChatOptions);
        ChatResponse response = openAiChatModel.call(prompt);

        System.out.println("response = " + response.toString());
        String editor = response.getResult().getOutput().getContent()
            //.replace("\n", "")
            .replace("\\n", "");

        return editor;*/
    }
}
