package GameService.core;

import static GameService.core.config.ApplicationUrls.REST_API_V1_GAME;
import static GameService.core.util.CommunicationUtil.createURLWithPort;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

import GameService.core.domein.dto.GameCreateDto;
import GameService.core.domein.dto.GameCreateDto.GameCreateDtoBuilder;
import GameService.core.domein.dto.GameDto;
import GameService.core.domein.dto.GameDto.GameDtoBuilder;
import GameService.core.domein.model.GameStatus;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

@Sql(scripts = {"/sql/game_before.sql"}, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = {"/sql/game_after.sql"}, executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GameControllerTest {

    @LocalServerPort
    private int port;

    TestRestTemplate testRestTemplate;

    @Autowired
    private RestTemplate restTemplate;

    HttpHeaders headers = new HttpHeaders();

    private MockRestServiceServer mockServer;
    private ObjectMapper mapper = new ObjectMapper();

    private static final ParameterizedTypeReference<List<GameDto>> responseGameDtoList =
            new ParameterizedTypeReference<List<GameDto>>() {
            };

    @Before
    public void init() {
        testRestTemplate = new TestRestTemplate();
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    public void testShouldCreateGame() throws URISyntaxException {

        GameCreateDto gameCreateDto = standardGameCreateDtoBuilder().build();
        HttpEntity<GameCreateDto> entity = new HttpEntity<>(gameCreateDto, headers);

        mockServer.expect(ExpectedCount.once(),
                          requestTo(new URI(createURLWithPort("player/registration" ))))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.CREATED)
                                    .contentType(MediaType.APPLICATION_JSON)
                );

        ResponseEntity<Void> response = testRestTemplate.exchange(
                createTestURLWithPort(REST_API_V1_GAME), HttpMethod.POST, entity, Void.class);

        assertThat(response.getStatusCodeValue()).isEqualTo(201);
    }

    @Test
    public void testShouldTryCreateGameWithPlayerServiceNotWorking() throws URISyntaxException {

        GameCreateDto gameCreateDto = standardGameCreateDtoBuilder().build();
        HttpEntity<GameCreateDto> entity = new HttpEntity<>(gameCreateDto, headers);

        mockServer.expect(ExpectedCount.once(),
                          requestTo(new URI(createURLWithPort("player/registration" ))))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                );

        ResponseEntity<Void> response = testRestTemplate.exchange(
                createTestURLWithPort(REST_API_V1_GAME), HttpMethod.POST, entity, Void.class);

        assertThat(response.getStatusCodeValue()).isEqualTo(500);
    }

    @Test
    public void testShouldGetAllGames(){

        HttpEntity<Void> entity = new HttpEntity<>(null, headers);

        ResponseEntity<List<GameDto>> response = testRestTemplate.exchange(
                createTestURLWithPort(REST_API_V1_GAME), HttpMethod.GET, entity, responseGameDtoList);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody().size()).isEqualTo(7);
    }

    @Test
    public void testShouldGetGameById(){

        HttpEntity<Void> entity = new HttpEntity<>(null, headers);

        ResponseEntity<GameDto> response = testRestTemplate.exchange(
                createTestURLWithPort(REST_API_V1_GAME) + "/1", HttpMethod.GET, entity, GameDto.class);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("game1");
        assertThat(response.getBody().getStatus()).isEqualTo(GameStatus.NEW);
    }

    @Test
    public void testShouldTryGetGameByIdWhichDoNotExist(){

        HttpEntity<Void> entity = new HttpEntity<>(null, headers);

        ResponseEntity<GameDto> response = testRestTemplate.exchange(
                createTestURLWithPort(REST_API_V1_GAME) + "/666", HttpMethod.GET, entity, GameDto.class);

        assertThat(response.getStatusCodeValue()).isEqualTo(404);
        assertThat(response.getBody()).isNull();
    }

    @Test
    public void testShouldTryDeleteGameByIdWhichDoNotExist(){

        HttpEntity<Void> entity = new HttpEntity<>(null, headers);

        ResponseEntity<GameDto> response = testRestTemplate.exchange(
                createTestURLWithPort(REST_API_V1_GAME) + "/666", HttpMethod.DELETE, entity, GameDto.class);

        assertThat(response.getStatusCodeValue()).isEqualTo(404);
    }

    @Test
    public void testShouldDeleteGameById(){

        HttpEntity<Void> entity = new HttpEntity<>(null, headers);

        ResponseEntity<GameDto> response = testRestTemplate.exchange(
                createTestURLWithPort(REST_API_V1_GAME) + "/1", HttpMethod.DELETE, entity, GameDto.class);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
    }

    @Test
    public void testShouldUpdateGameById(){

        GameDto gameDto = standardGameDtoBuilder().build();
        HttpEntity<GameDto> entity = new HttpEntity<>(gameDto, headers);

        ResponseEntity<GameDto> response = testRestTemplate.exchange(
                createTestURLWithPort(REST_API_V1_GAME) + "/1", HttpMethod.PUT, entity, GameDto.class);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo("GAME!");
        assertThat(response.getBody().getStatus()).isEqualTo(GameStatus.DROPED);
    }

    @Test
    public void testShouldTryUpdateGameByIdWhichNotExist(){

        GameDto gameDto = standardGameDtoBuilder().build();
        HttpEntity<GameDto> entity = new HttpEntity<>(gameDto, headers);

        ResponseEntity<GameDto> response = testRestTemplate.exchange(
                createTestURLWithPort(REST_API_V1_GAME) + "/666", HttpMethod.PUT, entity, GameDto.class);

        assertThat(response.getStatusCodeValue()).isEqualTo(404);
    }

    @Test
    public void testShouldGetGamesByFilterGameNameStatusPlayerName() throws URISyntaxException, JsonProcessingException {

        HttpEntity<GameDto> entity = new HttpEntity<>(null, headers);
        List<Long> gameIds = new ArrayList<>();
        gameIds.add(3L);

        mockServer.expect(ExpectedCount.once(),
                          requestTo(new URI(createURLWithPort("player/gameIds?name=ee"))))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                                    .contentType(MediaType.APPLICATION_JSON)
                            .body(mapper.writeValueAsString(gameIds)));

        ResponseEntity<List<GameDto>> response = testRestTemplate.exchange(
                createTestURLWithPort(REST_API_V1_GAME) + "/filter?gameName=GAME3&status=NEW&playerName=ee", HttpMethod.GET, entity, responseGameDtoList);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(Objects.requireNonNull(response.getBody()).size()).isEqualTo(1);
    }

    @Test
    public void testShouldGetGamesByFilterGameNameStatusPlayerNameAndPlayerServiceNotWorking() throws URISyntaxException {

        HttpEntity<GameDto> entity = new HttpEntity<>(null, headers);

        mockServer.expect(ExpectedCount.once(),
                          requestTo(new URI(createURLWithPort("player/gameIds?name=ee"))))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

        ResponseEntity<List<GameDto>> response = testRestTemplate.exchange(
                createTestURLWithPort(REST_API_V1_GAME) + "/filter?gameName=GAME3&status=NEW&playerName=ee", HttpMethod.GET, entity, responseGameDtoList);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(Objects.requireNonNull(response.getBody()).size()).isEqualTo(4);
    }

    @Test
    public void testShouldGetGamesByFilterGameNameStatus() {

        HttpEntity<GameDto> entity = new HttpEntity<>(null, headers);

        ResponseEntity<List<GameDto>> response = testRestTemplate.exchange(
                createTestURLWithPort(REST_API_V1_GAME) + "/filter?gameName=GAME3&status=NEW&playerName=", HttpMethod.GET, entity, responseGameDtoList);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(Objects.requireNonNull(response.getBody()).size()).isEqualTo(4);
    }

    @Test
    public void testShouldGetGamesByFilterGameName() {

        HttpEntity<GameDto> entity = new HttpEntity<>(null, headers);

        ResponseEntity<List<GameDto>> response = testRestTemplate.exchange(
                createTestURLWithPort(REST_API_V1_GAME) + "/filter?gameName=GAME3&status=&playerName=", HttpMethod.GET, entity, responseGameDtoList);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(Objects.requireNonNull(response.getBody()).size()).isEqualTo(5);
    }

    @Test
    public void testShouldGetGamesByFilterStatus() {

        HttpEntity<GameDto> entity = new HttpEntity<>(null, headers);

        ResponseEntity<List<GameDto>> response = testRestTemplate.exchange(
                createTestURLWithPort(REST_API_V1_GAME) + "/filter?gameName=&status=NEW&playerName=", HttpMethod.GET, entity, responseGameDtoList);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(Objects.requireNonNull(response.getBody()).size()).isEqualTo(6);
    }

    @Test
    public void testShouldGetGamesByFilterPlayerName() throws URISyntaxException, JsonProcessingException {

        HttpEntity<GameDto> entity = new HttpEntity<>(null, headers);
        List<Long> gameIds = new ArrayList<>();
        gameIds.add(3L);

        mockServer.expect(ExpectedCount.once(),
                          requestTo(new URI(createURLWithPort("player/gameIds?name=ee"))))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .body(mapper.writeValueAsString(gameIds)));

        ResponseEntity<List<GameDto>> response = testRestTemplate.exchange(
                createTestURLWithPort(REST_API_V1_GAME) + "/filter?gameName=&status=&playerName=ee", HttpMethod.GET, entity, responseGameDtoList);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(Objects.requireNonNull(response.getBody()).size()).isEqualTo(1);
    }

    public void testShouldGetGamesByFilterGameNamePlayerName() throws URISyntaxException, JsonProcessingException {

        HttpEntity<GameDto> entity = new HttpEntity<>(null, headers);
        List<Long> gameIds = new ArrayList<>();
        gameIds.add(3L);
        gameIds.add(4L);

        mockServer.expect(ExpectedCount.once(),
                          requestTo(new URI(createURLWithPort("player/gameIds?name=ee"))))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .body(mapper.writeValueAsString(gameIds)));

        ResponseEntity<List<GameDto>> response = testRestTemplate.exchange(
                createTestURLWithPort(REST_API_V1_GAME) + "/filter?gameName=GAME#&status=&playerName=ee", HttpMethod.GET, entity, responseGameDtoList);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(Objects.requireNonNull(response.getBody()).size()).isEqualTo(1);
    }

    public void testShouldGetGamesByFilterStatusPlayerName() throws URISyntaxException, JsonProcessingException {

        HttpEntity<GameDto> entity = new HttpEntity<>(null, headers);
        List<Long> gameIds = new ArrayList<>();
        gameIds.add(3L);
        gameIds.add(4L);

        mockServer.expect(ExpectedCount.once(),
                          requestTo(new URI(createURLWithPort("player/gameIds?name=ee"))))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .body(mapper.writeValueAsString(gameIds)));

        ResponseEntity<List<GameDto>> response = testRestTemplate.exchange(
                createTestURLWithPort(REST_API_V1_GAME) + "/filter?gameName=#&status=DONE&playerName=ee", HttpMethod.GET, entity, responseGameDtoList);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(Objects.requireNonNull(response.getBody()).size()).isEqualTo(0);
    }

    @Test
    public void testShouldGetGamesByFilterBadStatus() {

        HttpEntity<GameDto> entity = new HttpEntity<>(null, headers);

        ResponseEntity<List<GameDto>> response = testRestTemplate.exchange(
                createTestURLWithPort(REST_API_V1_GAME) + "/filter?gameName=&status=dfs&playerName=", HttpMethod.GET, entity, responseGameDtoList);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(Objects.requireNonNull(response.getBody()).size()).isEqualTo(7);
    }

    private String createTestURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }


    public static GameDtoBuilder standardGameDtoBuilder() {

        return GameDto.builder()
                .name("GAME!")
                .status(GameStatus.DROPED);
    }

    public static GameCreateDtoBuilder standardGameCreateDtoBuilder() {

        return GameCreateDto.builder()
                .name("GAME!")
                .playerName("ee");
    }
}
