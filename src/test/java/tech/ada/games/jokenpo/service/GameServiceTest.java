package tech.ada.games.jokenpo.service;

import jakarta.persistence.Tuple;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import tech.ada.games.jokenpo.dto.GameDto;
import tech.ada.games.jokenpo.dto.GameMoveDto;
import tech.ada.games.jokenpo.dto.RankingDto;
import tech.ada.games.jokenpo.dto.ResultDto;
import tech.ada.games.jokenpo.exception.BadRequestException;
import tech.ada.games.jokenpo.exception.DataConflictException;
import tech.ada.games.jokenpo.exception.DataNotFoundException;
import tech.ada.games.jokenpo.model.Game;
import tech.ada.games.jokenpo.model.Move;
import tech.ada.games.jokenpo.model.Player;
import tech.ada.games.jokenpo.model.PlayerMove;
import tech.ada.games.jokenpo.repository.GameRepository;
import tech.ada.games.jokenpo.repository.MoveRepository;
import tech.ada.games.jokenpo.repository.PlayerMoveRepository;
import tech.ada.games.jokenpo.repository.PlayerRepository;
import tech.ada.games.jokenpo.security.SecurityUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class GameServiceTest extends AbstractServiceTest {

    private GameRepository gameRepository;
    private PlayerMoveRepository playerMoveRepository;
    private MoveRepository moveRepository;
    private PlayerRepository playerRepository;
    private GameService service;
    private MockedStatic<SecurityUtils> mockStatic;

    @BeforeEach
    void setUp() {
        this.gameRepository = mock(GameRepository.class);
        this.playerMoveRepository = mock(PlayerMoveRepository.class);
        this.moveRepository = mock(MoveRepository.class);
        this.playerRepository = mock(PlayerRepository.class);
        this.service = new GameService(gameRepository, playerMoveRepository, moveRepository, playerRepository);
        this.mockStatic = Mockito.mockStatic(SecurityUtils.class);
    }

    @AfterEach
    void tearDown() {
        this.mockStatic.close();
    }

    @Test
    void createGameSuccessTest() {
        // Given (Arrange)
        final GameDto gameDto = this.buildGameDto();
        final String playerUsername1 = "player1";
        final String playerUsername2 = "player2";
        final Player player1 = this.buildPlayer(1L, playerUsername1, "Player 1");
        final Player player2 = this.buildPlayer(2L, playerUsername2, " Player 2");
        final Game game = this.buildGame(player1);
        mockStatic.when(SecurityUtils::getCurrentUserLogin)
                .thenReturn(playerUsername1);
        when(playerRepository.findByUsername(playerUsername1))
                .thenReturn(Optional.of(player1));
        when(playerRepository.findById(1L))
                .thenReturn(Optional.of(player1));
        when(playerRepository.findById(2L))
                .thenReturn(Optional.of(player2));
        when(gameRepository.save(any()))
                .thenReturn(game);

        // When (Act)
        assertDoesNotThrow(() -> service.newGame(gameDto));

        // Then (Assert)
        verify(playerRepository, times(1)).findByUsername(playerUsername1);
        verify(gameRepository, times(1)).save(any());
        verify(playerRepository, times(1)).findById(1L);
        verify(playerRepository, times(1)).findById(2L);
    }

    @Test
    void createGameWithNotRegistredCurrentPlayerShouldThrowDataNotFoundExceptionTest() {
        // Given (Arrange)
        final GameDto gameDto = this.buildGameDto();
        final String playerUsername1 = "player1";
        final String playerUsername2 = "player2";
        final Player player1 = this.buildPlayer(1L, playerUsername1, "Player 1");
        final Player player2 = this.buildPlayer(2L, playerUsername2, " Player 2");
        final Game game = this.buildGame(player1);
        mockStatic.when(SecurityUtils::getCurrentUserLogin)
                .thenReturn(playerUsername1);
        when(playerRepository.findByUsername(playerUsername1))
                .thenReturn(Optional.empty());
        when(playerRepository.findById(1L))
                .thenReturn(Optional.of(player1));
        when(playerRepository.findById(2L))
                .thenReturn(Optional.of(player2));
        when(gameRepository.save(any()))
                .thenReturn(game);

        // When (Act)
        final DataNotFoundException exception = assertThrows(DataNotFoundException.class , () -> service.newGame(gameDto));

        // Then (Assert)
        assertEquals("O jogador não está cadastrado!", exception.getMessage());
        verify(playerRepository, times(1)).findByUsername(playerUsername1);
        verify(gameRepository, times(0)).save(any());
        verify(playerRepository, times(0)).findById(1L);
        verify(playerRepository, times(0)).findById(2L);
    }

    @Test
    void createGameWithLessOf2PlayersShouldThrowBadRequestExceptionTest() {
        // Given (Arrange)
        final String playerUsername1 = "player1";
        final Player player1 = this.buildPlayer(1L, playerUsername1, "Player 1");
        final GameDto gameDto = this.buildGameDto(List.of(1L)); // Only 1 player
        mockStatic.when(SecurityUtils::getCurrentUserLogin)
                .thenReturn(playerUsername1);
        when(playerRepository.findByUsername(playerUsername1))
                .thenReturn(Optional.of(player1));

        // When (Act)
        final BadRequestException exception = assertThrows(BadRequestException.class , () -> service.newGame(gameDto));

        // Then (Assert)
        assertEquals("O jogo possui menos que dois jogadores!", exception.getMessage());
        verify(playerRepository, times(1)).findByUsername(playerUsername1);
        verify(gameRepository, times(0)).save(any());
        verify(playerRepository, times(0)).findById(1L);
        verify(playerRepository, times(0)).findById(2L);
    }

    @Test
    void createGameWithNotRegistredPlayerShouldThrowDataNotFoundExceptionTest() {
        // Given (Arrange)
        final String playerUsername1 = "player1";
        final Player player1 = this.buildPlayer(1L, playerUsername1, "Player 1");
        final Game game = this.buildGame(player1);
        final GameDto gameDto = this.buildGameDto(Arrays.asList(1L, 2L));
        mockStatic.when(SecurityUtils::getCurrentUserLogin)
                .thenReturn(playerUsername1);
        when(playerRepository.findByUsername(playerUsername1))
                .thenReturn(Optional.of(player1));
        when(playerRepository.findById(1L))
                .thenReturn(Optional.of(player1));
        when(playerRepository.findById(2L)) // Not registred
                .thenReturn(Optional.empty());
        when(gameRepository.save(any()))
                .thenReturn(game);

        // When (Act)
        final DataNotFoundException exception = assertThrows(DataNotFoundException.class , () -> service.newGame(gameDto));

        // Then (Assert)
        assertEquals("O jogador não está cadastrado!", exception.getMessage());
        verify(playerRepository, times(1)).findByUsername(playerUsername1);
        verify(gameRepository, times(1)).save(any());
        verify(playerRepository, times(1)).findById(1L);
        verify(playerRepository, times(1)).findById(2L);
    }

    @Test
    void insertPlayerMoveTest() throws DataNotFoundException, DataConflictException, BadRequestException {
        // Given (Arrange)
        final Long gameId = 1L;
        final Long moveId = 1L;
        final GameMoveDto gameMoveDto = new GameMoveDto(gameId, moveId);
        final String playerUsername1 = "player1";
        final Player player = this.buildPlayer(1L, playerUsername1, "Player");
        final Game game = this.buildGame(player);
        final String moveName = "Tesoura";
        final Move move = this.buildMove(moveId, moveName);
        final PlayerMove playerMove = this.buildPlayerMove(1L, game, player);

        mockStatic.when(SecurityUtils::getCurrentUserLogin).thenReturn(playerUsername1);
        when(playerRepository.findByUsername(playerUsername1)).thenReturn(Optional.of(player));
        when(gameRepository.findById(gameId)).thenReturn(Optional.of(game));
        when(moveRepository.findById(gameMoveDto.getMoveId())).thenReturn(Optional.of(move));
        when(playerMoveRepository.findByUnfinishedGameIdAndPlayer(player.getId(), gameMoveDto.getGameId())).thenReturn(Optional.of(playerMove));
        when(playerMoveRepository.save(playerMove)).thenReturn(playerMove);
        when(playerMoveRepository.countMovesPlayedByUnfinishedGame(game.getId())).thenReturn(1L);
        when(playerMoveRepository.countByUnfinishedGameId(game.getId())).thenReturn(2L);

        // When (Act)
        final ResultDto resultDto = service.insertPlayerMove(gameMoveDto);

        // Then (Assert)
        assertNotNull(resultDto);
        assertEquals("Jogada realizada! Faltam 1 jogadores para finalizar o jogo!", resultDto.getMessage());
    }

    @Test
    void findGamesShouldReturnListOfGamesTest() {
        // Given (Arrange)
        final List<Game> expectedResponse = List.of(new Game());
        when(gameRepository.findAll()).thenReturn(expectedResponse);

        // When (Act)
        List<Game> response = service.findGames();

        // Then (Assert)
        assertEquals(expectedResponse.size(), response.size(), "The size of the lists are not equal.");
    }

    @Test
    void findGameByIdShouldReturnGameTest() throws DataNotFoundException {
        // Given (Arrange)
        final Long id = 1L;
        final Player player = this.buildPlayer(id, "player", "Player");
        final Game expectedResponse = this.buildGame(player);
        when(gameRepository.findById(id)).thenReturn(Optional.of(expectedResponse));

        // When (Act)
        final Game response = service.findGameById(id);

        // Then (Assert)
        assertEquals(expectedResponse.getId(), response.getId(), "Game id is not as expected.");
    }

    @Test
    void findGameByIdWithNotRegistredGameShouldThrowsDataNotFoundExceptionTest() {
        // Given (Arrange)
        final Long id = 1L;

        // When (Act)
        final DataNotFoundException exception = assertThrows(
                DataNotFoundException.class, () -> service.findGameById(id));

        // Then (Assert)
        assertEquals("Este jogo não está cadastrado!", exception.getMessage(),  "Message exception is not as expected.");
        verify(gameRepository, times(1)).findById(1L);
    }


    @Test
    void insertPlayerMoveWithSuccessTest() throws DataNotFoundException, DataConflictException, BadRequestException {
        // Given (Arrange)
        final Long id = 1L;
        final Player player = this.buildPlayer(id, "player", "Player");
        final Game expectedResponse = this.buildGame(player);
        final Move expectedMove = this.buildMove(1L, "tesoura");
        final PlayerMove expectedPlayerMove = this.buildPlayerMove(1L, expectedResponse, player);
        final Long expectedCountMovesPlayed = 1L;
        final Long expectedCountMovesTotal = 2L;
        when(gameRepository.findById(id)).thenReturn(Optional.of(expectedResponse));
        when(moveRepository.findById(any())).thenReturn(Optional.of(expectedMove));
        when(playerMoveRepository.findByUnfinishedGameIdAndPlayer(any(), any())).thenReturn(Optional.of(expectedPlayerMove));
        when(playerMoveRepository.countMovesPlayedByUnfinishedGame(any())).thenReturn(expectedCountMovesPlayed);
        when(playerMoveRepository.countByUnfinishedGameId(any())).thenReturn(expectedCountMovesTotal);
        when(playerRepository.findByUsername(any())).thenReturn(Optional.of(player));

        // When (Act)
        final ResultDto resultDto = service.insertPlayerMove(buildGameMoveDTO());

        // Then (Assert)
        assertNotNull(resultDto);
        assertEquals("Jogada realizada! Faltam 1 jogadores para finalizar o jogo!", resultDto.getMessage());
    }

    @Test
    void insertPlayerMoveWithSuccessFinishingSpockWinsGameTest() throws DataNotFoundException, DataConflictException, BadRequestException {
        // Given (Arrange)
        final Long id = 1L;
        final Player player = this.buildPlayer(id, "player", "Player");
        final Game expectedResponse = this.buildGame(player);
        final Move expectedMove = this.buildMove(1L, "spock");
        final PlayerMove expectedPlayerMove = this.buildPlayerMove(1L, expectedResponse, player);
        final Long expectedCountMovesPlayed = 2L;
        final Long expectedCountMovesTotal = 2L;

        final boolean expectedIsSpock = true;
        final boolean expectedIsTesoura = true;
        final boolean expectedIsPapel = false;
        final boolean expectedIsPedra = false;
        final boolean expectedIsLagarto = false;

        when(gameRepository.findById(id)).thenReturn(Optional.of(expectedResponse));
        when(moveRepository.findById(any())).thenReturn(Optional.of(expectedMove));
        when(playerMoveRepository.findByUnfinishedGameIdAndPlayer(any(), any())).thenReturn(Optional.of(expectedPlayerMove));
        when(playerMoveRepository.countMovesPlayedByUnfinishedGame(any())).thenReturn(expectedCountMovesPlayed);
        when(playerMoveRepository.countByUnfinishedGameId(any())).thenReturn(expectedCountMovesTotal);
        when(playerRepository.findByUsername(any())).thenReturn(Optional.of(player));
        when(playerMoveRepository.existsSpockByUnfinishedGameId(any())).thenReturn(expectedIsSpock);
        when(playerMoveRepository.existsTesouraByUnfinishedGameId(any())).thenReturn(expectedIsTesoura);
        when(playerMoveRepository.existsPapelByUnfinishedGameId(any())).thenReturn(expectedIsPapel);
        when(playerMoveRepository.existsPedraByUnfinishedGameId(any())).thenReturn(expectedIsPedra);
        when(playerMoveRepository.existsLagartoByUnfinishedGameId(any())).thenReturn(expectedIsLagarto);
        when(playerMoveRepository.findByUnfinishedGameId(any(), any())).thenReturn(List.of(expectedPlayerMove));

        // When (Act)
        var result = service.insertPlayerMove(buildGameMoveDTO()).getMessage();

        // Then (Assert)
        assertEquals("Vencedor: Player", result);
    }

    @Test
    void insertPlayerMoveWithSuccessFinishingTesouraWinsGameTest() throws DataNotFoundException, DataConflictException, BadRequestException {
        // Given (Arrange)
        final Long id = 1L;
        final Player player = this.buildPlayer(id, "player", "Player");
        final Game expectedResponse = this.buildGame(player);
        final Move expectedMove = this.buildMove(1L, "tesoura");
        final PlayerMove expectedPlayerMove = this.buildPlayerMove(1L, expectedResponse, player);
        final Long expectedCountMovesPlayed = 2L;
        final Long expectedCountMovesTotal = 2L;

        final boolean expectedIsSpock = false;
        final boolean expectedIsTesoura = true;
        final boolean expectedIsPapel = true;
        final boolean expectedIsPedra = false;
        final boolean expectedIsLagarto = false;

        when(gameRepository.findById(id)).thenReturn(Optional.of(expectedResponse));
        when(moveRepository.findById(any())).thenReturn(Optional.of(expectedMove));
        when(playerMoveRepository.findByUnfinishedGameIdAndPlayer(any(), any())).thenReturn(Optional.of(expectedPlayerMove));
        when(playerMoveRepository.countMovesPlayedByUnfinishedGame(any())).thenReturn(expectedCountMovesPlayed);
        when(playerMoveRepository.countByUnfinishedGameId(any())).thenReturn(expectedCountMovesTotal);
        when(playerRepository.findByUsername(any())).thenReturn(Optional.of(player));
        when(playerMoveRepository.existsSpockByUnfinishedGameId(any())).thenReturn(expectedIsSpock);
        when(playerMoveRepository.existsTesouraByUnfinishedGameId(any())).thenReturn(expectedIsTesoura);
        when(playerMoveRepository.existsPapelByUnfinishedGameId(any())).thenReturn(expectedIsPapel);
        when(playerMoveRepository.existsPedraByUnfinishedGameId(any())).thenReturn(expectedIsPedra);
        when(playerMoveRepository.existsLagartoByUnfinishedGameId(any())).thenReturn(expectedIsLagarto);
        when(playerMoveRepository.findByUnfinishedGameId(any(), any())).thenReturn(List.of(expectedPlayerMove));

        // When (Act)
        var result = service.insertPlayerMove(buildGameMoveDTO()).getMessage();

        // Then (Assert)
        assertEquals("Vencedor: Player", result);
    }

    @Test
    void insertPlayerMoveWithSuccessFinishingPedraWinsGameTest() throws DataNotFoundException, DataConflictException, BadRequestException {
        // Given (Arrange)
        final Long id = 1L;
        final Player player = this.buildPlayer(id, "player", "Player");
        final Game expectedResponse = this.buildGame(player);
        final Move expectedMove = this.buildMove(1L, "pedra");
        final PlayerMove expectedPlayerMove = this.buildPlayerMove(1L, expectedResponse, player);
        final Long expectedCountMovesPlayed = 2L;
        final Long expectedCountMovesTotal = 2L;

        final boolean expectedIsSpock = false;
        final boolean expectedIsTesoura = true;
        final boolean expectedIsPapel = false;
        final boolean expectedIsPedra = true;
        final boolean expectedIsLagarto = false;

        when(gameRepository.findById(id)).thenReturn(Optional.of(expectedResponse));
        when(moveRepository.findById(any())).thenReturn(Optional.of(expectedMove));
        when(playerMoveRepository.findByUnfinishedGameIdAndPlayer(any(), any())).thenReturn(Optional.of(expectedPlayerMove));
        when(playerMoveRepository.countMovesPlayedByUnfinishedGame(any())).thenReturn(expectedCountMovesPlayed);
        when(playerMoveRepository.countByUnfinishedGameId(any())).thenReturn(expectedCountMovesTotal);
        when(playerRepository.findByUsername(any())).thenReturn(Optional.of(player));
        when(playerMoveRepository.existsSpockByUnfinishedGameId(any())).thenReturn(expectedIsSpock);
        when(playerMoveRepository.existsTesouraByUnfinishedGameId(any())).thenReturn(expectedIsTesoura);
        when(playerMoveRepository.existsPapelByUnfinishedGameId(any())).thenReturn(expectedIsPapel);
        when(playerMoveRepository.existsPedraByUnfinishedGameId(any())).thenReturn(expectedIsPedra);
        when(playerMoveRepository.existsLagartoByUnfinishedGameId(any())).thenReturn(expectedIsLagarto);
        when(playerMoveRepository.findByUnfinishedGameId(any(), any())).thenReturn(List.of(expectedPlayerMove));

        // When (Act)
        var result = service.insertPlayerMove(buildGameMoveDTO()).getMessage();

        // Then (Assert)
        assertEquals("Vencedor: Player", result);
    }

    @Test
    void insertPlayerMoveWithSuccessFinishingPapelWinsGameTest() throws DataNotFoundException, DataConflictException, BadRequestException {
        // Given (Arrange)
        final Long id = 1L;
        final Player player = this.buildPlayer(id, "player", "Player");
        final Game expectedResponse = this.buildGame(player);
        final Move expectedMove = this.buildMove(1L, "papel");
        final PlayerMove expectedPlayerMove = this.buildPlayerMove(1L, expectedResponse, player);
        final Long expectedCountMovesPlayed = 2L;
        final Long expectedCountMovesTotal = 2L;

        final boolean expectedIsSpock = false;
        final boolean expectedIsTesoura = false;
        final boolean expectedIsPapel = true;
        final boolean expectedIsPedra = true;
        final boolean expectedIsLagarto = false;

        when(gameRepository.findById(id)).thenReturn(Optional.of(expectedResponse));
        when(moveRepository.findById(any())).thenReturn(Optional.of(expectedMove));
        when(playerMoveRepository.findByUnfinishedGameIdAndPlayer(any(), any())).thenReturn(Optional.of(expectedPlayerMove));
        when(playerMoveRepository.countMovesPlayedByUnfinishedGame(any())).thenReturn(expectedCountMovesPlayed);
        when(playerMoveRepository.countByUnfinishedGameId(any())).thenReturn(expectedCountMovesTotal);
        when(playerRepository.findByUsername(any())).thenReturn(Optional.of(player));
        when(playerMoveRepository.existsSpockByUnfinishedGameId(any())).thenReturn(expectedIsSpock);
        when(playerMoveRepository.existsTesouraByUnfinishedGameId(any())).thenReturn(expectedIsTesoura);
        when(playerMoveRepository.existsPapelByUnfinishedGameId(any())).thenReturn(expectedIsPapel);
        when(playerMoveRepository.existsPedraByUnfinishedGameId(any())).thenReturn(expectedIsPedra);
        when(playerMoveRepository.existsLagartoByUnfinishedGameId(any())).thenReturn(expectedIsLagarto);
        when(playerMoveRepository.findByUnfinishedGameId(any(), any())).thenReturn(List.of(expectedPlayerMove));

        // When (Act)
        var result = service.insertPlayerMove(buildGameMoveDTO()).getMessage();

        // Then (Assert)
        assertEquals("Vencedor: Player", result);
    }

    @Test
    void insertPlayerMoveWithSuccessFinishingLagartoWinsGameTest() throws DataNotFoundException, DataConflictException, BadRequestException {
        // Given (Arrange)
        final Long id = 1L;
        final Player player = this.buildPlayer(id, "player", "Player");
        final Game expectedResponse = this.buildGame(player);
        final Move expectedMove = this.buildMove(1L, "lagarto");
        final PlayerMove expectedPlayerMove = this.buildPlayerMove(1L, expectedResponse, player);
        final Long expectedCountMovesPlayed = 2L;
        final Long expectedCountMovesTotal = 2L;

        final boolean expectedIsSpock = false;
        final boolean expectedIsTesoura = false;
        final boolean expectedIsPapel = true;
        final boolean expectedIsPedra = false;
        final boolean expectedIsLagarto = true;

        when(gameRepository.findById(id)).thenReturn(Optional.of(expectedResponse));
        when(moveRepository.findById(any())).thenReturn(Optional.of(expectedMove));
        when(playerMoveRepository.findByUnfinishedGameIdAndPlayer(any(), any())).thenReturn(Optional.of(expectedPlayerMove));
        when(playerMoveRepository.countMovesPlayedByUnfinishedGame(any())).thenReturn(expectedCountMovesPlayed);
        when(playerMoveRepository.countByUnfinishedGameId(any())).thenReturn(expectedCountMovesTotal);
        when(playerRepository.findByUsername(any())).thenReturn(Optional.of(player));
        when(playerMoveRepository.existsSpockByUnfinishedGameId(any())).thenReturn(expectedIsSpock);
        when(playerMoveRepository.existsTesouraByUnfinishedGameId(any())).thenReturn(expectedIsTesoura);
        when(playerMoveRepository.existsPapelByUnfinishedGameId(any())).thenReturn(expectedIsPapel);
        when(playerMoveRepository.existsPedraByUnfinishedGameId(any())).thenReturn(expectedIsPedra);
        when(playerMoveRepository.existsLagartoByUnfinishedGameId(any())).thenReturn(expectedIsLagarto);
        when(playerMoveRepository.findByUnfinishedGameId(any(), any())).thenReturn(List.of(expectedPlayerMove));

        // When (Act)
        var result = service.insertPlayerMove(buildGameMoveDTO()).getMessage();

        // Then (Assert)
        assertEquals("Vencedor: Player", result);
    }

    @Test
    void insertPlayerMoveWithSuccessFinishingEmpateWinsGameTest() throws DataNotFoundException, DataConflictException, BadRequestException {
        // Given (Arrange)
        final Long id = 1L;
        final Player player = this.buildPlayer(id, "player", "Player");
        final Game expectedResponse = this.buildGame(player);
        final Move expectedMove = this.buildMove(1L, "spock");
        final PlayerMove expectedPlayerMove = this.buildPlayerMove(1L, expectedResponse, player);
        final Long expectedCountMovesPlayed = 2L;
        final Long expectedCountMovesTotal = 2L;

        final boolean expectedIsSpock = true;
        final boolean expectedIsTesoura = false;
        final boolean expectedIsPapel = false;
        final boolean expectedIsPedra = false;
        final boolean expectedIsLagarto = false;

        when(gameRepository.findById(id)).thenReturn(Optional.of(expectedResponse));
        when(moveRepository.findById(any())).thenReturn(Optional.of(expectedMove));
        when(playerMoveRepository.findByUnfinishedGameIdAndPlayer(any(), any())).thenReturn(Optional.of(expectedPlayerMove));
        when(playerMoveRepository.countMovesPlayedByUnfinishedGame(any())).thenReturn(expectedCountMovesPlayed);
        when(playerMoveRepository.countByUnfinishedGameId(any())).thenReturn(expectedCountMovesTotal);
        when(playerRepository.findByUsername(any())).thenReturn(Optional.of(player));
        when(playerMoveRepository.existsSpockByUnfinishedGameId(any())).thenReturn(expectedIsSpock);
        when(playerMoveRepository.existsTesouraByUnfinishedGameId(any())).thenReturn(expectedIsTesoura);
        when(playerMoveRepository.existsPapelByUnfinishedGameId(any())).thenReturn(expectedIsPapel);
        when(playerMoveRepository.existsPedraByUnfinishedGameId(any())).thenReturn(expectedIsPedra);
        when(playerMoveRepository.existsLagartoByUnfinishedGameId(any())).thenReturn(expectedIsLagarto);
        when(playerMoveRepository.findByUnfinishedGameId(any(), any())).thenReturn(List.of(expectedPlayerMove));

        // When (Act)
        var result = service.insertPlayerMove(buildGameMoveDTO()).getMessage();

        // Then (Assert)
        assertEquals("Resultado Empate", result);
    }

    @Test
    void insertPlayerMoveWithSuccessFinishingWithMoreThanOneWinnerGameTest() throws DataNotFoundException, DataConflictException, BadRequestException {
        // Given (Arrange)
        final Long id = 1L;
        final Player player1 = this.buildPlayer(id, "player1", "Player 1");
        final Player player2 = this.buildPlayer(id, "player2", "Player 2");
        final Game expectedResponse = this.buildGame(player1);
        final Move expectedMove = this.buildMove(1L, "spock");
        final PlayerMove expectedPlayerMove1 = this.buildPlayerMove(1L, expectedResponse, player1);
        final PlayerMove expectedPlayerMove2 = this.buildPlayerMove(1L, expectedResponse, player2);
        final Long expectedCountMovesPlayed = 3L;
        final Long expectedCountMovesTotal = 3L;

        final boolean expectedIsSpock = true;
        final boolean expectedIsTesoura = true;
        final boolean expectedIsPapel = false;
        final boolean expectedIsPedra = true;
        final boolean expectedIsLagarto = false;

        when(gameRepository.findById(id)).thenReturn(Optional.of(expectedResponse));
        when(moveRepository.findById(any())).thenReturn(Optional.of(expectedMove));
        when(playerMoveRepository.findByUnfinishedGameIdAndPlayer(any(), any())).thenReturn(Optional.of(expectedPlayerMove1));
        when(playerMoveRepository.countMovesPlayedByUnfinishedGame(any())).thenReturn(expectedCountMovesPlayed);
        when(playerMoveRepository.countByUnfinishedGameId(any())).thenReturn(expectedCountMovesTotal);
        when(playerRepository.findByUsername(any())).thenReturn(Optional.of(player1));
        when(playerMoveRepository.existsSpockByUnfinishedGameId(any())).thenReturn(expectedIsSpock);
        when(playerMoveRepository.existsTesouraByUnfinishedGameId(any())).thenReturn(expectedIsTesoura);
        when(playerMoveRepository.existsPapelByUnfinishedGameId(any())).thenReturn(expectedIsPapel);
        when(playerMoveRepository.existsPedraByUnfinishedGameId(any())).thenReturn(expectedIsPedra);
        when(playerMoveRepository.existsLagartoByUnfinishedGameId(any())).thenReturn(expectedIsLagarto);
        when(playerMoveRepository.findByUnfinishedGameId(any(), any())).thenReturn(List.of(expectedPlayerMove1, expectedPlayerMove2));

        // When (Act)
        var result = service.insertPlayerMove(buildGameMoveDTO()).getMessage();

        // Then (Assert)
        assertEquals("Vencedores: Player 1 e Player 2 ", result);
    }

    @Test
    void insertPlayerMoveWithNotRegistredGameShouldThrowsDataNotFoundExceptionTest() {
        // Given (Arrange)
        final Long id = 1L;
        final String playerUsername = "player";
        final Player player = this.buildPlayer(id, playerUsername, "Player");
        mockStatic.when(SecurityUtils::getCurrentUserLogin).thenReturn(playerUsername);
        when(playerRepository.findByUsername(playerUsername)).thenReturn(Optional.of(player));
        when(gameRepository.findById(id)).thenReturn(Optional.empty());

        // When (Act)
        final DataNotFoundException exception = assertThrows(
                DataNotFoundException.class , () -> service.insertPlayerMove(buildGameMoveDTO()));

        // Then (Assert)
        assertEquals("Jogo não cadastrado!", exception.getMessage());
        verify(playerRepository, times(1)).findByUsername("player");
        verify(gameRepository, times(0)).save(any());
        verify(playerRepository, times(0)).findById(1L);
        verify(playerRepository, times(0)).findById(2L);
    }

    @Test
    void insertPlayerMoveWithFinishedGameShouldThrowsBadRequestExceptionTest() {
        // Given (Arrange)
        final Long id = 1L;
        final String playerUsername = "player";
        final Player player = this.buildPlayer(id, playerUsername, "Player ");
        final Game expectedGame = this.buildGame(player, Boolean.TRUE);
        mockStatic.when(SecurityUtils::getCurrentUserLogin).thenReturn(playerUsername);
        when(playerRepository.findByUsername(playerUsername)).thenReturn(Optional.of(player));
        when(gameRepository.findById(id)).thenReturn(Optional.of(expectedGame));

        // When (Act)
        final BadRequestException exception = assertThrows(
                BadRequestException.class , () -> service.insertPlayerMove(buildGameMoveDTO()));

        // Then (Assert)
        assertEquals("O jogo já foi finalizado!", exception.getMessage());
        verify(playerRepository, times(1)).findByUsername(playerUsername);
        verify(gameRepository, times(1)).findById(1L);
        verify(gameRepository, times(0)).save(any());
        verify(playerRepository, times(0)).findById(1L);
        verify(playerRepository, times(0)).findById(2L);
    }

    @Test
    void insertPlayerMoveWithNotRegistredMoveShouldThrowDataNotFoundExceptionTest() {
        // Given (Arrange)
        final Long id = 1L;
        final String playerUsername = "player";
        final Player player = this.buildPlayer(id, playerUsername, "Player");
        final Game expectedGame = this.buildGame(player);
        mockStatic.when(SecurityUtils::getCurrentUserLogin).thenReturn(playerUsername);
        when(playerRepository.findByUsername(playerUsername)).thenReturn(Optional.of(player));
        when(gameRepository.findById(id)).thenReturn(Optional.of(expectedGame));
        final GameMoveDto gameMoveDto = GameMoveDto.builder().gameId(1L).moveId(10L).build(); // Move is not registred

        // When (Act)
        final DataNotFoundException exception = assertThrows(
                DataNotFoundException.class , () -> service.insertPlayerMove(gameMoveDto));

        // Then (Assert)
        assertEquals("Jogada não cadastrada", exception.getMessage());
        verify(playerRepository, times(1)).findByUsername(playerUsername);
        verify(gameRepository, times(1)).findById(1L);
        verify(gameRepository, times(0)).save(any());
        verify(playerRepository, times(0)).findById(1L);
        verify(playerRepository, times(0)).findById(2L);
    }

    @Test
    void insertPlayerMovePlayerWithPlayerNotRegistredInGameShouldThrowDataNotFoundExceptionTest() {
        // Given (Arrange)
        final Long id = 1L;
        final String playerUsername = "player";
        final Player player = this.buildPlayer(id, playerUsername, "Player");
        final Player playerNotRegistredInGame = this.buildPlayer(2L, "playernotregistred", "Player Not Registred");
        final Game expectedGame = this.buildGame(playerNotRegistredInGame); // Another player registred in game
        final Move expectedMove = this.buildMove(1L, "spock");
        mockStatic.when(SecurityUtils::getCurrentUserLogin).thenReturn(playerUsername); // Current player
        when(playerRepository.findByUsername(playerUsername)).thenReturn(Optional.of(player));
        when(gameRepository.findById(id)).thenReturn(Optional.of(expectedGame));
        when(moveRepository.findById(any())).thenReturn(Optional.of(expectedMove));

        // When (Act)
        final DataNotFoundException exception = assertThrows(
                DataNotFoundException.class , () -> service.insertPlayerMove(buildGameMoveDTO()));

        // Then (Assert)
        assertEquals("Jogador não está cadastrado no jogo!", exception.getMessage());
        verify(playerRepository, times(1)).findByUsername(playerUsername);
        verify(gameRepository, times(1)).findById(1L);
        verify(gameRepository, times(0)).save(any());
        verify(playerRepository, times(0)).findById(1L);
        verify(playerRepository, times(0)).findById(2L);
    }

    @Test
    void insertPlayerMovePlayerWithAlreadyRegistredMoveShouldThrowDataConflictExceptionTest() {
        // Given (Arrange)
        final Long id = 1L;
        final String playerUsername = "player";
        final Player player = this.buildPlayer(id, playerUsername, "Player");
        final Game expectedGame = this.buildGame(player);
        final Move expectedMove = this.buildMove(id, "spock");
        final PlayerMove expectedPlayerMove = this.buildPlayerMove(id, expectedGame, player, expectedMove);
        mockStatic.when(SecurityUtils::getCurrentUserLogin).thenReturn(playerUsername); // Current player
        when(playerRepository.findByUsername(playerUsername)).thenReturn(Optional.of(player));
        when(gameRepository.findById(id)).thenReturn(Optional.of(expectedGame));
        when(moveRepository.findById(any())).thenReturn(Optional.of(expectedMove));
        when(playerMoveRepository.findByUnfinishedGameIdAndPlayer(any(), any())).thenReturn(Optional.of(expectedPlayerMove));

        // When (Act)
        final DataConflictException exception = assertThrows(
                DataConflictException.class , () -> service.insertPlayerMove(buildGameMoveDTO()));

        // Then (Assert)
        assertEquals("Jogador já realizou a sua jogada!", exception.getMessage());
        verify(playerRepository, times(1)).findByUsername(playerUsername);
        verify(gameRepository, times(1)).findById(1L);
        verify(gameRepository, times(0)).save(any());
        verify(playerRepository, times(0)).findById(1L);
        verify(playerRepository, times(0)).findById(2L);
    }

    // Teste criado utilizando o TDD
    @Test void getRankingShouldReturnRankingListTest() {

        // Given
        List<Tuple> expectedTuple = this.buildTupleList(3);
        when(gameRepository.getRanking()).thenReturn(expectedTuple);

        // When
        List<RankingDto> response = service.getRanking();

        // Then
        assertEquals(expectedTuple.size(), response.size());
        verify(gameRepository, times(1)).getRanking();
    }

    @Test void getRankingShouldReturnEmptyListTest() {

        // Given
        List<Tuple> expectedTuple = this.buildTupleList(0);
        when(gameRepository.getRanking()).thenReturn(expectedTuple);

        // When
        List<RankingDto> response = service.getRanking();

        // Then
        assertEquals(expectedTuple.size(), response.size());
        verify(gameRepository, times(1)).getRanking();
    }

}