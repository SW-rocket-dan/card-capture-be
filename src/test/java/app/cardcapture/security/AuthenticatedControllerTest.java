package app.cardcapture.security;

import app.cardcapture.auth.jwt.service.JwtComponent;
import app.cardcapture.security.config.SecurityConfig;
import app.cardcapture.user.domain.entity.User;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.mockito.BDDMockito.given;

@Import(SecurityConfig.class)
public abstract class AuthenticatedControllerTest {

    @MockBean
    protected JwtComponent jwtComponent;

    @MockBean
    protected PrincipleDetails principleDetails;

    @MockBean
    private PrincipleUserDetailsService principleUserDetailsService;

    protected User mockUser;

    @BeforeEach
    void initMocks() {
        MockitoAnnotations.openMocks(this);

        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setRole("ROLE_USER");
        mockUser.setEmail("test@example.com");
        mockUser.setName("Test User");
        mockUser.setPicture("http://example.com/picture.jpg");
        mockUser.setCreatedAt(LocalDateTime.now());

        given(principleDetails.getUser()).willReturn(mockUser);

        Authentication authentication = new UsernamePasswordAuthenticationToken(
            principleDetails, null, principleDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

//    protected String generateJwtToken() {
//         JWT 토큰 생성
//        return jwtComponent.createAccessToken(mockUser.getId(), mockUser.getRole(), TimeUtils.toDate(mockUser.getCreatedAt()));
//    }
}
