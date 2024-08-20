package events.ServiceEvents;

import events.IEvent;

public class UserEvent implements IEvent {
    private final String action;
    private final String data; // This can be a userId for retrieval or JSON data for addition.
    private final Class<?> resultType; // The expected result type for the operation.

    public UserEvent(String action, String data, Class<?> resultType) {
        this.action = action;
        this.data = data;
        this.resultType = resultType;
    }

    public String getAction() {
        return action;
    }

    public String getData() {
        return data;
    }

    public Class<?> getResultType() {
        return resultType;
    }
}
