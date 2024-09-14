package app.cardcapture.user.dto;

import java.time.LocalDateTime;

public record UserProfileResponseDto(
    String email,
    String name,
    String picture,
    LocalDateTime createdAt
) {

}
