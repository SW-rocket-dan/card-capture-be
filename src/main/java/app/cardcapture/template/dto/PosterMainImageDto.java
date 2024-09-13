package app.cardcapture.template.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PosterMainImageDto(
    @Min(1) Long aiImageId,
    @NotBlank @Size(max = 500) String originalUrl,
    @NotBlank @Size(max = 500) String removedBackgroundUrl
){

}
