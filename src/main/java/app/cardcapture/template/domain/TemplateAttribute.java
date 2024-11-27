package app.cardcapture.template.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum TemplateAttribute {
    EDITOR("editor"), TITLE("title"), DESCRIPTION("description"), FILE_URL("fileUrl");

    private final String key;
}
