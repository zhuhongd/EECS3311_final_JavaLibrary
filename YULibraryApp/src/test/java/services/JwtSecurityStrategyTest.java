package services;

import data.USERTYPE;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import services.credential.*;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class JwtSecurityStrategyTest {

    private JwtSecurityStrategy jwtSecurityStrategy;
    private SimpleCredentials credentials;

    @BeforeEach
    public void setUp() {
        jwtSecurityStrategy = new JwtSecurityStrategy();
        // Initialize with sample credentials for testing
        credentials = new SimpleCredentials("testUser", "password".toCharArray(), USERTYPE.STUDENT);
    }

    @Test
    public void testTokenGenerationAndValidation() {
        IAuthToken token = jwtSecurityStrategy.generate(credentials);
        assertNotNull(token);
        assertEquals("testUser", token.getUserId());
        assertEquals(USERTYPE.STUDENT, token.getUserType());
        assertTrue(token.getExpiration().after(new Date()));
        assertTrue(jwtSecurityStrategy.validate(token));
    }

    @Test
    public void testTokenValidationWithInvalidToken() {
        IAuthToken invalidToken = new SimpleAuthToken("invalidToken", "testUser", USERTYPE.STUDENT, new Date());
        assertFalse(jwtSecurityStrategy.validate(invalidToken));
    }

    @Test
    public void testAuthTokenProperties() {
        Date expiration = new Date(System.currentTimeMillis() + 10000); // 10 seconds from now
        SimpleAuthToken authToken = new SimpleAuthToken("token123", "user1", USERTYPE.FACULTY, expiration);
        assertEquals("token123", authToken.getTokenString());
        assertEquals("user1", authToken.getUserId());
        assertEquals(USERTYPE.FACULTY, authToken.getUserType());
        assertEquals(expiration, authToken.getExpiration());
    }

    @Test
    public void testCredentialsProperties() {
        char[] password = {'p', 'a', 's', 's'};
        SimpleCredentials credentials = new SimpleCredentials("username", password, USERTYPE.VISITOR);
        assertEquals("username", credentials.getUserId());
        assertArrayEquals(password, credentials.getPassword());
        assertEquals(USERTYPE.VISITOR, credentials.getType());
    }
}
