package events;

import java.util.concurrent.CompletableFuture;

public interface IEventBus {
    <T extends IEvent> void registerHandler(Class<T> evenType, IEventHandler<T> handler);

    CompletableFuture<Object> publish(IEvent event);
}
