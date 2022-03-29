package GameService.core.domein.repository;

import GameService.core.domein.model.Game;
import org.springframework.data.repository.CrudRepository;

public interface GameRepository extends CrudRepository<Game, Long> {

}
