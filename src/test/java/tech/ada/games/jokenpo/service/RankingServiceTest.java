package tech.ada.games.jokenpo.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import tech.ada.games.jokenpo.controller.AbstractBaseTest;
import tech.ada.games.jokenpo.model.Player;
import tech.ada.games.jokenpo.model.Game;
import tech.ada.games.jokenpo.repository.GameRepository;
import tech.ada.games.jokenpo.repository.PlayerRepository;

import java.util.Optional;

import static org.mockito.Mockito.when;


// Classe de teste escrita utilizando o TDD
public class RankingServiceTest {
    RankingRepository repository = Mockito.mock(RankingRepository.class);
    PlayerRepository playerRepository = Mockito.mock(PlayerRepository.class);
    GameRepository gameRepository = Mockito.mock(GameRepository.class);
    RankingService service = Mockito.mock(RankingService.class);

    @BeforeEach
    void setUp() { }

    @AfterEach
    void tearDown() { }

    @Test
    void insertNewVictoryTest() {
        // GIVEN
        Long winnerId = 1L;
        Long gameId = 1l;
        RankingDto rankingDto = new RankingDto(winnerId, gameId);
        Player expectedWinner = new Player();
        expectedWinner.setUsername("player");
        expectedWinner.setName("Player");
        expectedWinner.setId(winnerId);
        expectedWinner.setPassword("1234");

        Game expectedGame = new Game();
        expectedGame.setId(gameId);

        Ranking expectedRanking = new Ranking();
        expectedRanking.setWinnerId(winnerId);
        expectedRanking.setGameId(gameId);

        when(playerRepository.findById(winnerId)).thenReturn(Optional.of(expectedWinner));
        when(gameRepository.findById(gameId)).thenReturn(Optional.of(expectedGame));
        when(repository.save(expectedRanking)).thenReturn(expectedRanking);

        // WHEN
        service.insertNewVictory(gameResult);

        // THEN
        verify(playerRepository, times(1)).findById(winnerId);
        verify(gameRepository, times(1)).findById(gameId);
        verify(repository, times(1)).save(ranking);
    }
}
