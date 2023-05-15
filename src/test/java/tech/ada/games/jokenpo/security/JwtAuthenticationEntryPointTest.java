package tech.ada.games.jokenpo.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.AuthenticationException;

import java.io.IOException;

import static org.mockito.Mockito.*;

@SpringBootTest
class JwtAuthenticationEntryPointTest {

    private final HttpServletRequest request = mock(HttpServletRequest.class);
    private final HttpServletResponse response = mock(HttpServletResponse.class);
    private final AuthenticationException exception = mock(AuthenticationException.class);
    private final JwtAuthenticationEntryPoint entryPoint = new JwtAuthenticationEntryPoint();

    @Test
    public void commenceUnauthorizedTest() throws ServletException, IOException {
        // Given
        when(response.getStatus()).thenReturn(HttpServletResponse.SC_UNAUTHORIZED);

        // When
        entryPoint.commence(request, response, exception);

        // Then
        verify(response).sendError(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    public void commenceOKTest() throws ServletException, IOException {
        // Given
        when(response.getStatus()).thenReturn(HttpServletResponse.SC_OK);

        // When
        entryPoint.commence(request, response, exception);

        // Then
        verify(response).sendError(HttpServletResponse.SC_UNAUTHORIZED, exception.getMessage());
    }
}