package GameService.core.domein.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameRegistrationDto {

    private String name;

    private Long gameId;

}
