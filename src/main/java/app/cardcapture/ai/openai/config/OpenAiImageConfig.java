package app.cardcapture.ai.openai.config;

import app.cardcapture.ai.common.AiModel;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "openai.image")
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Getter
@Setter
public class OpenAiImageConfig {

    private String extension;
    private String backgroundImageFilePath;
    private String uniqueImageParameter;
    private String quality;
    private String responseFormat;
    private int width;
    private int height;
    private AiModel aiModel;
}
