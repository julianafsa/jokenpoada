package tech.ada.games.jokenpo.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class SecurityUtilsTest {

    @BeforeEach
    public void setup() {
        final String username = "username";
        final Authentication authentication = new UsernamePasswordAuthenticationToken(username, null);
        final SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
    }

    @Test
    public void getCurrentUserLoginTest() {
        // Given
        final String username = "username";

        // When
        final String currentUserLogin = SecurityUtils.getCurrentUserLogin();

        // Then
        assertEquals(username, currentUserLogin);
    }

    @Test
    public void getCurrentUserLoginWithPrincipalTest() {
        // Given
        final String username = "username";
        final Authentication authentication = new UsernamePasswordAuthenticationToken(username, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // When
        String currentUserLogin = SecurityUtils.getCurrentUserLogin();

        // Then
        assertEquals(username, currentUserLogin);
    }

    @Test
    public void getCurrentUserLoginWithNullAuthenticationShouldReturnNull() {
        // Given
        SecurityContextHolder.clearContext();

        // When
        String currentUserLogin = SecurityUtils.getCurrentUserLogin();

        // Then
        assertNull(currentUserLogin);
    }

}
