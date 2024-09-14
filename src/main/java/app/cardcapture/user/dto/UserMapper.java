package app.cardcapture.user.dto;

import app.cardcapture.user.domain.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserGoogleAuthResponseDto toUserResponseDto(User user) {
        return new UserGoogleAuthResponseDto(
            user.getGoogleId(),
            user.getEmail(),
            user.isVerifiedEmail(),
            user.getName(),
            user.getGivenName(),
            user.getFamilyName(),
            user.getPicture(),
            user.getCreatedAt(),
            user.getUpdatedAt()
        );
    }

    public UserProfileResponseDto toUserProfileResponseDto(User user) {
        return new UserProfileResponseDto(
            user.getEmail(),
            user.getName(),
            user.getPicture(),
            user.getCreatedAt()
        );
    }
}
