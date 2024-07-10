package app.cardcapture.common.s3.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PresignedUrlResponseDto {
    private String presignedUrl;
    private String fileUrl;
}
