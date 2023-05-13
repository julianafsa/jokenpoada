package tech.ada.games.jokenpo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.ada.games.jokenpo.model.Ranking;

public interface RankingRepository extends JpaRepository<Ranking, Long> {
}
