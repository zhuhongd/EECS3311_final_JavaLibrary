package services.data.builders;

import com.google.gson.Gson;
import data.binary.datums.LibraryContractDatum;
import data.binary.datums.UserDatum;
import events.IEventBus;
import events.QueryEvents.QueryEvent;
import model.contracts.LibraryContract;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public  abstract class UserBuilder {
    final IEventBus eventBus;
    private final Gson gson = new Gson();

    public UserBuilder(IEventBus eventBus) {
        this.eventBus = eventBus;
    }

    CompletableFuture<List<LibraryContract>> getUserPossessions(UserDatum data) {
        return fetchRelatedEntities(data.possessions, "contractDB:read:", LibraryContractDatum.class,
                this::mapToLibraryContract);
    }

    private <T, R> CompletableFuture<R> publishEventAndHandleResult(String query, Class<?> requestType,
                                                                    Class<R> resultType, String errorMessage) {
        return eventBus.publish(new QueryEvent(query, requestType))
                .thenApply(result -> {
                    if (resultType.isInstance(result)) {
                        return resultType.cast(result);
                    } else {
                        throw new IllegalStateException(errorMessage);
                    }
                });
    }

    LibraryContract mapToLibraryContract(LibraryContractDatum datum) {
        // Adapt this method to create a new LibraryContract from a LibraryContractDatum
        return new LibraryContract(datum.id, datum.userId, datum.itemId, datum.enabled == 1);
    }


    <T, R> CompletableFuture<List<R>> fetchRelatedEntities(long[] ids, String baseQuery, Class<T> datumClass,
                                                           java.util.function.Function<T, R> mapper) {
        List<CompletableFuture<R>> futures = Arrays.stream(ids)
                .filter(id -> id != 0)
                .mapToObj(id -> publishEventAndHandleResult(baseQuery + id, datumClass, datumClass,
                        "Unexpected data type received")
                        .thenApply(mapper))
                .collect(Collectors.toList());

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> futures.stream().map(CompletableFuture::join).collect(Collectors.toList()));
    }

}
