package app.cardcapture.ai.common.service;

import app.cardcapture.common.dto.ImageDto;
import app.cardcapture.template.dto.PosterMainImageDto;
import app.cardcapture.template.dto.PromptRequestDto;
import app.cardcapture.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AiInstuctionGenerator {

    private final UserRepository userRepository;

    public String makeTemplateBackgroundImageInstruction(
        String purpose,
        String color,
        String phrases
    ) { // TODO: instruction 외부파일로 관리하는 방법?
        String instruction = "an soft pastel-colored illustration of " + purpose + "and "
            + phrases + "." +
            "I like images that are easy to pick from the background with a thick, dark outline that encloses everything. "
            +
            "The color mood is " + color + "." +
            //"the key word is " + prompt.phraseDetails().firstEmphasis() + " and " +prompt.phraseDetails().secondEmphasis() +
            "Ensure there are no text or letters in the image. If the letter is included, draw it again until it is not included. If there are invalid letters,  draw it again until it is not included."
            +
            "The style is cute and cartoonish with rounded edges and a friendly atmosphere." +
            "I want the color to be more pastel.";

        log.info("instruction = " + instruction);
        return instruction;
    }

    public String makeTemplateEditorTextInstruction(
        PromptRequestDto promptRequestDto,
        PosterMainImageDto mainImageDto,
        ImageDto changedBackgroundImage
    ) {
        String instruction = "[{\n"
            + "  \"id\": 0,\n"
            + "  \"background\": {\n"
            + "        \"imageId\": \"" + changedBackgroundImage.aiImageId() + "\",\n"
            + "    \"url\": \"" + changedBackgroundImage.url() + "\",\n"
            + "    \"opacity\": 100,\n"
            + "    \"color\": \"#ffffff\"\n"
            + "  },\n"
            + "  \"layers\": [\n"
            + "    {\n"
            + "      \"id\": 1,\n"
            + "      \"type\": \"shape\",\n"
            + "      \"content\": {\n"
            + "        \"type\": \"circle\",\n"
            + "        \"color\": \"#ffe0fd\"\n"
            + "      },\n"
            + "      \"position\": {\n"
            + "        \"x\": 73.11132812500031,\n"
            + "        \"y\": 101.8417968750002,\n"
            + "        \"width\": 395.828125,\n"
            + "        \"height\": 399.23046875,\n"
            + "        \"rotate\": 0,\n"
            + "        \"zIndex\": 2,\n"
            + "        \"opacity\": 58\n"
            + "      }\n"
            + "    },\n"
            + "    {\n"
            + "      \"id\": 2,\n"
            + "      \"type\": \"text\",\n"
            + "      \"content\": {\n"
            + "        \"content\": {\n"
            + "          \"ops\": [\n"
            + "            {\n"
            + "              \"attributes\": {\n"
            + "                \"font\": \"BlackHanSans\",\n"
            + "                \"size\": \"32px\",\n"
            + "                \"color\": \"#ba1ff7\"\n"
            + "              },\n"
            + "              \"insert\": \"" + promptRequestDto.phraseDetails().phrases().get(0) + "\"\n"
            + "            },\n"
            + "            {\n"
            + "              \"insert\": \"\\n\"\n"
            + "            },\n"
            + "            {\"attributes\":{\"align\":\"center\"},\"insert\":\"\\n\"}\n"
            + "          ]\n"
            + "        }\n"
            + "      },\n"
            + "      \"position\": {\n"
            + "        \"x\": 0,\n"
            + "        \"y\": 0,\n"
            + "        \"width\": 550,\n"
            + "        \"height\": 110,\n"
            + "        \"rotate\": 0,\n"
            + "        \"zIndex\": 5,\n"
            + "        \"opacity\": 100\n"
            + "      }\n"
            + "    },\n"
            + "    {\n"
            + "      \"id\": 3,\n"
            + "      \"type\": \"image\",\n"
            + "      \"content\": {\n"
            + "        \"imageId\": \"" + mainImageDto.aiImageId() + "\",\n"
            + "        \"url\": \"" + mainImageDto.removedBackgroundUrl() + "\",\n"
            + "      \"originalUrl\": \"" + mainImageDto.originalUrl() + "\",\n"
            + "        \"cropStartX\": 0,\n"
            + "        \"cropStartY\": 0,\n"
            + "        \"cropWidth\": 0,\n"
            + "        \"cropHeight\": 0\n"
            + "      },\n"
            + "      \"position\": {\n"
            + "        \"x\": 80.90468749999932,\n"
            + "        \"y\": 80.537500000000314,\n"
            + "        \"width\": 400.625,\n"
            + "        \"height\": 400.5234375,\n"
            + "        \"rotate\": 0,\n"
            + "        \"zIndex\": 3,\n"
            + "        \"opacity\": 100\n"
            + "      }\n"
            + "    },\n"
            + "    {\n"
            + "      \"id\": 4,\n"
            + "      \"type\": \"text\",\n"
            + "      \"content\": {\n"
            + "        \"content\": {\n"
            + "          \"ops\": [\n"
            + "            {\n"
            + "              \"attributes\": {\n"
            + "                \"size\": \"64px\",\n"
            + "                \"font\": \"BlackHanSans\",\n"
            + "                \"color\": \"#9b00ff\"\n"
            + "              },\n"
            + "              \"insert\": \"" + (promptRequestDto.phraseDetails().phrases().size() >= 2 ? promptRequestDto.phraseDetails().phrases().get(1) : "") + "\"\n"
            + "            },\n"
            + "            {\n"
            + "              \"insert\": \"\\n\"\n"
            + "            },\n"
            + "            {\"attributes\":{\"align\":\"center\"},\"insert\":\"\\n\"}\n"
            + "          ]\n"
            + "        }\n"
            + "      },\n"
            + "      \"position\": {\n"
            + "        \"x\": 0,\n"
            + "        \"y\": 420.07695312500044,\n"
            + "        \"width\": 550.16796875,\n"
            + "        \"height\": 129.875,\n"
            + "        \"rotate\": 0,\n"
            + "        \"zIndex\": 4,\n"
            + "        \"opacity\": 100\n"
            + "      }\n"
            + "    }\n"
            + "  ]\n"
            + "}]";

        log.info("instruction = " + instruction);
        return instruction;
    }
}
