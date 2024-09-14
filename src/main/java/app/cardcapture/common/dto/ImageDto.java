package app.cardcapture.common.dto;

public record ImageDto(
    String path,
    String name,
    String url,
    byte[] raw,
    String prompt,
    Long aiImageId
){

}
