package app.cardcapture.template.dto;

import java.util.List;

public record TemplateSearchResponseDto(
    List<TemplateOpenSearchResponseDto> templates

) {

}
