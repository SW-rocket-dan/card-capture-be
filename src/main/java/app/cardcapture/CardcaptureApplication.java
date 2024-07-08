package app.cardcapture;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class CardcaptureApplication {

	public static void main(String[] args) {
		SpringApplication.run(CardcaptureApplication.class, args);
	}

}
