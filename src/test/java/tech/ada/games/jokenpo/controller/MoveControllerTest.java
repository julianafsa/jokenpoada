package tech.ada.games.jokenpo.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import tech.ada.games.jokenpo.dto.MoveDto;
import tech.ada.games.jokenpo.response.AuthResponse;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class MoveControllerTest extends BaseTests {

    private final String baseUri = "/api/v1/jokenpo/move";
    private AuthResponse authResponse;

    @BeforeEach
    void beforeAll() {
        this.populateDatabase();
        this.authResponse = this.loginAsF1rstPlayer();
    }

    @Test
    void createMoveTest() throws Exception {
        MoveDto move = new MoveDto().builder().move("JOGADA SPOCK").build();

        final MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders.post(baseUri)
                .content(asJsonString(move))
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", authResponse.getAccessToken()))
                .andDo(print())
                .andReturn().getResponse();

        assertEquals(HttpStatus.CREATED.value(), response.getStatus());
    }

    @Test
    void findAllMovesTest() throws Exception {

        final MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders.get(baseUri)
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", authResponse.getAccessToken()))
                .andDo(print())
                .andReturn().getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertNotNull(response.getContentAsString());

    }

    @Test
    void findMovesByNameTest() throws Exception {

        final MockHttpServletResponse response = mvc.perform(MockMvcRequestBuilders.get(baseUri + "/Spock")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", authResponse.getAccessToken()))
                .andDo(print())
                .andReturn().getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertNotNull(response.getContentAsString());

    }

    private String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}