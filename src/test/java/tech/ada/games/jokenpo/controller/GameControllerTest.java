package tech.ada.games.jokenpo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import tech.ada.games.jokenpo.dto.GameDto;
import tech.ada.games.jokenpo.exception.BadRequestException;
import tech.ada.games.jokenpo.exception.DataConflictException;
import tech.ada.games.jokenpo.response.AuthResponse;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

class GameControllerTest extends BaseGameTest {

    private final String baseUri = "/api/v1/jokenpo/game";
    private AuthResponse authResponse;

//    @Autowired
//    private DataSource ds; //your application.properties

    @BeforeEach
    void beforeAll() {
//        ResourceDatabasePopulator populator = new ResourceDatabasePopulator(
//                context.getResource("classpath:/import.sql"));
//        DatabasePopulatorUtils.execute(populator, ds);
        this.populateDatabase();
    }

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
    void newGameWithoutAuthorizationHeaderTest() throws Exception {
        List<Long> playersIds = Arrays.asList(1L, 2L);
        final GameDto gameDto = this.buildGameDto(playersIds);

        final MockHttpServletResponse response =
                mvc.perform(post(baseUri + "/new")
                                .content(asJsonString(gameDto))
                                //.header("Authorization", "")
                                .contentType(MediaType.APPLICATION_JSON))
                        .andDo(print())
                        .andReturn().getResponse();

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

    @Test
    void newGameNumberOfPlayersLessThan2Test() throws Exception {
        this.authResponse = this.loginAsF1rstPlayer();
        List<Long> playersIds = Arrays.asList(1L);
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



//    @Test
//    void insertPlayerMove() {
//    }
//
//    @Test
//    void findGames() {
//    }
//
//    @Test
//    void findGame() {
//    }

    private String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}