package services.credential;

import data.USERTYPE;

import java.util.Date;

public interface IAuthToken {
    String getTokenString();

    String getUserId();

    USERTYPE getUserType();

    Date getExpiration();
}
