package app.cardcapture.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig { // TODO: RestClient로 변경?

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
