package services.credential;

import data.USERTYPE;

public class SimpleCredentials implements ICredentials {
    private final String userId;
    private final char[] password;
    private final USERTYPE type;

    public SimpleCredentials(String userId, char[] password, USERTYPE type) {
        this.userId = userId;
        this.password = password;
        this.type = type;
    }

    @Override
    public String getUserId() {
        return userId;
    }

    @Override
    public char[] getPassword() {
        return password;
    }

    @Override
    public USERTYPE getType() {
        return type;
    }
}