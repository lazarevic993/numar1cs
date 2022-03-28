package core.service;

import core.model.Player;
import core.model.PlayerDto;
import core.respository.PlayerRepository;
import exception.NotFoundException;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PlayerService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    private final PlayerRepository playerRepository;

    public PlayerService(PlayerRepository playerRepository) {

        this.playerRepository = playerRepository;
    }

    public PlayerDto getPlayerById(Long id) {

        Player player = playerRepository.findById(id).orElseThrow(() -> new NotFoundException("player with id " + id + " does not exist"));

        logger.debug("PlayerService: getPlayerById successfully done");

        return new PlayerDto(player);
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

    public void deletePlayerById(Long id) {

        Player player = playerRepository.findById(id).orElseThrow(() -> new NotFoundException("player with id " + id + " does not exist"));

        playerRepository.delete(player);

        logger.debug("PlayerService: deletePlayerById successfully done");
    }
}
