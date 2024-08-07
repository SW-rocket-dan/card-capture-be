package app.cardcapture.ai.openai.service;

import app.cardcapture.template.dto.PromptRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.image.Image;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class OpenAiTextService {
    private final OpenAiChatModel openAiChatModel;
    private final OpenAiImageService openAiImageService;

    public String generateText(PromptRequestDto promptRequestDto) {

        Image image = openAiImageService.generateImage(promptRequestDto);
        String imageUrl = image.getUrl();

        // Message 생성
        String instruction = "This model processes a given description from the ‘editor’ JSON structure and outputs the associated texts and images. The JSON data is organized into objects representing each element of the card news, which include fields such as ‘id’, ‘background’, ‘layers’, and their respective attributes.\n" +
                "\n" +
                "1. Data Structure Overview:\n" +
                "    • background: Contains details about the background of the card such as URL, opacity, and color.\n" +
                "    • layers: An array of objects, each representing a layer on the card. Layers can be of type ‘text’ or ‘image’, and each has properties related to content and position.\n" +
                "\n" +
                "2. Usage:\n" +
                "    • Input the JSON object from the editor.\n" +
                "    • The model will parse the JSON structure and output:\n" +
                "        • All texts associated with that card, showing the text’s content, positioning, and style details.\n" +
                "        • One image, listing the image’s URL as " +  imageUrl + ", size, and positioning within the card.\n" +
                "\n" +
                "3. Practical Example:\n" +
                "    • Provide the following JSON input:\n" +
                "      ```json\n" +
                "      {\n" +
                "        \"id\": 0,\n" +
                "        \"background\": {\n" +
                "          \"url\": \"\",\n" +
                "          \"opacity\": 100,\n" +
                "          \"color\": \"#ffffff\"\n" +
                "        },\n" +
                "        \"layers\": [\n" +
                "          {\n" +
                "            \"id\": 1,\n" +
                "            \"type\": \"text\",\n" +
                "            \"content\": {\n" +
                "              \"content\": {\n" +
                "                \"ops\": [\n" +
                "                  {\n" +
                "                    \"attributes\": {\n" +
                "                      \"font\": \"Jua\",\n" +
                "                      \"size\": \"64px\"\n" +
                "                    },\n" +
                "                    \"insert\": \"안녕하세요!\"\n" +
                "                  },\n" +
                "                  {\n" +
                "                    \"insert\": \"\\n\"\n" +
                "                  }\n" +
                "                ]\n" +
                "              }\n" +
                "            },\n" +
                "            \"position\": {\n" +
                "              \"x\": 106.94140625,\n" +
                "              \"y\": 43.537109375,\n" +
                "              \"width\": 324.41796875,\n" +
                "              \"height\": 114.875,\n" +
                "              \"rotate\": 0,\n" +
                "              \"zIndex\": 2,\n" +
                "              \"opacity\": 100\n" +
                "            }\n" +
                "          },\n" +
                "          {\n" +
                "            \"id\": 2,\n" +
                "            \"type\": \"text\",\n" +
                "            \"content\": {\n" +
                "              \"content\": {\n" +
                "                \"ops\": [\n" +
                "                  {\n" +
                "                    \"attributes\": {\n" +
                "                      \"size\": \"18px\",\n" +
                "                      \"font\": \"Jua\",\n" +
                "                      \"color\": \"#000000\"\n" +
                "                    },\n" +
                "                    \"insert\": \"현재 테스트 기간이라 AI 카드뉴스가 아닌 \"\n" +
                "                  },\n" +
                "                  {\n" +
                "                    \"attributes\": {\n" +
                "                      \"align\": \"center\"\n" +
                "                    },\n" +
                "                    \"insert\": \"\\n\"\n" +
                "                  },\n" +
                "                  {\n" +
                "                    \"attributes\": {\n" +
                "                      \"size\": \"18px\",\n" +
                "                      \"font\": \"Jua\",\n" +
                "                      \"color\": \"#000000\"\n" +
                "                    },\n" +
                "                    \"insert\": \"임시 데이터가 출력됩니다. 감사합니다\"\n" +
                "                  },\n" +
                "                  {\n" +
                "                    \"attributes\": {\n" +
                "                      \"align\": \"center\"\n" +
                "                    },\n" +
                "                    \"insert\": \"\\n\"\n" +
                "                  }\n" +
                "                ]\n" +
                "              }\n" +
                "            },\n" +
                "            \"position\": {\n" +
                "              \"x\": 93.5703125,\n" +
                "              \"y\": 136.625,\n" +
                "              \"width\": 350.44140625,\n" +
                "              \"height\": 75.109375,\n" +
                "              \"rotate\": 0,\n" +
                "              \"zIndex\": 2,\n" +
                "              \"opacity\": 100\n" +
                "            }\n" +
                "          },\n" +
                "          {\n" +
                "            \"id\": 4,\n" +
                "            \"type\": \"image\",\n" +
                "            \"content\": {\n" +
                "              \"url\": \"https://cardcaptureposterimage.s3.ap-northeast-2.amazonaws.com/test/65727aec-0e20-4fd6-932a-0d553d07280c_cat.png\",\n" +
                "              \"cropStartX\": 0,\n" +
                "              \"cropStartY\": 0,\n" +
                "              \"cropWidth\": 0,\n" +
                "              \"cropHeight\": 0\n" +
                "            },\n" +
                "            \"position\": {\n" +
                "              \"x\": 52.21484375,\n" +
                "              \"y\": 132.826171875,\n" +
                "              \"width\": 435.484375,\n" +
                "              \"height\": 421.85546875,\n" +
                "              \"rotate\": 0,\n" +
                "              \"zIndex\": 2,\n" +
                "              \"opacity\": 100\n" +
                "            }\n" +
                "          }\n" +
                "        ]\n" +
                "      }\n" +
                "      ```\n" +
                "\n" +
                "4. Data Dependencies:\n" +
                "    • Ensure that the JSON input is correctly formatted with all necessary attributes filled in as described. This is essential for the model to accurately parse and return the relevant data.\n" +
                "\n" +
                "5. Output Format:\n" +
                "    • The output will be clearly structured, presenting texts first followed by one image, each in their respective sections with details as specified in the JSON structure. The image URL will be set as \"abcd\".\n" +
                "\n" +
                "Give the texts and images when it promotes '"+  promptRequestDto.purpose() + "'" +
                "texts의 insert는" +  String.join(" / " , promptRequestDto.phraseRequestDto().phrases()) + "이렇게" +
                promptRequestDto.phraseRequestDto().phrases().size() + "가지만 있어야 해. " +
                "Please return the results in JSON format. An example of a return is as follows" +
                "6. Output Example:\n" +
                "    ```json\n" +
                "{\n" +
                "  \"id\": 0,\n" +
                "  \"background\": {\n" +
                "    \"url\": \"\",\n" +
                "    \"opacity\": 100,\n" +
                "    \"color\": \"#ffffff\"\n" +
                "  },\n" +
                "  \"layers\": [\n" +
                "    {\n" +
                "      \"id\": 1,\n" +
                "      \"type\": \"text\",\n" +
                "      \"content\": {\n" +
                "        \"content\": {\n" +
                "          \"ops\": [\n" +
                "            {\n" +
                "              \"attributes\": {\n" +
                "                \"font\": \"Jua\",\n" +
                "                \"size\": \"64px\"\n" +
                "              },\n" +
                "              \"insert\": \"첫 번째 insert\"\n" +
                "            },\n" +
                "            {\n" +
                "              \"insert\": \"\\n\"\n" +
                "            }\n" +
                "          ]\n" +
                "        }\n" +
                "      },\n" +
                "      \"position\": {\n" +
                "        \"x\": 106.94140625,\n" +
                "        \"y\": 43.537109375,\n" +
                "        \"width\": 324.41796875,\n" +
                "        \"height\": 114.875,\n" +
                "        \"rotate\": 0,\n" +
                "        \"zIndex\": 2,\n" +
                "        \"opacity\": 100\n" +
                "      }\n" +
                "    },\n" +
                "    {\n" +
                "      \"id\": 2,\n" +
                "      \"type\": \"text\",\n" +
                "      \"content\": {\n" +
                "        \"content\": {\n" +
                "          \"ops\": [\n" +
                "            {\n" +
                "              \"attributes\": {\n" +
                "                \"size\": \"18px\",\n" +
                "                \"font\": \"Jua\",\n" +
                "                \"color\": \"#000000\"\n" +
                "              },\n" +
                "              \"insert\": \"두 번째 insert가 있으면 이 id도 있다 \"\n" +
                "            },\n" +
                "            {\n" +
                "              \"attributes\": {\n" +
                "                \"align\": \"center\"\n" +
                "              },\n" +
                "              \"insert\": \"\\n\"\n" +
                "            },\n" +
                "            {\n" +
                "              \"attributes\": {\n" +
                "                \"size\": \"18px\",\n" +
                "                \"font\": \"Jua\",\n" +
                "                \"color\": \"#000000\"\n" +
                "              },\n" +
                "              \"insert\": \"세 번째 insert가 있으면 이 id도 있다\"\n" +
                "            },\n" +
                "            {\n" +
                "              \"attributes\": {\n" +
                "                \"align\": \"center\"\n" +
                "              },\n" +
                "              \"insert\": \"\\n\"\n" +
                "            }\n" +
                "          ]\n" +
                "        }\n" +
                "      },\n" +
                "      \"position\": {\n" +
                "        \"x\": 93.5703125,\n" +
                "        \"y\": 136.625,\n" +
                "        \"width\": 350.44140625,\n" +
                "        \"height\": 75.109375,\n" +
                "        \"rotate\": 0,\n" +
                "        \"zIndex\": 2,\n" +
                "        \"opacity\": 100\n" +
                "      }\n" +
                "    },\n" +
                "    {\n" +
                "      \"id\": 4,\n" +
                "      \"type\": \"image\",\n" +
                "      \"content\": {\n" +
                "        \"url\": \"" + imageUrl + "\",\n" +
                "        \"cropStartX\": 0,\n" +
                "        \"cropStartY\": 0,\n" +
                "        \"cropWidth\": 0,\n" +
                "        \"cropHeight\": 0\n" +
                "      },\n" +
                "      \"position\": {\n" +
                "        \"x\": 52.21484375,\n" +
                "        \"y\": 132.826171875,\n" +
                "        \"width\": 435.484375,\n" +
                "        \"height\": 421.85546875,\n" +
                "        \"rotate\": 0,\n" +
                "        \"zIndex\": 2,\n" +
                "        \"opacity\": 100\n" +
                "      }\n" +
                "    }\n" +
                "  ]\n" +
                "}" +
                "    ```";

        System.out.println("instruction = " + instruction);

        OpenAiChatOptions openAiChatOptions = OpenAiChatOptions.builder()
                .withModel(OpenAiApi.ChatModel.GPT_4_O)
                .withN(1)
                .withResponseFormat(new OpenAiApi.ChatCompletionRequest.ResponseFormat("json_object")) // 생성 결과물이 json임을 보장합니다
                .withUser(String.valueOf(2L))
                .build();

        Prompt prompt = new Prompt(instruction, openAiChatOptions);
        ChatResponse response = openAiChatModel.call(prompt);

        System.out.println("response = " + response.toString());
        String editor = response.getResult().getOutput().getContent().replace("\n", "");

        return editor;
    }
}
