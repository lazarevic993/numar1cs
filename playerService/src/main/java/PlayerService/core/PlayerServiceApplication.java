package PlayerService.core;

import java.util.Collections;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PlayerServiceApplication {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(PlayerServiceApplication.class);
		app.setDefaultProperties(Collections.singletonMap("server.port", "2020"));
		app.run(args);
	}
}
