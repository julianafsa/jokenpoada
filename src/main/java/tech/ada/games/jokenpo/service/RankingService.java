package tech.ada.games.jokenpo.service;

import org.springframework.stereotype.Service;
import tech.ada.games.jokenpo.dto.RankingDto;
import tech.ada.games.jokenpo.exception.DataNotFoundException;
import tech.ada.games.jokenpo.model.Game;
import tech.ada.games.jokenpo.model.Player;
import tech.ada.games.jokenpo.model.Ranking;
import tech.ada.games.jokenpo.repository.GameRepository;
import tech.ada.games.jokenpo.repository.PlayerRepository;
import tech.ada.games.jokenpo.repository.RankingRepository;

@Service
public class RankingService {

    private RankingRepository repository;
    private PlayerRepository playerRepository;
    private GameRepository gameRepository;
    public RankingService(RankingRepository repository, PlayerRepository playerRepository, GameRepository gameRepository) {
        this.repository = repository;
        this.playerRepository = playerRepository;
        this.gameRepository = gameRepository;
    }

    public void insertNewVictory(RankingDto rankingDto) throws DataNotFoundException{

        Player player = playerRepository.findById(rankingDto.getWinnerId())
                .orElseThrow(()-> new DataNotFoundException("Player not found"));

        Game game = gameRepository.findById(rankingDto.getGameId())
                .orElseThrow(()-> new DataNotFoundException("Game not found"));



        Ranking ranking = new Ranking();
        ranking.setGame(game);
        ranking.setWinner(player);

        repository.save(ranking);
    }


}
