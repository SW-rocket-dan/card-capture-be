package app.cardcapture.ai.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum AiModel {
    DALL_E_3("dall-e-3"),
    STABLE_DIFFUSION("stable-diffusion");

    private final String apiName;
}
