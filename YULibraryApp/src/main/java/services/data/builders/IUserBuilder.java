package services.data.builders;

import data.binary.datums.UserDatum;
import model.clients.User;

import java.util.concurrent.CompletableFuture;

public interface IUserBuilder {
    CompletableFuture<? extends User> buildUser(UserDatum datum);
}
