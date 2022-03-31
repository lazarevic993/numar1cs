package PlayerService.core;

import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

public class TestMicroServiceApplication {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
