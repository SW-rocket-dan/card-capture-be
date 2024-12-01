package app.cardcapture.s3.dto;


public record PresignedUrlResponseDto(
    String presignedUrl,
    String fileUrl
) {

}
