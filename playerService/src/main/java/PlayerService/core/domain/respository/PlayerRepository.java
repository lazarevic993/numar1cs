package PlayerService.core.domain.respository;

import PlayerService.core.domain.model.Player;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface PlayerRepository extends CrudRepository<Player, Long> {

    List<Player> findByName(String name);
    Player findDistinctByName(String name);
}
