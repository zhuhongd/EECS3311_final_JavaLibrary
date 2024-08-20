    package services.data;

    import com.google.gson.Gson;
    import com.google.gson.GsonBuilder;
    import data.LocalDateAdapter;
    import data.binary.datums.ItemDatum;
    import data.binary.datums.LibraryContractDatum;
    import data.binary.datums.UserDatum;
    import events.EventBus;
    import events.QueryEvents.QueryEvent;

    import model.clients.User;
    import services.data.builders.IUserBuilder;

    import java.time.LocalDate;
    import java.util.concurrent.CompletableFuture;

    import java.util.stream.Collectors;
    /**
     * UserService handles operations related to user management, including adding
     * and retrieving user information from a database asynchronously.
     */
    public class UserService {
        private final EventBus eventBus;
        private final Gson gson;
        private final UserBuilderFactory builderFactory;

        public UserService(EventBus eventBus) {
            this.eventBus = eventBus;
            this.builderFactory = new UserBuilderFactory(eventBus);
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(LocalDate.class , new LocalDateAdapter());
            gson = gsonBuilder.create();
        }

        /**
         * Asynchronously adds a user to the database.
         *
         * @param user The user to be added.
         * @return A CompletableFuture that, when completed, returns a Boolean indicating
         *         the success or failure of the add operation.
         */
        public CompletableFuture<Boolean> addUser(User user) {
            String jsonData = gson.toJson(user, user.getClass());
            String query = String.format("userDB:add::%s", jsonData);

            return publishEventAndHandleResult(query, user.getClass(), Boolean.class,
                    "Unexpected result type from add operation");
        }



        /**
         * Asynchronously retrieves a user based on the user ID.
         *
         * @param userId The ID of the user to retrieve.
         * @return A CompletableFuture that, when completed, returns the Student object.
         */
        public CompletableFuture<? extends User> getUser(String userId) {
            return fetchUserDatum(userId)
                    .thenCompose(userDatum -> {
                        IUserBuilder builder = builderFactory.getBuilder(userDatum.getUserType());
                        return builder.buildUser(userDatum);
                    });
        }

        private CompletableFuture<UserDatum> fetchUserDatum(String userId) {
            return publishEventAndHandleResult("userDB:read:" + userId, User.class, UserDatum.class,
                    "Unexpected data type received");
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
    }
