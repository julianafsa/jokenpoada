package tech.ada.games.jokenpo.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.annotation.DirtiesContext;
import tech.ada.games.jokenpo.dto.GameDto;
import tech.ada.games.jokenpo.dto.GameMoveDto;
import tech.ada.games.jokenpo.dto.ResultDto;
import tech.ada.games.jokenpo.model.Game;
import tech.ada.games.jokenpo.response.AuthResponse;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;


@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class GameControllerTest extends AbstractBaseTest {

    private final String baseUri = "/api/v1/jokenpo/game";
    private AuthResponse authResponse;

//    @Autowired
//    private DataSource dataSource; // application.properties

//    @Autowired
//    private WebApplicationContext context;

    @BeforeEach
    void setup() {
//        final ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
//        populator.setContinueOnError(false);
//        populator.addScript(new ClassPathResource("import.sql"));
//        ResourceDatabasePopulator populator = new ResourceDatabasePopulator(
//                context.getResource("classpath:/import.sql"));
//        DatabasePopulatorUtils.execute(populator, dataSource);
        this.populateDatabase();
    }

//    @AfterEach
//    void finish() {
//        final ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
//        populator.setContinueOnError(false);
//        populator.addScript(new ClassPathResource("drop_database.sql"));
//        DatabasePopulatorUtils.execute(populator, dataSource);
        // ddl-auto= create-drop" means that when the server is run,
        // the database(table) instance is created. And whenever the server stops,
        // the database table instance is droped.
//    }

    @Test
    void newGameTest() throws Exception {
        this.authResponse = this.loginAsF1rstPlayer();
        List<Long> playersIds = Arrays.asList(1L, 2L);
        final GameDto gameDto = this.buildGameDto(playersIds);

        final MockHttpServletResponse response =
                mvc.perform(post(baseUri + "/new")
                                .content(asJsonString(gameDto))
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", authResponse.getAccessToken()))
                        .andDo(print())
                        .andReturn().getResponse();

        assertEquals(HttpStatus.CREATED.value(), response.getStatus());
    }

    @Test
    void newGameNumberOfPlayersGreaterThan2Test() throws Exception {
        this.authResponse = this.loginAsF1rstPlayer();
        List<Long> playersIds = Arrays.asList(1L, 2L, 3L);
        final GameDto gameDto = this.buildGameDto(playersIds);

        final MockHttpServletResponse response =
                mvc.perform(post(baseUri + "/new")
                                .content(asJsonString(gameDto))
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", authResponse.getAccessToken()))
                        .andDo(print())
                        .andReturn().getResponse();

        assertEquals(HttpStatus.CREATED.value(), response.getStatus());
    }

    @Test
    void newGameNumberOfPlayersLessThan2Test() throws Exception {
        this.authResponse = this.loginAsF1rstPlayer();
        List<Long> playersIds = List.of(1L);
        final GameDto gameDto = this.buildGameDto(playersIds);

        final MockHttpServletResponse response =
                mvc.perform(post(baseUri + "/new")
                                .content(asJsonString(gameDto))
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", authResponse.getAccessToken()))
                        .andDo(print())
                        .andReturn().getResponse();

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    }

    @Test
    void newGamePlayerNotFoundedTest() throws Exception {
        this.authResponse = this.loginAsF1rstPlayer();
        List<Long> playersIds = Arrays.asList(1L, 10L); // Player with id 10L does not exist.
        final GameDto gameDto = this.buildGameDto(playersIds);

        final MockHttpServletResponse response =
                mvc.perform(post(baseUri + "/new")
                                .content(asJsonString(gameDto))
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", authResponse.getAccessToken()))
                        .andDo(print())
                        .andReturn().getResponse();

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

    @Test
    void newGameWithoutAuthorizationHeaderTest() throws Exception {
        List<Long> playersIds = Arrays.asList(1L, 2L);
        final GameDto gameDto = this.buildGameDto(playersIds);

        final MockHttpServletResponse response =
                mvc.perform(post(baseUri + "/new")
                                .content(asJsonString(gameDto))
                                .contentType(MediaType.APPLICATION_JSON))
                        .andDo(print())
                        .andReturn().getResponse();

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

    @Test
    void insertPlayerMoveTest() throws Exception {
        this.authResponse = this.loginAsF1rstPlayer();
        this.buildMoves();
        List<Long> playersIds = Arrays.asList(1L, 2L);
        final GameDto gameDto = this.buildGameDto(playersIds);
        this.createGame(gameDto);
        final GameMoveDto gameMoveDto = this.buildGameMoveDto(1L, 1L);

        final MockHttpServletResponse response =
                mvc.perform(post(baseUri + "/move")
                                .content(asJsonString(gameMoveDto))
                                .contentType(MediaType.APPLICATION_JSON))
                        .andDo(print())
                        .andReturn().getResponse();
        final ResultDto resultDto = this.asResultDtoObject(response.getContentAsString());

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals("Jogada realizada! Faltam 1 jogadores para finalizar o jogo!", resultDto.getMessage());
    }

    @Test
    void insertPlayerMoveNotFoundTest() throws Exception {
        this.authResponse = this.loginAsF1rstPlayer();
        this.buildMoves();
        final GameMoveDto gameMoveDto = this.buildGameMoveDto(1L, 1L); // Game with id 1L does not exist

        final MockHttpServletResponse response =
                mvc.perform(post(baseUri + "/move")
                                .content(asJsonString(gameMoveDto))
                                .contentType(MediaType.APPLICATION_JSON))
                        //.andDo(print())
                        .andReturn().getResponse();

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
        //assertEquals("Jogada realizada! Faltam 1 jogadores para finalizar o jogo!", resultDto.getMessage());
    }

    @Test
    void findGamesTest() throws Exception {
        this.authResponse = this.loginAsF1rstPlayer();
        List<Long> playersIds = Arrays.asList(1L, 2L);
        this.createGame(this.buildGameDto(playersIds));

        playersIds = Arrays.asList(3L, 4L, 5L);
        this.buildGameDto(playersIds);
        this.createGame(this.buildGameDto(playersIds));

        final MockHttpServletResponse response =
                mvc.perform(get(baseUri)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", authResponse.getAccessToken()))
                        .andDo(print())
                        .andReturn().getResponse();
        final List<Game> games = this.asGameListObject(response.getContentAsString());

        assertEquals(2, games.size());
        assertEquals(Boolean.FALSE, games.get(0).getFinished());
        assertEquals(Boolean.FALSE, games.get(1).getFinished());
        assertEquals(HttpStatus.OK.value(), response.getStatus());
    }

    @Test
    void findGamesNoContentTest() throws Exception {
        this.authResponse = this.loginAsF1rstPlayer();

        final MockHttpServletResponse response =
                mvc.perform(get(baseUri)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", authResponse.getAccessToken()))
                        .andDo(print())
                        .andReturn().getResponse();

        assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatus());
    }

    @Test
    void findGameTest() throws Exception {
        this.authResponse = this.loginAsF1rstPlayer();
        this.createGame(this.buildGameDto(Arrays.asList(1L, 2L)));

        final MockHttpServletResponse response =
                mvc.perform(get(baseUri + "/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", authResponse.getAccessToken()))
                        .andDo(print())
                        .andReturn().getResponse();

        final Game game = this.asGameObject(response.getContentAsString());
        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertNotNull(game);
        assertEquals(1, game.getId());
    }

    @Test
    void findGameNotFoundTest() throws Exception {
        this.authResponse = this.loginAsF1rstPlayer();

        final MockHttpServletResponse response =
                mvc.perform(get(baseUri + "/1000") // Game with id 1000 does not exist.
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", authResponse.getAccessToken()))
                        .andDo(print())
                        .andReturn().getResponse();

        final String responseAsString = response.getContentAsString();
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
        assertEquals("", responseAsString);
    }

}