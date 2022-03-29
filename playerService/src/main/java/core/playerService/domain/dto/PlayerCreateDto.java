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
public class PlayerCreateDto {

    private String name;

    private Long gameId;

    private String username;

    private String password;

    public PlayerCreateDto(Player player)
    {
        name = player.getName();
        gameId = player.getGameId();
        username = player.getUsername();
        password = player.getPassword();
    }
}
