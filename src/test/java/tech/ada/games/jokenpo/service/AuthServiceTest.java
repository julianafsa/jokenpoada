package tech.ada.games.jokenpo.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import tech.ada.games.jokenpo.dto.LoginDto;
import tech.ada.games.jokenpo.response.AuthResponse;
import tech.ada.games.jokenpo.security.JwtTokenProvider;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    private AuthenticationManager authenticationManager;
    private JwtTokenProvider tokenProvider;
    private AuthService service;

    @BeforeEach
    void setUp() {
        this.authenticationManager = mock(AuthenticationManager.class);
        this.tokenProvider = mock(JwtTokenProvider.class);
        this.service = new AuthService(authenticationManager, tokenProvider);
    }

    @Test
    void loginShouldAuthenticateWithSucess() {
        // Given (Arrange)
        final LoginDto loginDto = this.buildLoginDto();
        final Authentication usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword());
        final Authentication authentication = mock(Authentication.class);
        final String token = this.buildToken();
        when(authenticationManager.authenticate(usernamePasswordAuthenticationToken))
                .thenReturn(authentication);
        when(tokenProvider.generateToken(authentication))
                .thenReturn(token);

        // When (Act)
        final AuthResponse response = service.login(loginDto);

        // Then (Assert)
        final AuthResponse expectedResponse = new AuthResponse(token);
        assertEquals(expectedResponse.getAccessToken(), response.getAccessToken(), "Tokens should be equal");
        verify(authenticationManager, times(1)).authenticate(usernamePasswordAuthenticationToken);
        verify(tokenProvider, times(1)).generateToken(authentication);
    }

    @Test
    void loginWithNotRegistredUserShouldThrowException() {
        // Given (Arrange)
        final LoginDto loginDto = this.buildInvalidLoginDto();
        final Authentication usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword());
        when(authenticationManager.authenticate(usernamePasswordAuthenticationToken))
                .thenThrow(new RuntimeException("Invalid credentials"));

        // When (Act)
        assertThrows(RuntimeException.class, () -> service.login(loginDto));

        // Then (Assert)
        verify(authenticationManager, times(1)).authenticate(usernamePasswordAuthenticationToken);
        verifyNoInteractions(tokenProvider);
    }

    private LoginDto buildLoginDto() {
        final LoginDto loginDto = new LoginDto();
        loginDto.setUsername("username");
        loginDto.setPassword("password");
        return loginDto;
    }
    private LoginDto buildInvalidLoginDto() {
        final LoginDto loginDto = new LoginDto();
        loginDto.setUsername("usernameInvalid");
        loginDto.setPassword("passwordInvalid");
        return loginDto;
    }


    private String buildToken() {
        return "accessToken";
    }
}