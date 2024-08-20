package events;

import java.util.concurrent.*;

/**
 * An implementation of an event bus for asynchronous event handling.
 * It allows registration of event handlers and publishing of events to those handlers.
 */
public class EventBus implements IEventBus {

    // Map to hold event types and their respective handlers.
    private final ConcurrentHashMap<Class<? extends IEvent>, ConcurrentLinkedQueue<IEventHandler<?>>> handlers = new ConcurrentHashMap<>();
    // Queue to hold events that are published and waiting to be processed.
    private final BlockingQueue<IEvent> eventQueue = new LinkedBlockingQueue<>();
    // Executor service for processing events asynchronously.
    private final ExecutorService executorService;
    // Map to hold futures for published events, allowing for asynchronous results.
    private final ConcurrentHashMap<String, CompletableFuture<Object>> futures = new ConcurrentHashMap<>();

    /**
     * Constructs an EventBus with a fixed number of worker threads.
     *
     * @param numberOfWorkers The number of threads to process events.
     */
    public EventBus(int numberOfWorkers) {
        executorService = Executors.newFixedThreadPool(numberOfWorkers);
        for (int i = 0; i < numberOfWorkers; i++) {
            executorService.submit(this::processEvents);
        }
    }

    /**
     * Registers an event handler for a specific type of event.
     *
     * @param eventType The class of the event type.
     * @param handler   The handler for the event.
     * @param <T>       The type of the event.
     */
    @Override
    public <T extends IEvent> void registerHandler(Class<T> eventType, IEventHandler<T> handler) {
        handlers.computeIfAbsent(eventType, k -> new ConcurrentLinkedQueue<>()).add(handler);
    }

    /**
     * handlerFuture
     * Publishes an event to the event bus. Handlers registered for this event type will be notified.
     *
     * @param event The event to publish.
     * @return A CompletableFuture that will be completed once the event has been handled.
     */
    @Override
    public CompletableFuture<Object> publish(IEvent event) {
        CompletableFuture<Object> future = new CompletableFuture<>();
        String eventId = generateUniqueEventId(event);
        futures.put(eventId, future);
        eventQueue.offer(event);
        return future;
    }

    /**
     * Continuously processes events from the event queue, dispatching them to their respective handlers.
     */
    private void processEvents() {
        try {
            while (!Thread.interrupted()) {
                IEvent event = eventQueue.take();
                String eventId = generateUniqueEventId(event);
                handlers.getOrDefault(event.getClass(), new ConcurrentLinkedQueue<>())
                        .forEach(handler -> processEventHandler(event, eventId, (IEventHandler<IEvent>) handler));
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Processes an event handler, executing it and handling its completion.
     *
     * @param event   The event being processed.
     * @param eventId The unique ID of the event.
     * @param handler The handler for the event.
     */
    private <T extends IEvent> void processEventHandler(T event, String eventId, IEventHandler<T> handler) {
        try {
            CompletableFuture<Object> handlerFuture = handler.handleEvent(event);
            handlerFuture.whenComplete((result, throwable) -> {
                CompletableFuture<Object> eventFuture = futures.remove(eventId);
                if (eventFuture != null) {
                    if (throwable != null) {
                        eventFuture.completeExceptionally(throwable);
                    } else {
                        eventFuture.complete(result);
                    }
                }
            });
        } catch (Exception e) {
            CompletableFuture<Object> eventFuture = futures.remove(eventId);
            if (eventFuture != null) {
                eventFuture.completeExceptionally(e);
            }
        }
    }

    /**
     * Generates a unique identifier for an event, based on its class name and hash code.
     *
     * @param event The event for which to generate the ID.
     * @return A unique ID for the event.
     */
    private String generateUniqueEventId(IEvent event) {
        return event.getClass().getSimpleName() + "-" + event.hashCode();
    }


}

