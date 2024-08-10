package app.cardcapture.ai.openai.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AiInstuctionGenerator {

    public String makeTemplateBackgroundImageInstruction(
        String purpose,
        String color,
        String phrases
    ) {
        String instruction = "an solo illustration of " + purpose + "and "
            + phrases + "." +
            "I like images that are easy to pick from the background with a thick, dark outline that encloses everything. "
            +
            "The color mood is all " + color + "." +
            //"the key word is " + prompt.phraseDetails().firstEmphasis() + " and " +prompt.phraseDetails().secondEmphasis() +
            "There shouldn't be any letters in the image." +
            "Draw a cute illustration." +
            "I want the color to be more pastel.";

        log.info("instruction = " + instruction);
        return instruction;
    }
}
