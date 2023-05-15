package tech.ada.games.jokenpo.response;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AuthResponseTest {

    @Test
    void getAccessTokenTest() {
        final String accessToken = "accessToken";
        final AuthResponse authResponse  = new AuthResponse(accessToken);
        assertEquals("Bearer", authResponse.getTokenType());
        assertEquals(accessToken, authResponse.getAccessToken());
    }

    @Test
    void getTokenType() {
        final String accessToken = "accessToken";
        final AuthResponse authResponse  = new AuthResponse(accessToken);
        assertEquals("Bearer", authResponse.getTokenType());
    }

    @Test
    void setAccessToken() {
        final String accessToken = "accessToken";
        final AuthResponse authResponse  = new AuthResponse(accessToken);
        authResponse.setAccessToken(accessToken);
        assertEquals(accessToken, authResponse.getAccessToken());
    }

    @Test
    void setTokenType() {
        final String tokenType = "Bearer";
        final String accessToken = "accessToken";
        final AuthResponse authResponse  = new AuthResponse(accessToken);
        authResponse.setTokenType(tokenType);
        assertEquals(tokenType, authResponse.getTokenType());
    }

    @Test
    void testToString() {
        final String accessToken = "accessToken";
        final AuthResponse authResponse  = new AuthResponse(accessToken);
        final String expectedAuthResponseAsString = "AuthResponse(accessToken=accessToken, tokenType=Bearer)";
        assertEquals(expectedAuthResponseAsString, authResponse.toString());
    }
}