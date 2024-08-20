package events;

import java.util.concurrent.CompletableFuture;

public interface IEventHandler<T extends IEvent> {
    CompletableFuture<Object> handleEvent(T event);
}
