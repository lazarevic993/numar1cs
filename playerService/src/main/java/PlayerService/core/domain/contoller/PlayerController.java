package PlayerService.core.domain.contoller;

import static PlayerService.core.config.ApplicationUrls.REST_API_V1_PLAYER;

import PlayerService.core.domain.dto.PlayerDto;
import PlayerService.core.domain.service.PlayerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(tags = "player")
@RequestMapping(value = REST_API_V1_PLAYER)
public class PlayerController {

    private final PlayerService playerService;

    public PlayerController(PlayerService playerService) {

        this.playerService = playerService;
    }

    @ApiOperation(
            value = "Get all players.",
            notes = "Return list of players .",
            tags = {"player"})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Players retrieved", response = PlayerDto.class),
            @ApiResponse(code = 500, message = "Unexpected error", response = Error.class),
            @ApiResponse(code = 400, message = "Invalid input", response = Error.class),
            @ApiResponse(code = 401, message = "You are not authorized to assign this player info", response = Error.class),
            @ApiResponse(code = 403, message = "You do not have right permissions to assign this player info", response = Error.class)
    })
    @GetMapping
    public ResponseEntity<List<PlayerDto>> getAllPlayers()
    {
        List<PlayerDto> playerDtos =  playerService.getAllPlayers();

        return new ResponseEntity<>(playerDtos, HttpStatus.OK);
    }

    @ApiOperation(
            value = "Get player by id.",
            notes = "Return player.",
            tags = {"player"})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Player retrieved", response = PlayerDto.class),
            @ApiResponse(code = 500, message = "Unexpected error", response = Error.class),
            @ApiResponse(code = 400, message = "Invalid input", response = Error.class),
            @ApiResponse(code = 401, message = "You are not authorized to assign this player info", response = Error.class),
            @ApiResponse(code = 403, message = "You do not have right permissions to assign this player info", response = Error.class)
    })
    @GetMapping(value = "/{id}")
    public ResponseEntity<PlayerDto> getPlayer(@PathVariable Long id)
    {

        PlayerDto playerDto = playerService.getPlayerById(id);

        return playerDto == null ?
                new ResponseEntity<>(null, HttpStatus.NOT_FOUND) : new ResponseEntity<>(playerDto, HttpStatus.OK);
    }

    @ApiOperation(
            value = "Create player.",
            notes = "Create player in DB.",
            tags = {"player"})
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Player created", response = PlayerDto.class),
            @ApiResponse(code = 500, message = "Unexpected error", response = Error.class),
            @ApiResponse(code = 400, message = "Invalid input", response = Error.class),
            @ApiResponse(code = 401, message = "You are not authorized to assign this player info", response = Error.class),
            @ApiResponse(code = 403, message = "You do not have right permissions to assign this player info", response = Error.class)
    })
    @PostMapping
    public ResponseEntity<HttpStatus> createPlayer(@RequestBody PlayerDto playerDto)
    {
        playerService.createPlayer(playerDto);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @ApiOperation(
            value = "Delete player by id.",
            notes = "Delete player.",
            tags = {"player"})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Player delteted", response = PlayerDto.class),
            @ApiResponse(code = 500, message = "Unexpected error", response = Error.class),
            @ApiResponse(code = 400, message = "Invalid input", response = Error.class),
            @ApiResponse(code = 401, message = "You are not authorized to assign this player info", response = Error.class),
            @ApiResponse(code = 403, message = "You do not have right permissions to assign this player info", response = Error.class)
    })
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<HttpStatus> deletePlayerById(@PathVariable Long id)
    {
        return new ResponseEntity<HttpStatus>(playerService.deletePlayerById(id));
    }


}
