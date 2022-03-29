package GameService.core.domein.service;

import GameService.core.domein.dto.GameDto;
import GameService.core.domein.model.Game;
import GameService.core.domein.model.GameStatus;
import GameService.core.domein.repository.GameRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

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

        Game game = null;

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

    public void createGame(GameDto gameCreateDto) {

        Game game = new Game();
        game.setName(gameCreateDto.getName());
        game.setStatus(GameStatus.NEW);

        logger.debug("GameService: createGame successfully done");

        gameRepository.save(game);
    }

    public HttpStatus deleteGameById(Long id) {

        Game game = null;

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

        Game game = null;

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
}
