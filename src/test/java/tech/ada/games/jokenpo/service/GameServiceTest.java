package tech.ada.games.jokenpo.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import tech.ada.games.jokenpo.dto.GameDto;
import tech.ada.games.jokenpo.dto.GameMoveDto;
import tech.ada.games.jokenpo.exception.BadRequestException;
import tech.ada.games.jokenpo.exception.DataConflictException;
import tech.ada.games.jokenpo.exception.DataNotFoundException;
import tech.ada.games.jokenpo.model.*;
import tech.ada.games.jokenpo.repository.GameRepository;
import tech.ada.games.jokenpo.repository.MoveRepository;
import tech.ada.games.jokenpo.repository.PlayerMoveRepository;
import tech.ada.games.jokenpo.repository.PlayerRepository;
import tech.ada.games.jokenpo.security.SecurityUtils;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class GameServiceTest {

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
        final Player player1 = this.buildPlayer(1L, playerUsername1);
        final Player player2 = this.buildPlayer(2L, playerUsername2);
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
    void insertPlayerMoveTest() {
        // TODO WIP
        // Given (Arrange)
        final Long gameId = 1L;
        final Long moveId = 1L;
        final GameMoveDto gameMoveDto = new GameMoveDto(gameId, moveId);

        final String playerUsername1 = "player1";
        final Player player1 = this.buildPlayer(1L, playerUsername1);

        mockStatic.when(SecurityUtils::getCurrentUserLogin).thenReturn(playerUsername1);
        when(playerRepository.findByUsername(playerUsername1)).thenReturn(Optional.of(player1));

        final Game game = this.buildGame(player1);
        when(gameRepository.findById(gameId)).thenReturn(Optional.of(game));

        final Move move = new Move();
        move.setId(moveId);
        move.setMove("Tesoura");
        when(moveRepository.findById(gameMoveDto.getMoveId())).thenReturn(Optional.of(move));

        PlayerMove playerMove = new PlayerMove(1L, game, player1, move);
        when(playerMoveRepository.findByUnfinishedGameIdAndPlayer(player1.getId(), gameMoveDto.getGameId())).thenReturn(Optional.of(playerMove));
        when(playerMoveRepository.save(playerMove)).thenReturn(playerMove);
        when(playerMoveRepository.countMovesPlayedByUnfinishedGame(game.getId())).thenReturn(1L);
        // TODO see insertPlayerMove. It seems to be wrong.
        //when(playerMoveRepository.countMovesPlayedByUnfinishedGame(game.getId())).thenReturn(1);
        when(playerMoveRepository.existsSpockByUnfinishedGameId(gameId)).thenReturn(false);
        when(playerMoveRepository.existsTesouraByUnfinishedGameId(gameId)).thenReturn(true);
        when(playerMoveRepository.existsPapelByUnfinishedGameId(gameId)).thenReturn(false);
        when(playerMoveRepository.existsPedraByUnfinishedGameId(gameId)).thenReturn(false);
        when(playerMoveRepository.existsLagartoByUnfinishedGameId(gameId)).thenReturn(false);

        // When (Act)

        // Then (Assert)
    }

    @Test
    void findGamesTest() {
        // Given (Arrange)
        final List<Game> expectedResponse = Arrays.asList(new Game());
        when(gameRepository.findAll()).thenReturn(expectedResponse);

        // When (Act)
        List<Game> response = service.findGames();

        // Then (Assert)
        assertEquals(expectedResponse.size(), response.size(), "The size of the lists are not equal.");
    }

    @Test
    void findGameByIdTest() throws DataNotFoundException {
        // Given (Arrange)
        final Long id = 1L;
        final Player player = this.buildPlayer(id, "player");
        final Game expectedResponse = this.buildGame(player);
        when(gameRepository.findById(id)).thenReturn(Optional.of(expectedResponse));

        // When (Act)
        final Game response = service.findGameById(id);

        // Then (Assert)
        assertEquals(expectedResponse.getId(), response.getId(), "Game id is not as expected.");
    }

    @Test
    void findGameByIdThrowsDataNotFoundExceptionTest() {
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
        final Player player = this.buildPlayer(id, "player");
        final Game expectedResponse = this.buildGame(player);
        final Move expectedMove = this.buildMove();
        final PlayerMove expectedPlayerMove = this.buildPlayerMove(expectedResponse, player);
        final Long expectedCountMovesPlayed = 1L;
        final Long expectedCountMovesTotal = 2L;

        when(gameRepository.findById(id)).thenReturn(Optional.of(expectedResponse));
        when(moveRepository.findById(any())).thenReturn(Optional.of(expectedMove));
        when(playerMoveRepository.findByUnfinishedGameIdAndPlayer(any(), any())).thenReturn(Optional.of(expectedPlayerMove));
        when(playerMoveRepository.countMovesPlayedByUnfinishedGame(any())).thenReturn(expectedCountMovesPlayed);
        when(playerMoveRepository.countByUnfinishedGameId(any())).thenReturn(expectedCountMovesTotal);
        when(playerRepository.findByUsername(any())).thenReturn(Optional.of(player));
        assertNotNull(service.insertPlayerMove(buildGameMoveDTO()));

    }

    @Test
    void insertPlayerMoveWithSuccessFinishingSpockWinsGameTest() throws DataNotFoundException, DataConflictException, BadRequestException {
        // Given (Arrange)
        final Long id = 1L;
        final Player player = this.buildPlayer(id, "player");
        final Game expectedResponse = this.buildGame(player);
        final Move expectedMove = this.buildMove();
        final PlayerMove expectedPlayerMove = this.buildPlayerMove(expectedResponse, player);
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
        var result = service.insertPlayerMove(buildGameMoveDTO()).getMessage();
        assertEquals("Vencedor: Player", result);


    }


    private GameDto buildGameDto() {
        final GameDto gameDto = new GameDto();
        List<Long> players = Arrays.asList(1L, 2L);
        gameDto.setPlayers(players);
        return gameDto;
    }

    private Player buildPlayer(final Long id, final String username) {
        final Player player = new Player();
        player.setId(id);
        player.setUsername(username);
        player.setPassword("1234");
        player.setName("Player");
        final Role role = this.buildRole();
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        player.setRoles(roles);
        return player;
    }

    private Role buildRole() {
        final Role role = new Role();
        role.setId(1L);
        role.setName("ROLE_USER");
        return role;
    }

    private Game buildGame(final Player player) {
        final Game game = new Game();
        game.setId(player.getId());
        game.setCreator(player);
        game.setFinished(Boolean.FALSE);
        game.setCreatedAt(LocalDateTime.now());
        return game;
    }

    private GameMoveDto buildGameMoveDTO() {
        GameMoveDto gmDto = new GameMoveDto();

        gmDto.setMoveId(1L);
        gmDto.setGameId(1L);
        return gmDto;
    }
    private Move buildMove() {
        Move move = new Move();
        move.setMove("tesoura");
        move.setId(1L);
        return move;
    }

    private PlayerMove buildPlayerMove(Game game, Player player) {
        PlayerMove pMove = new PlayerMove();

        pMove.setGame(game);
        pMove.setPlayer(player);
        pMove.setId(1L);
        return pMove;
    }

}