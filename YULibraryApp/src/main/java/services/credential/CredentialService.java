package services.credential;

import at.favre.lib.crypto.bcrypt.BCrypt;
import events.EventBus;
import events.IEventBus;
import events.QueryEvents.QueryEvent;
import jdk.jfr.Event;

import java.util.concurrent.CompletableFuture;

public class CredentialService {


    private final SecurityStrategy tokenManager;

    IEventBus eventBus;

    public CredentialService(SecurityStrategy tokenManager , IEventBus eventBus) {
        this.eventBus = eventBus;
        this.tokenManager = tokenManager;
    }

//    public CompletableFuture<IAuthToken> authenticate(ICredentials credentials){
//
//    }

    public char[] getHash(char[] password) {
        return BCrypt.withDefaults().hashToChar(12, password);
    }

    public boolean validate(IAuthToken authToken) {
        return tokenManager.validate(authToken);
    }
}
