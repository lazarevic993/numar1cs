package core.playerService.domain.respository;

import core.playerService.domain.model.Player;
import org.springframework.data.repository.CrudRepository;

public interface PlayerRepository extends CrudRepository<Player, Long> {

}
