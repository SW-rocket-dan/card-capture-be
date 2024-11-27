package app.cardcapture.template.dto;

public record TemplateOpenSearchResponseDto(
    Long id,
    String userId,
    String title,
    String description,
    int likes,
    int purchaseCount,
    String editor,
    String fileUrl,
    boolean visible
) {}