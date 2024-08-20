package events.QueryEvents;

import events.IEvent;

//a query is : " [action] : [key] : [json data]"
public class QueryEvent implements IEvent {
    private String query; // The query string
    private Class<?> type; // The class type associated with this query

    // Constructor
    public QueryEvent(String query, Class<?> type) {
        this.query = query;
        this.type = type;
    }

    // Getter for the query
    public String getQuery() {
        return query;
    }

    // Setter for the query
    public void setQuery(String query) {
        this.query = query;
    }

    // Getter for the type
    public Class<?> getType() {
        return type;
    }

    // Setter for the type
    public void setType(Class<?> type) {
        this.type = type;
    }
}
