package app.cardcapture.common.swagger.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties
public class SwaggerConfig {

    private String swaggerUrl;

    public SwaggerConfig(@Value("${swagger.url}") String swaggerUrl) {
        this.swaggerUrl = swaggerUrl;
    }

    @Bean
    public OpenAPI openAPI() {
        Info info = new Info()
                .version("1.0.0")
                .title("Card Capture API")
                .description("설명");

        Server server = new Server();
        server.setUrl(swaggerUrl);

        return new OpenAPI()
                .info(new Info()
                        .title("API Documentation")
                        .version("1.0.0"))
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components()
                        .addSecuritySchemes("Bearer Authentication",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }
}