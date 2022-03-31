package GameService.core.domein.service;

import GameService.core.domein.dto.GameCreateDto;
import GameService.core.domein.dto.GameDto;
import GameService.core.domein.dto.GameRegistrationDto;
import GameService.core.domein.model.Game;
import GameService.core.domein.model.GameStatus;
import GameService.core.domein.repository.GameRepository;
import GameService.core.util.CommunicationUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GameService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    private final GameRepository gameRepository;

    public GameService(GameRepository gameRepository) {

        this.gameRepository = gameRepository;
    }

    public List<GameDto> getAllGames() {

        List<Game> games = (List<Game>) gameRepository.findAll();

        logger.debug("GameService: getAllGames successfully done");

        return games.stream().map(GameDto::new).collect(Collectors.toList());
    }

    public GameDto getGameById(Long id) {

        Game game;

        if(gameRepository.findById(id).isPresent())
        {
            game = gameRepository.findById(id).get();
            logger.debug("GameService: getGameById successfully done");
            return new GameDto(game);
        }
        else
        {
            logger.debug("GameService: getGameById, game with id {} dose not exist", id);
            return null;
        }

    }

    public void createGame(GameCreateDto gameCreateDto) {

        Game game = new Game();
        game.setName(gameCreateDto.getName());
        game.setStatus(GameStatus.NEW);
        Game newGame = gameRepository.save(game);

        playerRegistration(gameCreateDto.getPlayerName(), newGame.getId());

        logger.debug("GameService: createGame successfully done");

        gameRepository.save(game);
    }

    private void playerRegistration(String playerName, Long gameId) {

        GameRegistrationDto gameRegistrationDto = new GameRegistrationDto();
        gameRegistrationDto.setGameId(gameId);
        gameRegistrationDto.setName(playerName);

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<GameRegistrationDto> entity = new HttpEntity<GameRegistrationDto>(gameRegistrationDto, headers);



        logger.debug("GameService: playerRegistration successfully sent request");
        restTemplate.exchange(CommunicationUtil.createURLWithPort("player/registration" ), HttpMethod.POST, entity, Void.class);
    }

    public HttpStatus deleteGameById(Long id) {

        Game game;

        if(gameRepository.findById(id).isPresent())
        {
            game = gameRepository.findById(id).get();
            gameRepository.delete(game);

            logger.debug("GameService: deleteGameById successfully done");
            return HttpStatus.OK;
        }
        else
        {
            logger.debug("GameService: deleteGameById, game with id {} dose not exist", id);
            return HttpStatus.NOT_FOUND;
        }
    }

    public GameDto updateGame(Long id, GameDto gameDto) {

        Game game;

        if(gameRepository.findById(id).isPresent())
        {
            game = gameRepository.findById(id).get();
            game.setName(gameDto.getName());
            game.setStatus(gameDto.getStatus());

            Game updatedGame = gameRepository.save(game);

            logger.debug("GameService: updateGame successfully done");
            return new GameDto(updatedGame);
        }
        else
        {
            logger.debug("GameService: updateGame, game with id {} dose not exist", id);
            return null;
        }

    }

    public List<GameDto> getGamesFiltered(String gameName, String stringStatus, String playerName) {

        GameStatus status;
        List<Long> gameIds = new ArrayList<>();
        List<Game> gameDtos;

        status = checkGameStatusValue(stringStatus);

        if(!playerName.isEmpty()){
            gameIds = getGameIdsByPlayerName(playerName);
        }

        gameDtos = findIntersectionGameNameGameStatus(gameName, status);

        if(!gameIds.isEmpty())
        {
            gameDtos = findIntersectionGameIdGameNameGameStatus(gameIds, gameDtos);
        }

        return gameDtos.stream().map(GameDto::new).collect(Collectors.toList());
    }

    private List<Game> findIntersectionGameNameGameStatus(String gameName, GameStatus status) {
        if(!gameName.isEmpty()){
            if(status!=null){
                return gameRepository.findAllByNameAndStatus(gameName, status);
            }
            else {
                return gameRepository.findAllByName(gameName);
            }
        }
        else {
            if(status!=null){
                return gameRepository.findAllByStatus(status);
            }
            else {
                return  (List<Game>) gameRepository.findAll();
            }
        }
    }

    private List<Game> findIntersectionGameIdGameNameGameStatus(List<Long> listIds, List<Game> gameDtoList){

        List<Long> gameIds = gameDtoList.stream().map(Game::getId).collect(Collectors.toList());
        List<Long> resultIds = gameIds.stream().distinct().filter(listIds::contains).collect(Collectors.toList());

        return gameDtoList.stream().filter(listElement -> resultIds.contains(listElement.getId())).collect(
                Collectors.toList());
    }

    private GameStatus checkGameStatusValue(String stringStatus)
    {
        if(!stringStatus.isEmpty()){
            return GameStatus.valueOf(stringStatus);
        }
        else{
            return null;
        }
    }

    private List<Long> getGameIdsByPlayerName(String playerName) {

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<Void> entity = new HttpEntity<Void>(null, headers);
        Map<String, String> params = new HashMap<>();
        params.put("name", playerName);

        ParameterizedTypeReference<List<Long>> responseList = new ParameterizedTypeReference<List<Long>>() {};

        logger.debug("GameService: getGameIdsByPlayerName successfully sent request");

        ResponseEntity<List<Long>> response = restTemplate.exchange(CommunicationUtil.createURLWithPort("player/gameIds?name={name}"), HttpMethod.GET, entity, responseList, playerName);

        return response.getBody();
    }
}
