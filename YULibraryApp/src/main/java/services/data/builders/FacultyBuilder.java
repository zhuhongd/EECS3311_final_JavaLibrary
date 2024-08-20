package services.data.builders;

import data.ObjectToDatumUtil;
import data.binary.datums.CourseDatum;
import data.binary.datums.ItemDatum;
import data.binary.datums.UserDatum;
import events.IEventBus;
import events.QueryEvents.QueryEvent;
import model.assets.Course;
import model.assets.PhysicalItem;
import model.clients.Faculty;
import model.clients.User;
import model.contracts.LibraryContract;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class FacultyBuilder extends UserBuilder implements IUserBuilder{
    public FacultyBuilder(IEventBus eventBus) {
        super(eventBus);
    }

    @Override
    public CompletableFuture<? extends User> buildUser(UserDatum datum) {
        return CompletableFuture.allOf(
                        getUserPossessions(datum),
                        getTeaching(datum),
                        getPreviousBooks(datum))
                .thenCompose(v -> buildFaculty(datum,
                        getUserPossessions(datum).join(),
                        getTeaching(datum).join(),
                        getPreviousBooks(datum).join()));
    }

    private CompletionStage<Faculty> buildFaculty(UserDatum datum, List<LibraryContract> contracts, List<Course> teaching, List<PhysicalItem> previousBooks ){
        Faculty f = new Faculty(datum.email, datum.username, datum.passwordHash, datum.getId() );
        contracts.forEach(f::addContract);
        teaching.forEach(f::addCourse);
        previousBooks.forEach(f::addPreviousBook);
        return  CompletableFuture.completedFuture(f);
    }

    private CompletableFuture<List<PhysicalItem>> getPreviousBooks(UserDatum datum) {
        return fetchRelatedEntities(datum.previousBooks, "itemDB:read:", ItemDatum.class, this::mapToItem);
    }

    private CompletableFuture<List<Course>> getTeaching(UserDatum datum) {
        long[] teachingLongs = Arrays.stream(datum.teaching).asLongStream().toArray();
        return fetchRelatedEntities(teachingLongs, "courseDB:read:", CourseDatum.class, this::mapToCourse);
    }

    private Course mapToCourse(CourseDatum courseDatum) {
        ItemDatum itemDatum = (ItemDatum) eventBus.publish(new QueryEvent("itemDB:read:" + courseDatum.textbookId , CourseDatum.class)).join();
        PhysicalItem item = (PhysicalItem) ObjectToDatumUtil.getDatum(itemDatum);
        Course retCoure = new Course(item,courseDatum.title, LocalDate.ofEpochDay(courseDatum.endDate));
        Arrays.stream(courseDatum.students).boxed().map(String::valueOf).forEach(retCoure::addStudent);
        return retCoure;
    }

    private PhysicalItem mapToItem(ItemDatum datum){
        PhysicalItem ret= new PhysicalItem(datum.title, datum.author);
        ret.setEnabled(datum.enabled);
        ret.setLost(datum.isLost());
        ret.setCopiesAvailable(datum.getCopiesAvailable());
        return ret;
    }

}
