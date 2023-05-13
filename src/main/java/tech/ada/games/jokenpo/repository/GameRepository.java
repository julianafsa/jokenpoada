package tech.ada.games.jokenpo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import tech.ada.games.jokenpo.dto.RankingDto;
import tech.ada.games.jokenpo.model.Game;
import java.util.List;

public interface GameRepository extends JpaRepository<Game, Long> {
    @Query(value = "select rownum() as ranking, subselect.* from " +
            " (select  p.NAME as name, p.USERNAME as username, count(w.game_id) as victories" +
            " from Player as p left join GAMES_WINNERS w on  p.ID = w.player_id" +
            " group by p.ID order by victories desc) as subselect", nativeQuery = true)
    List<Object> getRanking();
}
/*
@Query(value = "select rownum() as ranking, subselect.* from " +
        "(select  p.NAME, p.USERNAME, count(w.game_id) as victories " +
        "from Player as p left join GAMES_WINNERS w on  p.ID  = w.player_id " +
        "group by p.ID order by victories desc) as subselect;", nativeQuery = true)

 */
