package tech.ada.games.jokenpo.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import tech.ada.games.jokenpo.dto.GameDto;
import tech.ada.games.jokenpo.dto.PlayerDto;
import tech.ada.games.jokenpo.exception.DataConflictException;
import tech.ada.games.jokenpo.exception.DataNotFoundException;
import tech.ada.games.jokenpo.response.AuthResponse;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

class PlayerControllerTest extends AbstractBaseTest {

    private final String baseUri = "/api/v1/jokenpo/player";
    private AuthResponse authResponse;

    @BeforeEach
    void beforeAll() {
        this.populateDatabase();
        this.authResponse = this.loginAsF1rstPlayer();
    }

    @Test
    void createPlayerTest() throws Exception {
        PlayerDto player = PlayerDto.builder()
            .name("player1")
            .username("username1")
            .password("1234")
            .build();

        final MockHttpServletResponse response =
                mvc.perform(post(baseUri + "/create")
                                .content(asJsonString(player))
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", authResponse.getAccessToken()))
                        .andDo(print())
                        .andReturn().getResponse();

        assertEquals(HttpStatus.CREATED.value(), response.getStatus());
    }

    @Test
    void createPlayerAlreadyCreatedTest() throws Exception {
        PlayerDto playerDto = PlayerDto.builder()
                .name("player1")
                .username("player1")
                .password("1234")
                .build();

        final MvcResult result =
                mvc.perform(post(baseUri + "/create")
                                .content(asJsonString(playerDto))
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", authResponse.getAccessToken()))
                        .andDo(print())
                        .andReturn();
        final DataConflictException exception = (DataConflictException) result.getResolvedException();

        assertEquals("O jogador já está cadastrado!", exception.getMessage());
        assertEquals(HttpStatus.CONFLICT.value(), result.getResponse().getStatus());
    }

    @Test
    void findAllPlayersTest() throws Exception {

        final MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders.get(baseUri)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", authResponse.getAccessToken()))
                .andDo(print())
                .andReturn().getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertNotNull(response.getContentAsString());

    }

    @Test
    void findPlayersByNameTest() throws Exception {
        final MockHttpServletResponse response = mvc.perform(get(baseUri + "/player1")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", authResponse.getAccessToken()))
                .andDo(print())
                .andReturn().getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertNotNull(response.getContentAsString());
    }

    @Test
    void findPlayersByNameNotFoundTest() throws Exception {
        final String invalidUsername = "InvalidUsername";
        final MvcResult result = mvc.perform(MockMvcRequestBuilders.get(
                                baseUri + "/" + invalidUsername)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", authResponse.getAccessToken()))
                .andDo(print())
                .andReturn();

        assertTrue(result.getResolvedException() instanceof DataNotFoundException);
        assertEquals(HttpStatus.NOT_FOUND.value(), result.getResponse().getStatus());
    }
 
     @Test
     void deletePlayerWithSuccessTest() throws Exception {
         this.executeScript("delete_games.sql");
         final MockHttpServletResponse response = mvc.perform(delete(baseUri + "/1")
                 .contentType(MediaType.APPLICATION_JSON)
                 .header("Authorization", authResponse.getAccessToken()))
                 .andDo(print())
                 .andReturn().getResponse();

         assertEquals(HttpStatus.NO_CONTENT.value(), response.getStatus());
     }

    @Test
    void deletePlayerNotFoundTest() throws Exception {
        final String invalidId = "1000";
        final MvcResult result = mvc.perform(delete(baseUri + "/" + invalidId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", authResponse.getAccessToken()))
                .andDo(print())
                .andReturn();

        assertTrue(result.getResolvedException() instanceof DataNotFoundException);
        assertEquals(HttpStatus.NOT_FOUND.value(), result.getResponse().getStatus());
    }

    @Test
    void deletePlayerDataConflitExceptionTest() throws Exception {
        this.createGame(new GameDto(Arrays.asList(1L, 2L)));
        final MvcResult result = mvc.perform(delete(baseUri + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", authResponse.getAccessToken()))
                .andDo(print())
                .andReturn();
        final DataConflictException exception = (DataConflictException) result.getResolvedException();

        assertEquals("O jogador está registrado em uma partida não finalizada!", exception.getMessage());
        assertEquals(HttpStatus.CONFLICT.value(), result.getResponse().getStatus());
    }

}