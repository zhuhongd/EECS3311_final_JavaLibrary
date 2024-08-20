package events.ServiceEvents;

import events.IEventHandler;

import java.util.concurrent.CompletableFuture;

public class UserEventHandler implements IEventHandler<UserEvent> {
    @Override
    public CompletableFuture<Object> handleEvent(UserEvent event) {
        return null;
    }
}
