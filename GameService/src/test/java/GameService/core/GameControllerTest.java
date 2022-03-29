package GameService.core;

import static GameService.core.config.ApplicationUrls.REST_API_V1_GAME;
import static org.assertj.core.api.Assertions.assertThat;

import GameService.core.domein.dto.GameCreateDto;
import GameService.core.domein.dto.GameCreateDto.GameCreateDtoBuilder;
import GameService.core.domein.dto.GameDto;
import GameService.core.domein.dto.GameDto.GameDtoBuilder;
import GameService.core.domein.model.GameStatus;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.context.junit4.SpringRunner;

@Sql(scripts = {"/sql/game_before.sql"}, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = {"/sql/game_after.sql"}, executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GameControllerTest {

    @LocalServerPort
    private int port;

    TestRestTemplate restTemplate = new TestRestTemplate();

    HttpHeaders headers = new HttpHeaders();

    private static final ParameterizedTypeReference<List<GameDto>> responseGameDtoList =
            new ParameterizedTypeReference<List<GameDto>>() {
            };

    @Test
    public void testShouldCreateGame(){

        GameCreateDto gameCreateDto = standardGameCreateDtoBuilder().build();
        HttpEntity<GameCreateDto> entity = new HttpEntity<GameCreateDto>(gameCreateDto, headers);

        ResponseEntity<Void> response = restTemplate.exchange(
                createURLWithPort(REST_API_V1_GAME), HttpMethod.POST, entity, Void.class);

        assertThat(response.getStatusCodeValue()).isEqualTo(201);
    }

    @Test
    public void testShouldGetAllGames(){

        HttpEntity<Void> entity = new HttpEntity<Void>(null, headers);

        ResponseEntity<List<GameDto>> response = restTemplate.exchange(
                createURLWithPort(REST_API_V1_GAME), HttpMethod.GET, entity, responseGameDtoList);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody().size()).isEqualTo(3);
    }

    @Test
    public void testShouldGetGameById(){

        HttpEntity<Void> entity = new HttpEntity<Void>(null, headers);

        ResponseEntity<GameDto> response = restTemplate.exchange(
                createURLWithPort(REST_API_V1_GAME) + "/1", HttpMethod.GET, entity, GameDto.class);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("game1");
        assertThat(response.getBody().getStatus()).isEqualTo(GameStatus.NEW);
    }

    @Test
    public void testShouldTryGetGameByIdWhichDoNotExist(){

        HttpEntity<Void> entity = new HttpEntity<Void>(null, headers);

        ResponseEntity<GameDto> response = restTemplate.exchange(
                createURLWithPort(REST_API_V1_GAME) + "/6", HttpMethod.GET, entity, GameDto.class);

        assertThat(response.getStatusCodeValue()).isEqualTo(404);
        assertThat(response.getBody()).isNull();
    }

    @Test
    public void testShouldTryDeleteGameByIdWhichDoNotExist(){

        HttpEntity<Void> entity = new HttpEntity<Void>(null, headers);

        ResponseEntity<GameDto> response = restTemplate.exchange(
                createURLWithPort(REST_API_V1_GAME) + "/6", HttpMethod.DELETE, entity, GameDto.class);

        assertThat(response.getStatusCodeValue()).isEqualTo(404);
    }

    @Test
    public void testShouldDeleteGameById(){

        HttpEntity<Void> entity = new HttpEntity<Void>(null, headers);

        ResponseEntity<GameDto> response = restTemplate.exchange(
                createURLWithPort(REST_API_V1_GAME) + "/1", HttpMethod.DELETE, entity, GameDto.class);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
    }

    @Test
    public void testShouldUpdateGameById(){

        GameDto gameDto = standardGameDtoBuilder().build();
        HttpEntity<GameDto> entity = new HttpEntity<GameDto>(gameDto, headers);

        ResponseEntity<GameDto> response = restTemplate.exchange(
                createURLWithPort(REST_API_V1_GAME) + "/1", HttpMethod.PUT, entity, GameDto.class);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("GAME!");
        assertThat(response.getBody().getStatus()).isEqualTo(GameStatus.DROPED);
    }

    @Test
    public void testShouldTryUpdateGameByIdWhichNotExist(){

        GameDto gameDto = standardGameDtoBuilder().build();
        HttpEntity<GameDto> entity = new HttpEntity<GameDto>(gameDto, headers);

        ResponseEntity<GameDto> response = restTemplate.exchange(
                createURLWithPort(REST_API_V1_GAME) + "/6", HttpMethod.PUT, entity, GameDto.class);

        assertThat(response.getStatusCodeValue()).isEqualTo(404);
    }

    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }

    public static GameDtoBuilder standardGameDtoBuilder() {

        return GameDto.builder()
                .name("GAME!")
                .status(GameStatus.DROPED);
    }

    public static GameCreateDtoBuilder standardGameCreateDtoBuilder() {

        return GameCreateDto.builder()
                .name("GAME!");
    }
}
