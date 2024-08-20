package services.data;

import data.USERTYPE;
import events.IEventBus;
import services.data.builders.FacultyBuilder;
import services.data.builders.StudentBuilder;
import services.data.builders.IUserBuilder;
public class UserBuilderFactory {
    private final IEventBus eventBus;

    public UserBuilderFactory(IEventBus eventBus) {
        this.eventBus = eventBus;
    }

    public IUserBuilder getBuilder(USERTYPE type) {
        switch (type) {
            case STUDENT:
                return new StudentBuilder(eventBus);
            case FACULTY:
                return new FacultyBuilder(eventBus); // Implement FacultyBuilder similar to StudentBuilder
            default:
                throw new IllegalArgumentException("Unknown user type: " + type);
        }
    }
}
