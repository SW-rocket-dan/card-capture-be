package app.cardcapture.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper(); //.configure(Feature.); // TODO: 프로젝트 공통에서 쓸 설정을 관리하기 위해 빈으로 많이 등록해 씀. 만약 이 공통 룰을 따르지 않아야하면 객체 새로 만들어씀  예) 외부 API에 필드 추가됐는데 내 객체에는 없는 필드면 에러 안뜨게
    }
}
