package services.credential;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtSecurityStrategy implements SecurityStrategy {

    private final SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256); // Secure random key

    @Override
    public boolean validate(IAuthToken authToken) {
        try {
            // Extract the token string from IAuthToken
            String token = authToken.getTokenString();

            Jwts.parser().setSigningKey(key).build().parseClaimsJws(token);
            return true; // Token is valid
        } catch (JwtException e) {
            return false; // Token is invalid
        }
    }


    @Override
    public IAuthToken generate(ICredentials credentials) {
        long currentTimeMillis = System.currentTimeMillis();
        Date now = new Date(currentTimeMillis);

        // Assuming token should expire in 24 hours
        long expirationMillis = currentTimeMillis + 24 * 60 * 60 * 1000;
        Date expirationDate = new Date(expirationMillis);

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", credentials.getUserId());
        claims.put("userType", credentials.getType().toString());
        claims.put("iss", "JwtSecurityStrategy");
        claims.put("iat", now);
        claims.put("exp", expirationDate);

        String token = Jwts.builder()
                .setClaims(claims)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        // Create and return a SimpleAuthToken with the token and extracted information
        return new SimpleAuthToken(
                token,
                credentials.getUserId(),
                credentials.getType(),
                expirationDate
        );
    }
}
