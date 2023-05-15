package tech.ada.games.jokenpo.dto;

import org.junit.jupiter.api.Test;
import tech.ada.games.jokenpo.model.Move;
import tech.ada.games.jokenpo.model.Player;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
public class DtoTest {
    @Test
    void GameDtoTest() {
        assertNotNull(GameDto.builder().build());
    }

    @Test
    void ResultDtoTest() {
        assertNotNull(ResultDto.builder().build());
    }

    @Test
    void PlayerDtoTest() {
        Player player = new Player();
        player.setUsername("jogador");
        player.setName("Jogador Dos Santos");

        PlayerDto expectedDto = new PlayerDto(player);

        assertEquals(PlayerDto.builder().name("Jogador Dos Santos").username("jogador").build(), expectedDto);
    }

    @Test
    void MoveDtoTest() {
        Move move = new Move();
        move.setMove("tesoura");

        MoveDto expectedDto = new MoveDto(move);

        assertEquals(MoveDto.builder().move("tesoura").build(), expectedDto);
    }

}
