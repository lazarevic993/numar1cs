package GameService.core.domein.dto;

import GameService.core.domein.model.Game;
import GameService.core.domein.model.GameStatus;
import liquibase.pro.packaged.S;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class GameDto {

    private String name;

    private GameStatus status;

    public GameDto(Game game){
        name = game.getName();
        status = game.getStatus();
    }

}
