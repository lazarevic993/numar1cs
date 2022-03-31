package GameService.core.domein.controller;

import static GameService.core.config.ApplicationUrls.REST_API_V1_GAME;

import GameService.core.domein.dto.GameCreateDto;
import GameService.core.domein.dto.GameDto;
import GameService.core.domein.service.GameService;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(tags = "game")
@RequestMapping(value = REST_API_V1_GAME)
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {

        this.gameService = gameService;
    }

    @ApiOperation(
            value = "Get all games.",
            notes = "Return list of games.",
            tags = {"game"})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Players retrieved", response = GameDto.class),
            @ApiResponse(code = 500, message = "Unexpected error", response = Error.class),
            @ApiResponse(code = 400, message = "Invalid input", response = Error.class),
            @ApiResponse(code = 401, message = "You are not authorized to assign this player info", response = Error.class),
            @ApiResponse(code = 403, message = "You do not have right permissions to assign this player info", response = Error.class)
    })
    @GetMapping
    public ResponseEntity<List<GameDto>> getAllGames() {

        List<GameDto> gameDtos = gameService.getAllGames();

        return new ResponseEntity<List<GameDto>>(gameDtos, HttpStatus.OK);
    }

    @ApiOperation(
            value = "Get game by id.",
            notes = "Return game.",
            tags = {"game"})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Game retrieved", response = GameDto.class),
            @ApiResponse(code = 500, message = "Unexpected error", response = Error.class),
            @ApiResponse(code = 400, message = "Invalid input", response = Error.class),
            @ApiResponse(code = 401, message = "You are not authorized to assign this game info", response = Error.class),
            @ApiResponse(code = 403, message = "You do not have right permissions to assign this game info", response = Error.class)
    })
    @GetMapping(value = "/{id}")
    public ResponseEntity<GameDto> getGame(@PathVariable Long id) {

        GameDto gameDto = gameService.getGameById(id);

        return gameDto == null ?
                new ResponseEntity<>(null, HttpStatus.NOT_FOUND) : new ResponseEntity<>(gameDto, HttpStatus.OK);
    }


    @ApiOperation(
            value = "Create game.",
            notes = "Create game in DB.",
            tags = {"game"})
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Game created", response = GameDto.class),
            @ApiResponse(code = 500, message = "Unexpected error", response = Error.class),
            @ApiResponse(code = 400, message = "Invalid input", response = Error.class),
            @ApiResponse(code = 401, message = "You are not authorized to assign this game info", response = Error.class),
            @ApiResponse(code = 403, message = "You do not have right permissions to assign this game info", response = Error.class)
    })
    @PostMapping
    public ResponseEntity<HttpStatus> createGame(@RequestBody GameCreateDto gameDto)
    {
        return new ResponseEntity<>(gameService.createGame(gameDto));
    }

    @ApiOperation(
            value = "Delete game by id.",
            notes = "Delete game.",
            tags = {"game"})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Player delteted", response = HttpStatus.class),
            @ApiResponse(code = 500, message = "Unexpected error", response = Error.class),
            @ApiResponse(code = 400, message = "Invalid input", response = Error.class),
            @ApiResponse(code = 401, message = "You are not authorized to assign this player info", response = Error.class),
            @ApiResponse(code = 403, message = "You do not have right permissions to assign this player info", response = Error.class)
    })
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<HttpStatus> deletePlayerById(@PathVariable Long id)
    {
        return new ResponseEntity<>( gameService.deleteGameById(id));
    }

    @ApiOperation(
            value = "Update game by id.",
            notes = "Update game.",
            tags = {"game"})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Game updated", response = HttpStatus.class),
            @ApiResponse(code = 500, message = "Unexpected error", response = Error.class),
            @ApiResponse(code = 400, message = "Invalid input", response = Error.class),
            @ApiResponse(code = 401, message = "You are not authorized to assign this game info", response = Error.class),
            @ApiResponse(code = 403, message = "You do not have right permissions to assign this game info", response = Error.class)
    })
    @PutMapping(value = "/{id}")
    public ResponseEntity<GameDto> updateGameById(@PathVariable Long id, @RequestBody GameDto gameDto)
    {

        GameDto updatedGameDto = gameService.updateGame(id, gameDto);

        return updatedGameDto == null ?
                new ResponseEntity<>(null, HttpStatus.NOT_FOUND) : new ResponseEntity<>(updatedGameDto, HttpStatus.OK);
    }


    @ApiOperation(
            value = "Get game filtered by game name, status and player name.",
            notes = "Return games.",
            tags = {"game"})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Games retrieved", response = GameDto.class),
            @ApiResponse(code = 500, message = "Unexpected error", response = Error.class),
            @ApiResponse(code = 400, message = "Invalid input", response = Error.class),
            @ApiResponse(code = 401, message = "You are not authorized to assign this game info", response = Error.class),
            @ApiResponse(code = 403, message = "You do not have right permissions to assign this game info", response = Error.class)
    })
    @GetMapping(value = "/filter")
    public ResponseEntity<List<GameDto>> getGameByFilter(@RequestParam String gameName, @RequestParam String status, @RequestParam String playerName) {

        List<GameDto> gameDtos = gameService.getGamesFiltered(gameName, status, playerName);

        return new ResponseEntity<>(gameDtos, HttpStatus.OK);
    }
}
