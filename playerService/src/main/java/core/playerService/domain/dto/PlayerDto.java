package core.playerService.domain.dto;

import core.playerService.domain.model.Player;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlayerDto {

    private String name;

    private Long gameId;

    public PlayerDto(Player player)
    {
        name = player.getName();
        gameId = player.getGameId();
    }
}
