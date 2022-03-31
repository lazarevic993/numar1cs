package PlayerService.core.domain.service;

import PlayerService.core.domain.dto.PlayerDto;
import PlayerService.core.domain.model.Player;
import PlayerService.core.domain.respository.PlayerRepository;
import PlayerService.core.util.CommunicationUtil;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class PlayerService {

    private final RestTemplate restTemplate;

    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    private final PlayerRepository playerRepository;

    public PlayerService(PlayerRepository playerRepository, RestTemplate restTemplate) {

        this.playerRepository = playerRepository;
        this.restTemplate = restTemplate;
    }

    public List<PlayerDto> getPlayerByName(String name) {

        List<Player>  players = playerRepository.findByName(name);

        return players.stream().map(PlayerDto::new).collect(Collectors.toList());
    }

    public List<PlayerDto> getAllPlayers() {

        List<Player> players = (List<Player>) playerRepository.findAll();

        logger.debug("PlayerService: getAllPlayers successfully done");

        return players.stream().map(PlayerDto::new).collect(Collectors.toList());
    }

    public HttpStatus createPlayer(PlayerDto playerDto) {

        Player player = new Player();
        player.setName(playerDto.getName());
        player.setGameId(getGameIdToSet(playerDto));

        if(player.getGameId()==0)
        {
            Player playersWithSameName = playerRepository.findDistinctByName(playerDto.getName());

            if(playersWithSameName!=null)
            {
                return HttpStatus.BAD_REQUEST;
            }
        }

        playerRepository.save(player);

        logger.debug("PlayerService: createPlayer successfully done");

        return HttpStatus.CREATED;
    }

    public HttpStatus deletePlayerById(Long id) {

        Player player;

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

    public boolean isGameExist(Long gameId)
    {
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<Void> entity = new HttpEntity<Void>(null, headers);

        try {
            ResponseEntity<Void> response = restTemplate.exchange(CommunicationUtil.createURLWithPort("game/" + gameId), HttpMethod.GET, entity, Void.class);

            return response.getStatusCodeValue() == 200;
        }
        catch (Exception e)
        {
            return false;
        }
    }

    private Long getGameIdToSet(PlayerDto playerDto)
    {
        if(playerDto.getGameId()==null)
        {
            return 0L;
        }
        else {
           return isGameExist(playerDto.getGameId()) ? playerDto.getGameId() : 0L;
        }
    }

    public HttpStatus registerPlayer(PlayerDto playerDto) {

        List<Player> players = playerRepository.findByName(playerDto.getName());
        List<PlayerDto> playerDtos = players.stream().map(PlayerDto::new).collect(Collectors.toList());

        if(playerDtos.contains(playerDto)){
            return HttpStatus.NOT_MODIFIED;
        }
        else {
            Player player = new Player();
            player.setGameId(playerDto.getGameId());
            player.setName(playerDto.getName());

            playerRepository.save(player);

            return HttpStatus.CREATED;
        }
    }

    public List<Long> getGameIdsByPlayerName(String name) {

        List<Player> players = playerRepository.findByName(name);

        return players.stream().map(Player::getGameId).collect(Collectors.toList());
    }
}
