package tech.ada.games.jokenpo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import tech.ada.games.jokenpo.dto.GameDto;
import tech.ada.games.jokenpo.dto.LoginDto;
import tech.ada.games.jokenpo.dto.MoveDto;
import tech.ada.games.jokenpo.dto.PlayerDto;
import tech.ada.games.jokenpo.exception.DataConflictException;
import tech.ada.games.jokenpo.exception.DataNotFoundException;
import tech.ada.games.jokenpo.model.Move;
import tech.ada.games.jokenpo.model.Player;
import tech.ada.games.jokenpo.response.AuthResponse;
import tech.ada.games.jokenpo.service.AuthService;
import tech.ada.games.jokenpo.service.GameService;
import tech.ada.games.jokenpo.service.MoveService;
import tech.ada.games.jokenpo.service.PlayerService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@SpringBootTest
@AutoConfigureMockMvc
abstract class BaseGameTest {

    @Autowired
    protected MockMvc mvc;

    @Autowired
    protected GameService service;

    @Autowired
    protected PlayerService playerService;

    @Autowired
    protected MoveService moveService;

    @Autowired
    protected AuthService authService;

    protected void populateDatabase() {
        this.buildPlayers();
        this.buildMoves();
    }

    protected AuthResponse loginAsF1rstPlayer() {
        final LoginDto loginDto = this.buildLoginDto("player1", "1234");
        return authService.login(loginDto);
    }
    protected void buildPlayers() {
        final String playerUsername1 = "player1";
        final String playerName1 = "Player 1";
        final String playerPassword1 = "1234";
        final PlayerDto playerDto1 = this.buildPlayerDto(playerUsername1, playerName1, playerPassword1);
        this.createPlayerIfNotExists(playerDto1);
        final String playerUsername2 = "player2";
        final String playerName2 = "Player 2";
        final String playerPassword2 = "1234";
        final PlayerDto playerDto2 = this.buildPlayerDto(playerUsername2, playerName2, playerPassword2);
        this.createPlayerIfNotExists(playerDto2);
    }

    protected void buildMoves() {
        final List<String> moves = Arrays.asList("Lagarto", "Papel", "Pedra", "Spock", "Tesoura");
        for (String moveAsString : moves) {
            final MoveDto move = MoveDto.builder()
                    .move(moveAsString)
                    .build();
            this.createMove(move);
        }
    }

    protected GameDto buildGameDto(List<Long> playersId) {
        final GameDto gameDto = new GameDto();
        gameDto.setPlayers(playersId);
        return gameDto;
    }

    protected PlayerDto buildPlayerDto(final String username, final String name, final String password) {
        final PlayerDto playerDto = new PlayerDto();
        playerDto.setUsername(username);
        playerDto.setName(name);
        playerDto.setPassword(password);
        return playerDto;
    }

    private LoginDto buildLoginDto(final String username, final String password) {
        final LoginDto loginDto = new LoginDto();
        loginDto.setUsername(username);
        loginDto.setPassword(password);
        return loginDto;
    }

    protected Optional<Player> createPlayerIfNotExists(final PlayerDto playerDto) {
        final String username = playerDto.getUsername();
        try {
            final Player player = playerService.findByPlayer(username);
            return Optional.ofNullable(player);
        } catch (DataNotFoundException exception) {
            return this.createPlayer(playerDto);
        }
    }

    private Optional<Player> createPlayer(final PlayerDto playerDto) {
        try {
            playerService.createPlayer(playerDto);
        } catch (DataConflictException exception) {
            return Optional.empty();
        }
        try {
            return Optional.ofNullable(playerService.findByPlayer(playerDto.getUsername()));
        } catch (DataNotFoundException e) {
            return Optional.empty();
        }
    }

    protected Optional<Move> createMoveIfNotExists(final MoveDto moveDto) {
        try {
            final Move move = moveService.findByMove(moveDto.getMove());
            return Optional.ofNullable(move);
        } catch (DataNotFoundException exception) {
            return this.createMove(moveDto);
        }
    }

    private Optional<Move> createMove(final MoveDto moveDto) {
        try {
            moveService.createMove(moveDto);
        } catch (Exception exception) {
            return Optional.empty();
        }

        try {
            return Optional.ofNullable(moveService.findByMove(moveDto.getMove()));
        } catch (DataNotFoundException e) {
            return Optional.empty();
        }
    }

}