package PlayerService.core.domain.respository;

import PlayerService.core.domain.model.Player;
import org.springframework.data.repository.CrudRepository;

public interface PlayerRepository extends CrudRepository<Player, Long> {

}
