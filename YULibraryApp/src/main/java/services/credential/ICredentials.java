package services.credential;

import data.USERTYPE;

public interface ICredentials {
    String getUserId();

    char[] getPassword();

    USERTYPE getType();
}
