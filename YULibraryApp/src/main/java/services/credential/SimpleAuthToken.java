package services.credential;

import data.USERTYPE;

import java.util.Date;

public class SimpleAuthToken implements IAuthToken {
    private final String tokenString;
    private final String userId;
    private final USERTYPE userType;
    private final Date expiration;

    public SimpleAuthToken(String tokenString, String userId, USERTYPE userType, Date expiration) {
        this.tokenString = tokenString;
        this.userId = userId;
        this.userType = userType;
        this.expiration = expiration;
    }

    @Override
    public String getTokenString() {
        return tokenString;
    }

    @Override
    public String getUserId() {
        return userId;
    }

    @Override
    public USERTYPE getUserType() {
        return userType;
    }

    @Override
    public Date getExpiration() {
        return expiration;
    }
}
