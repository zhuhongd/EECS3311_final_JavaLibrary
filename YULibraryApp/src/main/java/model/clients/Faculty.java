package model.clients;

import model.assets.Course;
import model.assets.Item;

import java.util.ArrayList;
import java.util.List;

public class Faculty extends User {

    private final List<Course> teaching;
    private final List<Item> previousBooks;
    private boolean validated;

    public Faculty(String email, String username, String passwordHash) {
        super(email, username, passwordHash);
        this.teaching = new ArrayList<>();
        this.previousBooks = new ArrayList<>();
        this.validated = false;
    }

    public Faculty(String email, String username, String passwordHash,String id) {
        super(email, username, passwordHash,id);
        this.teaching = new ArrayList<>();
        this.previousBooks = new ArrayList<>();
        this.validated = false;
    }

    // Getters
    public List<Course> getallTeaching() {
        return new ArrayList<>(teaching); // Return a copy to prevent external modification
    }

    public List<Item> getPreviousBooks() {
        return new ArrayList<>(previousBooks); // Similarly, return a copy for encapsulation
    }

    public boolean isValidated() {
        return validated;
    }

    // Setters
    public void setValidated(boolean validated) {
        this.validated = validated;
    }

    // Methods to manage teaching list
    public void addCourse(Course course) {
        if (!teaching.contains(course)) {
            teaching.add(course);
        }
    }

    public boolean removeCourse(Course course) {
        return teaching.remove(course);
    }

    // Methods to manage previousBooks list
    public void addPreviousBook(Item book) {
        if (!previousBooks.contains(book)) {
            previousBooks.add(book);
        }
    }

    public boolean removePreviousBook(Item book) {
        return previousBooks.remove(book);
    }

}