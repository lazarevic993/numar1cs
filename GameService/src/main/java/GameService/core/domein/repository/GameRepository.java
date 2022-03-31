package GameService.core.domein.repository;

import GameService.core.domein.model.Game;
import GameService.core.domein.model.GameStatus;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface GameRepository extends CrudRepository<Game, Long> {

    List<Game> findAllByNameAndStatus(String name, GameStatus status);
    List<Game> findAllByName(String name);
    List<Game> findAllByStatus(GameStatus status);

}
