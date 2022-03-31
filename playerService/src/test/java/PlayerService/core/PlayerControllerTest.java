package PlayerService.core;

import static PlayerService.core.config.ApplicationUrls.REST_API_V1_PLAYER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

import PlayerService.core.domain.dto.PlayerDto;
import PlayerService.core.domain.dto.PlayerDto.PlayerDtoBuilder;
import PlayerService.core.util.CommunicationUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
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

@Sql(scripts = {"/sql/player_before.sql"}, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = {"/sql/player_after.sql"}, executionPhase = ExecutionPhase.AFTER_TEST_METHOD)
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PlayerControllerTest {

    @LocalServerPort
    private int port;

    TestRestTemplate testRestTemplate;

    @Autowired
    private RestTemplate restTemplate;

    HttpHeaders headers = new HttpHeaders();

    private MockRestServiceServer mockServer;
    private ObjectMapper mapper = new ObjectMapper();

    private static final ParameterizedTypeReference<List<PlayerDto>> responsePlayerDtoList =
            new ParameterizedTypeReference<>() {
            };

    private static final ParameterizedTypeReference<List<Long>> responseListLongs =
            new ParameterizedTypeReference<>() {
            };

    @Before
    public void init() {
        testRestTemplate = new TestRestTemplate();
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    public void testShouldCreatePlayer() throws URISyntaxException {

        PlayerDto playerDto = standardPlayerDtoBuilder().build();
        HttpEntity<PlayerDto> entity = new HttpEntity<>(playerDto, headers);

        mockServer.expect(ExpectedCount.once(), requestTo(new URI(CommunicationUtil.createURLWithPort("game/1"))))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                                    .contentType(MediaType.APPLICATION_JSON)
                );

        ResponseEntity<Void> response = testRestTemplate.exchange(createTestURLWithPort(REST_API_V1_PLAYER), HttpMethod.POST, entity, Void.class);

        assertThat(response.getStatusCodeValue()).isEqualTo(201);
    }

    @Test
    public void testShouldCreatePlayerWhenGameServiceNotWorking() throws URISyntaxException {

        PlayerDto playerDto = standardPlayerDtoBuilder().build();
        HttpEntity<PlayerDto> entity = new HttpEntity<>(playerDto, headers);

        mockServer.expect(ExpectedCount.once(), requestTo(new URI(CommunicationUtil.createURLWithPort("game/1"))))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                );

        ResponseEntity<Void> response = testRestTemplate.exchange(createTestURLWithPort(REST_API_V1_PLAYER), HttpMethod.POST, entity, Void.class);

        assertThat(response.getStatusCodeValue()).isEqualTo(201);
    }

    @Test
    public void testShouldCreatePlayerWhenGameNotExist() throws URISyntaxException {

        PlayerDto playerDto = standardPlayerDtoBuilder().build();
        HttpEntity<PlayerDto> entity = new HttpEntity<>(playerDto, headers);

        mockServer.expect(ExpectedCount.once(), requestTo(new URI(CommunicationUtil.createURLWithPort("game/1"))))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.NOT_FOUND)
                );

        ResponseEntity<Void> response = testRestTemplate.exchange(createTestURLWithPort(REST_API_V1_PLAYER), HttpMethod.POST, entity, Void.class);

        assertThat(response.getStatusCodeValue()).isEqualTo(201);
    }

    @Test
    public void testShouldGetAllPlayers(){

        HttpEntity<Void> entity = new HttpEntity<>(null, headers);

        ResponseEntity<List<PlayerDto>> response = testRestTemplate.exchange(
                createTestURLWithPort(REST_API_V1_PLAYER + "/all"), HttpMethod.GET, entity, responsePlayerDtoList);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody().size()).isEqualTo(5);
    }

    @Test
    public void testShouldGetAllPlayersByName(){

        HttpEntity<Void> entity = new HttpEntity<>(null, headers);

        ResponseEntity<List<PlayerDto>> response = testRestTemplate.exchange(createTestURLWithPort( REST_API_V1_PLAYER + "?name={name}"), HttpMethod.GET, entity, responsePlayerDtoList, "player1");

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody().size()).isEqualTo(3);
    }

    @Test
    public void testShouldGetAllPlayersByNameNotExist(){

        HttpEntity<Void> entity = new HttpEntity<>(null, headers);

        ResponseEntity<List<PlayerDto>> response = testRestTemplate.exchange(createTestURLWithPort( REST_API_V1_PLAYER + "?name={name}"), HttpMethod.GET, entity, responsePlayerDtoList, "fsdfsd");

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody().size()).isEqualTo(0);
    }


    @Test
    public void testShouldDeletePlayerById(){

        HttpEntity<Void> entity = new HttpEntity<>(null, headers);

        ResponseEntity<PlayerDto> response = testRestTemplate.exchange(
                createTestURLWithPort(REST_API_V1_PLAYER) + "/1", HttpMethod.DELETE, entity, PlayerDto.class);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
    }

    @Test
    public void testShouldDeletePlayerByIdNotFound(){

        HttpEntity<Void> entity = new HttpEntity<>(null, headers);

        ResponseEntity<PlayerDto> response = testRestTemplate.exchange(
                createTestURLWithPort(REST_API_V1_PLAYER) + "/66", HttpMethod.DELETE, entity, PlayerDto.class);

        assertThat(response.getStatusCodeValue()).isEqualTo(404);
    }

    @Test
    public void testShouldRegisterPlayer(){

        PlayerDto playerDto = standardPlayerDtoBuilder().build();
        HttpEntity<PlayerDto> entity = new HttpEntity<>(playerDto, headers);

        ResponseEntity<PlayerDto> response = testRestTemplate.exchange(
                createTestURLWithPort(REST_API_V1_PLAYER) + "/registration", HttpMethod.POST, entity, PlayerDto.class);

        assertThat(response.getStatusCodeValue()).isEqualTo(201);
    }

    @Test
    public void testShouldReturnPlayerAlreadyRegistered(){

        PlayerDto playerDto = standardPlayerDtoBuilder().gameId(1L).name("player1").build();
        HttpEntity<PlayerDto> entity = new HttpEntity<>(playerDto, headers);

        ResponseEntity<PlayerDto> response = testRestTemplate.exchange(
                createTestURLWithPort(REST_API_V1_PLAYER) + "/registration", HttpMethod.POST, entity, PlayerDto.class);

        assertThat(response.getStatusCodeValue()).isEqualTo(304);
    }

    @Test
    public void testShouldReturnIdsByPlayerName(){

        HttpEntity<Void> entity = new HttpEntity<>(null, headers);

        ResponseEntity<List<Long>> response = testRestTemplate.exchange(
                createTestURLWithPort( REST_API_V1_PLAYER + "/gameIds?name={name}"), HttpMethod.GET, entity, responseListLongs, "player1");

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody().size()).isEqualTo(3);
    }

    private String createTestURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }

    public static PlayerDtoBuilder standardPlayerDtoBuilder() {

        return PlayerDto.builder()
                .name("PLAYER_TEST")
                .gameId(1L);
    }
}
