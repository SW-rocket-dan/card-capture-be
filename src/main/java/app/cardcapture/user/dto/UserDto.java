package app.cardcapture.user.dto;

import app.cardcapture.user.domain.entity.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UserDto {
    @JsonProperty("id")
    private String googleId;
    private String email;
    private boolean verifiedEmail;
    private String name;
    private String givenName;
    private String familyName;
    private String picture;

    public static UserDto from(User user) {
        return new UserDto(
                user.getGoogleId(),
                user.getEmail(),
                user.isVerifiedEmail(),
                user.getName(),
                user.getGivenName(),
                user.getFamilyName(),
                user.getPicture()
        );
    }

    public User toEntity() {
        return User.builder()
                .googleId(googleId)
                .email(email)
                .verifiedEmail(verifiedEmail)
                .name(name)
                .givenName(givenName)
                .familyName(familyName)
                .picture(picture)
                .build();
    }
}
