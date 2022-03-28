package core.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PlayerDto {

    private String name;

    private Long gameId;

    public PlayerDto(Player player)
    {
        name = player.getName();
        gameId = player.getGameId();
    }
}
