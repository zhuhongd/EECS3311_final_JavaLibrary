package services.data.builders;

import com.google.gson.Gson;
import data.binary.datums.ItemDatum;
import data.binary.datums.LibraryContractDatum;
import data.binary.datums.UserDatum;
import events.EventBus;
import events.IEventBus;
import events.QueryEvents.QueryEvent;
import model.assets.PhysicalItem;
import model.clients.Student;
import model.clients.User;
import model.contracts.LibraryContract;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class StudentBuilder extends UserBuilder implements IUserBuilder{


    public StudentBuilder(IEventBus eventBus) {
        super(eventBus);
            }

    @Override
    public CompletableFuture<Student> buildUser(UserDatum datum) {
        return CompletableFuture.allOf(
                        getUserPossessions(datum),
                        getTextbooks(datum))
                .thenCompose(v -> buildStudent(datum,
                        getUserPossessions(datum).join(),
                        getTextbooks(datum).join()));
    }



    private CompletableFuture<List<PhysicalItem>> getTextbooks(UserDatum data) {
        return fetchRelatedEntities(data.textbooks, "itemDB:read:", ItemDatum.class, this::mapToItem);
    }

    private PhysicalItem mapToItem(ItemDatum datum){
        PhysicalItem ret= new PhysicalItem(datum.title, datum.author);
        ret.setEnabled(datum.enabled);
        ret.setLost(datum.isLost());
        ret.setCopiesAvailable(datum.getCopiesAvailable());
        return ret;
    }
    private CompletableFuture<Student> buildStudent(UserDatum userDatum, List<LibraryContract> contracts, List<PhysicalItem> textbooks) {
        Student student = new Student(userDatum.email, userDatum.username, userDatum.passwordHash,userDatum.getId());
        contracts.forEach(student::addContract);
        textbooks.forEach(student::addTextbook);
        return CompletableFuture.completedFuture(student);
    }

}
