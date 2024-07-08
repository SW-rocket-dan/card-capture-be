package app.cardcapture;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class ApplicationTestConfigTest {
    @Autowired
    private Environment environment;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    @Test
    public void testDbPasswordInjection() {
        // 현재 활성화된 프로파일 출력
        System.out.println("Active Profiles: " + String.join(", ", environment.getActiveProfiles()));
        // 설정 파일에서 읽어온 DB_PASSWORD 출력
        System.out.println("DB Password: " + environment.getProperty("spring.datasource.password"));

        assertThat(dbPassword).isEqualTo(System.getenv("DB_PASSWORD"));
    }
}
