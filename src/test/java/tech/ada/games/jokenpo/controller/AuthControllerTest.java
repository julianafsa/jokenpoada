package tech.ada.games.jokenpo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.annotation.DirtiesContext;
import tech.ada.games.jokenpo.dto.LoginDto;
import tech.ada.games.jokenpo.response.AuthResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class AuthControllerTest extends AbstractBaseTest {

    private final String baseUri = "/api/v1/jokenpo/login";

    @Test
    void authenticateUserTest() throws Exception {
        // Given
        final String username = "username";
        final String password = "password";
        createPlayerIfNotExists(username, password);
        final LoginDto loginDto = this.buildLoginDto(username, password);

        // When
        final String responseAsString =
                mvc.perform(post(baseUri)
                                .content(asJsonString(loginDto))
                                .contentType(MediaType.APPLICATION_JSON))
                        .andDo(print())
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString();
        final AuthResponse response =
                new ObjectMapper().convertValue(responseAsString, AuthResponse.class);

        // Then
        assertEquals("Bearer", response.getTokenType());
        assertNotNull(response.getAccessToken());
    }

    @Test
    void authenticateUserNotFoundTest() throws Exception {
        // Given
        final String username = "usernotfound";
        final String password = "password";
        final LoginDto loginDto = this.buildLoginDto(username, password);

        // When
        final MockHttpServletResponse response =
                mvc.perform(post(baseUri)
                                .content(asJsonString(loginDto))
                                .contentType(MediaType.APPLICATION_JSON))
                        .andDo(print())
                        .andExpect(status().is4xxClientError())
                        .andReturn().getResponse();

        // Then
        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
    }

    @Test
    void authenticateUserWithInvalidPassoword() throws Exception {
        // Given
        final String username = "username";
        final String password = "password";
        createPlayerIfNotExists(username, password);
        final LoginDto loginDto = this.buildLoginDto(username, "invalidpassword");

        // When
        final MockHttpServletResponse response =
                mvc.perform(post(baseUri)
                                .content(asJsonString(loginDto))
                                .contentType(MediaType.APPLICATION_JSON))
                        .andDo(print())
                        .andExpect(status().is4xxClientError())
                        .andReturn().getResponse();

        // Then
        assertEquals(HttpStatus.UNAUTHORIZED.value(), response.getStatus());
    }

}