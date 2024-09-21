package app.cardcapture.user.dto;

import app.cardcapture.user.domain.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class UserMapperTest {

    private UserMapper userMapper;

    @BeforeEach
    public void setUp() {
        userMapper = new UserMapper();
    }

    @Test
    public void 유저를_UserGoogleAuthResponseDto로_매핑할_수_있다() {
        // given
        User user = new User();
        user.setGoogleId("1234578910987654321");
        user.setEmail("inpink@cardcapture.app");
        user.setVerifiedEmail(true);
        user.setName("Veronica");
        user.setGivenName("Veronica");
        user.setFamilyName("Y");
        user.setPicture("http://example.com/picture");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        // when
        UserGoogleAuthResponseDto dto = userMapper.toUserResponseDto(user);

        // then
        assertThat(dto).isNotNull();
        assertThat(dto.googleId()).isEqualTo(user.getGoogleId());
        assertThat(dto.email()).isEqualTo(user.getEmail());
        assertThat(dto.verifiedEmail()).isEqualTo(user.isVerifiedEmail());
        assertThat(dto.name()).isEqualTo(user.getName());
        assertThat(dto.givenName()).isEqualTo(user.getGivenName());
        assertThat(dto.familyName()).isEqualTo(user.getFamilyName());
        assertThat(dto.picture()).isEqualTo(user.getPicture());
        assertThat(dto.createdAt()).isEqualTo(user.getCreatedAt());
        assertThat(dto.updatedAt()).isEqualTo(user.getUpdatedAt());
    }

    @Test
    public void 유저를_UserProfileResponseDto로_매핑할_수_있다() {
        // given
        User user = new User();
        user.setEmail("inpink@cardcapture.app");
        user.setName("Veronica");
        user.setPicture("http://example.com/picture");
        user.setCreatedAt(LocalDateTime.now());

        // when
        UserProfileResponseDto dto = userMapper.toUserProfileResponseDto(user);

        // then
        assertThat(dto).isNotNull();
        assertThat(dto.email()).isEqualTo(user.getEmail());
        assertThat(dto.name()).isEqualTo(user.getName());
        assertThat(dto.picture()).isEqualTo(user.getPicture());
        assertThat(dto.createdAt()).isEqualTo(user.getCreatedAt());
    }
}