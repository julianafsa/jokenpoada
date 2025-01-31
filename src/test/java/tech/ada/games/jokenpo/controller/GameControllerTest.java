package tech.ada.games.jokenpo.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MvcResult;
import tech.ada.games.jokenpo.dto.GameDto;
import tech.ada.games.jokenpo.dto.GameMoveDto;
import tech.ada.games.jokenpo.dto.RankingDto;
import tech.ada.games.jokenpo.dto.ResultDto;
import tech.ada.games.jokenpo.exception.BadRequestException;
import tech.ada.games.jokenpo.exception.DataNotFoundException;
import tech.ada.games.jokenpo.model.Game;
import tech.ada.games.jokenpo.response.AuthResponse;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;


@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class GameControllerTest extends AbstractBaseTest {

    private final String baseUri = "/api/v1/jokenpo/game";
    private AuthResponse authResponse;

    @BeforeEach
    void setup() {
        this.populateDatabase();
    }

    @Test
    void newGameWithTwoRegistredPlayersShouldCreateGameTest() throws Exception {
        // Given
        this.authResponse = this.loginAsF1rstPlayer();
        List<Long> playersIds = Arrays.asList(1L, 2L);
        final GameDto gameDto = this.buildGameDto(playersIds);

        // When
        final MockHttpServletResponse response =
                mvc.perform(post(baseUri + "/new")
                                .content(asJsonString(gameDto))
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", authResponse.getAccessToken()))
                        .andDo(print())
                        .andReturn().getResponse();

        // Then
        assertEquals(HttpStatus.CREATED.value(), response.getStatus());
    }

    @Test
    void newGameNumberOfPlayersGreaterThan2ShouldCreatesGameTest() throws Exception {
        // Given
        this.authResponse = this.loginAsF1rstPlayer();
        List<Long> playersIds = Arrays.asList(1L, 2L, 3L);
        final GameDto gameDto = this.buildGameDto(playersIds);

        // When
        final MockHttpServletResponse response =
                mvc.perform(post(baseUri + "/new")
                                .content(asJsonString(gameDto))
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", authResponse.getAccessToken()))
                        .andDo(print())
                        .andReturn().getResponse();

        // Then
        assertEquals(HttpStatus.CREATED.value(), response.getStatus());
    }

    @Test
    void newGameWithNumberOfPlayersLessThan2ShouldNotCreateGameAndShouldReturnsBadRequestStatus() throws Exception {
        // Given
        this.authResponse = this.loginAsF1rstPlayer();
        List<Long> playersIds = List.of(1L);
        final GameDto gameDto = this.buildGameDto(playersIds);

        // When
        final MvcResult result =
                mvc.perform(post(baseUri + "/new")
                                .content(asJsonString(gameDto))
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", authResponse.getAccessToken()))
                        .andDo(print())
                        .andReturn();

        // Then
        assertTrue(result.getResolvedException() instanceof BadRequestException);
        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
    }

    @Test
    void newGameWithPlayerNotFoundedShouldNotCreateGameAndShouldReturnsNotFoundStatusTest() throws Exception {
        // Given
        this.authResponse = this.loginAsF1rstPlayer();
        List<Long> playersIds = Arrays.asList(1L, 10L); // Player with id 10L does not exist.
        final GameDto gameDto = this.buildGameDto(playersIds);

        // When
        final MvcResult result =
                mvc.perform(post(baseUri + "/new")
                                .content(asJsonString(gameDto))
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", authResponse.getAccessToken()))
                        .andDo(print())
                        .andReturn();

        // Then
        assertTrue(result.getResolvedException() instanceof DataNotFoundException);
        assertEquals(HttpStatus.NOT_FOUND.value(), result.getResponse().getStatus());
    }

    @Test
    void newGameWithoutAuthorizationHeaderShouldNotCreateGameAndShouldReturnsUnauthorizedStatusTest() throws Exception {
        // Given
        List<Long> playersIds = Arrays.asList(1L, 2L);
        final GameDto gameDto = this.buildGameDto(playersIds);

        // When
        final MockHttpServletResponse response =
                mvc.perform(post(baseUri + "/new")
                                .content(asJsonString(gameDto))
                                .contentType(MediaType.APPLICATION_JSON))
                        .andDo(print())
                        .andReturn().getResponse();

        // Then
        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
    }

    @Test
    void insertPlayerMoveTest() throws Exception {
        // Given
        this.authResponse = this.loginAsF1rstPlayer();
        this.buildMoves();
        List<Long> playersIds = Arrays.asList(1L, 2L);
        final GameDto gameDto = this.buildGameDto(playersIds);
        this.createGame(gameDto);
        final GameMoveDto gameMoveDto = this.buildGameMoveDto(1L, 1L);

        // When
        final MockHttpServletResponse response =
                mvc.perform(post(baseUri + "/move")
                                .content(asJsonString(gameMoveDto))
                                .contentType(MediaType.APPLICATION_JSON))
                        .andDo(print())
                        .andReturn().getResponse();
        final ResultDto resultDto = this.asResultDtoObject(response.getContentAsString());

        // Then
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals("Jogada realizada! Faltam 1 jogadores para finalizar o jogo!", resultDto.getMessage());
    }

    @Test
    void insertPlayerMoveWithGameNotRegistredShouldNotInsertMoveAndReturnNotFoundStatusTest() throws Exception {
        // Given
        this.authResponse = this.loginAsF1rstPlayer();
        this.buildMoves();
        final GameMoveDto gameMoveDto = this.buildGameMoveDto(1L, 1L); // Game with id 1L does not exist

        // When
        final MvcResult result =
                mvc.perform(post(baseUri + "/move")
                                .content(asJsonString(gameMoveDto))
                                .contentType(MediaType.APPLICATION_JSON))
                        //.andDo(print())
                        .andReturn();

        // Then
        assertTrue(result.getResolvedException() instanceof DataNotFoundException);
        assertEquals(HttpStatus.NOT_FOUND.value(), result.getResponse().getStatus());
    }

    @Test
    void findGamesTest() throws Exception {
        // Given
        this.authResponse = this.loginAsF1rstPlayer();
        List<Long> playersIds = Arrays.asList(1L, 2L);
        this.createGame(this.buildGameDto(playersIds));

        playersIds = Arrays.asList(3L, 4L, 5L);
        this.buildGameDto(playersIds);
        this.createGame(this.buildGameDto(playersIds));

        // When
        final MockHttpServletResponse response =
                mvc.perform(get(baseUri)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", authResponse.getAccessToken()))
                        .andDo(print())
                        .andReturn().getResponse();
        final List<Game> games = this.asGameListObject(response.getContentAsString());

        // Then
        assertEquals(2, games.size());
        assertEquals(Boolean.FALSE, games.get(0).getFinished());
        assertEquals(Boolean.FALSE, games.get(1).getFinished());
        assertEquals(HttpStatus.OK.value(), response.getStatus());
    }

    @Test
    void findGamesWithNotRegistredGamesShouldReturnNoContentStatusTest() throws Exception {
        // Given
        this.authResponse = this.loginAsF1rstPlayer();

        // When
        final MockHttpServletResponse response =
                mvc.perform(get(baseUri)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", authResponse.getAccessToken()))
                        .andDo(print())
                        .andReturn().getResponse();

        // Then
        assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatus());
    }

    @Test
    void findGameByIdTest() throws Exception {
        // Given
        this.authResponse = this.loginAsF1rstPlayer();
        this.createGame(this.buildGameDto(Arrays.asList(1L, 2L)));

        // When
        final MockHttpServletResponse response =
                mvc.perform(get(baseUri + "/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", authResponse.getAccessToken()))
                        .andDo(print())
                        .andReturn().getResponse();
        final Game game = this.asGameObject(response.getContentAsString());

        // Then
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertNotNull(game);
        assertEquals(1, game.getId());
    }

    @Test
    void findGameByIdWithGameNotRegistredShouldReturnsNotFoundStatusTest() throws Exception {
        // Given
        this.authResponse = this.loginAsF1rstPlayer();

        // When
        final MvcResult result =
                mvc.perform(get(baseUri + "/1000") // Game with id 1000 does not exist.
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", authResponse.getAccessToken()))
                        .andDo(print())
                        .andReturn();
        final String responseAsString = result.getResponse().getContentAsString();

        // Then
        assertTrue(result.getResolvedException() instanceof DataNotFoundException);
        assertEquals(HttpStatus.NOT_FOUND.value(), result.getResponse().getStatus());
        assertEquals("", responseAsString);
    }

    @Test
    void getRankingTest() throws Exception {
        // Given
        this.authResponse = this.loginAsF1rstPlayer();
        final int numberOfPlayers = 5;
        this.createGames(5);

        // When
        final MockHttpServletResponse response =
                mvc.perform(get(baseUri + "/ranking")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", authResponse.getAccessToken()))
                        .andDo(print())
                        .andReturn().getResponse();
        final List<RankingDto> rankingResponse = this.asRankingListObject(response.getContentAsString());

        // Then
        assertNotNull(rankingResponse);
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(numberOfPlayers, rankingResponse.size());
    }

}