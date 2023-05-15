package tech.ada.games.jokenpo.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class JwtAuthenticationFilterTest {

    private final JwtTokenProvider tokenProvider = mock(JwtTokenProvider.class);
    private final UserDetailsService userDetailsService = mock(UserDetailsService.class);
    private final HttpServletRequest request = mock(HttpServletRequest.class);
    private final HttpServletResponse response = mock(HttpServletResponse.class);
    private final FilterChain chain = mock(FilterChain.class);
    private final JwtAuthenticationFilter filter = new JwtAuthenticationFilter(tokenProvider, userDetailsService);
    private final String token = "validToken";

    @Before
    public void setUp() {
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
    }

    @Test
    public void doFilterInternalWithValidTokenShouldAuthenticateUser() throws ServletException, IOException {
        // Given
        final String username = "username";
        final UserDetails userDetails = User.builder().username(username).password("password").roles("USER").build();
        when(tokenProvider.validateToken(token)).thenReturn(Boolean.TRUE);
        when(tokenProvider.getUsername(token)).thenReturn(username);
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);

        // When
        filter.doFilterInternal(request, response, chain);

        // Then
        verify(userDetailsService, times(1)).loadUserByUsername(username);
        verify(chain, times(1)).doFilter(request, response);
    }

    @Test
    public void doFilterInternalWithInvalidTokenShouldNotAuthenticateUser() throws ServletException, IOException {
        // Given
        when(tokenProvider.validateToken(token)).thenReturn(Boolean.FALSE);

        // When
        filter.doFilterInternal(request, response, chain);

        // Then
        verify(userDetailsService, never()).loadUserByUsername(anyString());
        verify(chain, times(1)).doFilter(request, response);
    }

}
