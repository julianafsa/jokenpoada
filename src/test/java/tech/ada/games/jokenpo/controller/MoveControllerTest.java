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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import tech.ada.games.jokenpo.dto.MoveDto;
import tech.ada.games.jokenpo.exception.BadRequestException;
import tech.ada.games.jokenpo.exception.DataNotFoundException;
import tech.ada.games.jokenpo.response.AuthResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class MoveControllerTest extends AbstractBaseTest {

    private final String baseUri = "/api/v1/jokenpo/move";
    private AuthResponse authResponse;

    @BeforeEach
    void beforeAll() {
        final int NUMBER_OF_PLAYERS = 5;
        this.buildPlayers(NUMBER_OF_PLAYERS);
        this.authResponse = this.loginAsF1rstPlayer();
    }

    @Test
    void createMoveTest() throws Exception {
        final MoveDto move = MoveDto.builder().move("JOGADA SPOCK").build();

        final MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders.post(baseUri)
                .content(asJsonString(move))
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", authResponse.getAccessToken()))
                .andDo(print())
                .andReturn().getResponse();

        assertEquals(HttpStatus.CREATED.value(), response.getStatus());
    }

    @Test
    void createInvalidMoveTest() throws Exception {
        final MoveDto move = new MoveDto();
        move.setMove("JOGADA INVALIDA");

        final MvcResult result = mvc.perform(MockMvcRequestBuilders.post(baseUri)
                        .content(asJsonString(move))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", authResponse.getAccessToken()))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andReturn();

        assertTrue(result.getResolvedException() instanceof BadRequestException);
        assertEquals(HttpStatus.BAD_REQUEST.value(), result.getResponse().getStatus());
    }

    @Test
    void findAllMovesTest() throws Exception {
        this.buildMoves();
        final MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders.get(baseUri)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", authResponse.getAccessToken()))
                .andDo(print())
                .andReturn().getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertNotNull(response.getContentAsString());
    }

    @Test
    void findAllMovesNotFoundTest() throws Exception {
        this.executeScript("delete_moves.sql");
        final MvcResult result = mvc.perform(MockMvcRequestBuilders.get(baseUri)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", authResponse.getAccessToken()))
                .andDo(print())
                .andReturn();

        assertTrue(result.getResolvedException() instanceof DataNotFoundException);
        assertEquals(HttpStatus.NOT_FOUND.value(), result.getResponse().getStatus());
    }

    @Test
    void findMovesByNameTest() throws Exception {
        this.buildMoves();
        final MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders.get(baseUri + "/Spock")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", authResponse.getAccessToken()))
                .andDo(print())
                .andReturn().getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertNotNull(response.getContentAsString());
    }

    @Test
    void findMovesByNameNotFoundTest() throws Exception {
        final String invalidMove = "JOGADA INVALIDA";
        final MvcResult result = mvc.perform(MockMvcRequestBuilders.get(
                baseUri + "/" + invalidMove)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", authResponse.getAccessToken()))
                .andDo(print())
                .andReturn();

        assertTrue(result.getResolvedException() instanceof DataNotFoundException);
        assertEquals(HttpStatus.NOT_FOUND.value(), result.getResponse().getStatus());
    }

}