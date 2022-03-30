package PlayerService.core.domain.service;

import PlayerService.core.domain.dto.PlayerDto;
import PlayerService.core.domain.model.Player;
import PlayerService.core.domain.respository.PlayerRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class PlayerService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    private final PlayerRepository playerRepository;

    public PlayerService(PlayerRepository playerRepository) {

        this.playerRepository = playerRepository;
    }

    public PlayerDto getPlayerById(Long id) {

        Player player = null;

        if(playerRepository.findById(id).isPresent())
        {
            player = playerRepository.findById(id).get();
            logger.debug("PlayerService: getPlayerById successfully done");
            return new PlayerDto(player);
        }
        else
        {
            logger.debug("GameService: getGameById, game with id {} dose not exist", id);
            return null;
        }
    }

    public List<PlayerDto> getAllPlayers() {

        List<Player> players = (List<Player>) playerRepository.findAll();

        logger.debug("PlayerService: getAllPlayers successfully done");

        return players.stream().map(PlayerDto::new).collect(Collectors.toList());
    }

    public void createPlayer(PlayerDto playerDto) {

        Player player = new Player();
        player.setName(playerDto.getName());
        player.setGameId(playerDto.getGameId());

        playerRepository.save(player);

        logger.debug("PlayerService: createPlayer successfully done");
    }

    public HttpStatus deletePlayerById(Long id) {

        Player player = null;

        if(playerRepository.findById(id).isPresent())
        {
            player = playerRepository.findById(id).get();
            playerRepository.delete(player);

            logger.debug("GameService: deleteGameById successfully done");
            return HttpStatus.OK;
        }
        else
        {
            logger.debug("GameService: deleteGameById, game with id {} dose not exist", id);
            return HttpStatus.NOT_FOUND;
        }
    }

}
