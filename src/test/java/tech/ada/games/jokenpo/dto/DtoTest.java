package tech.ada.games.jokenpo.dto;

import org.junit.jupiter.api.Test;
import tech.ada.games.jokenpo.model.Move;
import tech.ada.games.jokenpo.model.Player;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
public class DtoTest {
    @Test
    void GameDtoTest() {
        GameDto expectedGameDto = new GameDto();
        expectedGameDto.setPlayers(Arrays.asList(1L, 2L));

        GameDto gameDtoBuilder = GameDto.builder().players(Arrays.asList(1L, 2L)). build();

        assertEquals(expectedGameDto, gameDtoBuilder);
        assertEquals("GameDto(players=[1, 2])", gameDtoBuilder.toString());
    }

    @Test
    void GameMoveDtoTest() {
        GameMoveDto expectedGameMoveDto = new GameMoveDto();
        expectedGameMoveDto.setGameId(1L);
        expectedGameMoveDto.setMoveId(1L);

        GameMoveDto gameMoveDtoBuilder = GameMoveDto.builder().gameId(1L).moveId(1L).build();

        assertEquals(expectedGameMoveDto, gameMoveDtoBuilder);
        assertEquals("GameMoveDto(gameId=1, moveId=1)", gameMoveDtoBuilder.toString());
    }

    @Test
    void ResultDtoTest() {
        ResultDto expectedResultDto = new ResultDto();
        expectedResultDto.setMoveId(1L);
        expectedResultDto.setMessage("message");

        ResultDto resultDtoBuilder = ResultDto.builder().moveId(1L).message("message").build();

        assertEquals(expectedResultDto, resultDtoBuilder);
        assertEquals("ResultDto(winners=null, moveId=1, message=message)", resultDtoBuilder.toString());
    }

    @Test
    void PlayerDtoTest() {
        Player player = new Player();
        player.setUsername("jogador");
        player.setName("Jogador Dos Santos");
        PlayerDto expectedDto = new PlayerDto(player);

        PlayerDto playerDtoBuilder = PlayerDto.builder().name("Jogador Dos Santos").username("jogador").build();

        assertEquals(expectedDto, playerDtoBuilder);
        assertEquals("PlayerDto(username=jogador, password=null, name=Jogador Dos Santos)", playerDtoBuilder.toString());
    }

    @Test
    void MoveDtoTest() {
        Move move = new Move();
        move.setMove("tesoura");
        MoveDto expectedDto = new MoveDto(move);

        MoveDto moveDtoBuilder = MoveDto.builder().move("tesoura").build();

        assertEquals(expectedDto, moveDtoBuilder);
        assertEquals("MoveDto(move=tesoura)", moveDtoBuilder.toString());
    }

    @Test
    void RankingDtoTest() {
        RankingDto expectedRankingDto = new RankingDto();
        expectedRankingDto.setRanking(1L);
        expectedRankingDto.setName("Player");
        expectedRankingDto.setUsername("player");
        expectedRankingDto.setVictories(10L);

        RankingDto rankingDto =
                RankingDto.builder().ranking(1L).name("Player").username("player").victories(10L).build();

        assertEquals(expectedRankingDto.getRanking(), rankingDto.getRanking());
        assertEquals(expectedRankingDto.getName(), rankingDto.getName());
        assertEquals(expectedRankingDto.getUsername(), rankingDto.getUsername());
        assertEquals(expectedRankingDto.getVictories(), rankingDto.getVictories());
        assertEquals("RankingDto(ranking=1, name=Player, username=player, victories=10)", rankingDto.toString());
    }

}
