package app.cardcapture.security;

import app.cardcapture.auth.jwt.service.JwtComponent;
import app.cardcapture.security.config.SecurityConfig;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

@Import(SecurityConfig.class)
//@Import(TestSecurityConfig.class) // 이건 아예 security를 바꿔버리는거라 좀 그럴듯~뺴버리자
// *** SecurityConfig 파일자체가 안떠버린다. 그래서 이걸 import해주거나, BaseCOntrollertest에서
// SecurityContextHolder에 Authentication 등록해줘야 하는듯. 내가 가려는 경로가 permitall이라는 정보를 못받았으니까.. / 관련 포스팅 https://seongonion.tistory.com/149
// https://github.com/spring-projects/spring-boot/issues/31162
public abstract class EstablishedSecurityControllerTest {

    @MockBean
    protected JwtComponent jwtComponent;

    @MockBean
    private PrincipleUserDetailsService principleUserDetailsService;
}
