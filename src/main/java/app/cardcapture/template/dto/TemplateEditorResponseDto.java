package app.cardcapture.template.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class TemplateEditorResponseDto {
    private final Long templateId;
    private final String editor;
}
