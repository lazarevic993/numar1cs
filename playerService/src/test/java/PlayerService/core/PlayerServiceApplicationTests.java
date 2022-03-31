package PlayerService.core;

import static org.assertj.core.api.Assertions.assertThat;

import PlayerService.core.domain.contoller.PlayerController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class PlayerServiceApplicationTests {

	@Autowired
	private PlayerController controller;

	@Test
	void contextLoads() {

		assertThat(controller).isNotNull();
	}

}
