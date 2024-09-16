package app.cardcapture.ai.openai.service;

import app.cardcapture.ai.openai.dto.AiTranslationResponseDto;
import app.cardcapture.common.dto.ErrorCode;
import app.cardcapture.common.exception.BusinessLogicException;
import app.cardcapture.user.domain.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
@RequiredArgsConstructor
public class OpenAiTextService {

    private final OpenAiChatModel openAiChatModel;
    private final ObjectMapper jacksonObjectMapper;

    public String translateToEnglish(String text, User user) {
        OpenAiChatOptions openAiChatOptions = OpenAiChatOptions.builder()
            .withModel("gpt-4o-mini")
            .withN(1)
            .withResponseFormat(new OpenAiApi.ChatCompletionRequest.ResponseFormat("json_object"))
            .withUser(String.valueOf(user.getId()))
            .build();

        String instruction = "Translate '" + text + "' into English." +
            "output format is json and key name is translated_text." +
            "input example: \"나는 귀여워. 나는 선물을 좋아해.\". output example: { \"translated_text\": \"I'm cute. I love gifts.\" }";

        Prompt prompt = new Prompt(instruction, openAiChatOptions);
        ChatResponse response = openAiChatModel.call(prompt);
        log.info("response = " + response.toString());

        return getTranslatedText(response);
    }

    private String getTranslatedText(ChatResponse response) {
        try {
            AiTranslationResponseDto chatResponseDTO = jacksonObjectMapper.readValue(
                response.getResult().getOutput().getContent(), AiTranslationResponseDto.class);
            return chatResponseDTO.translatedText();
        } catch (IOException e) {
            log.error("Error parsing JSON response", e);
            throw new BusinessLogicException(ErrorCode.JSON_PARSING_ERROR);
        }
    }
}
