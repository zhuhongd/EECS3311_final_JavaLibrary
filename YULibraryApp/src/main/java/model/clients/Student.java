package model.clients;

import model.assets.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Student extends User {

    /*
     * Student doesn't "own" textbooks the same way a user rents an item, this keeps track of which textbooks
     * a student has based on their courses, but is not a possession as the user isn't leasing or renting it.
     * This also allows us to make virtual copies of the textbooks.
     */
    private final List<Item> textbooks;
    private boolean validated;

    public Student(String email, String username, String passwordHash) {
        super(email, username, passwordHash);
        this.validated = false;
        textbooks = new ArrayList<Item>();
    }
    public Student(String email, String username, String passwordHash, String id) {
        super(email, username, passwordHash,id);
        this.validated = false;
        textbooks = new ArrayList<Item>();
    }

    public boolean isValidated() {
        return validated;
    }

    public void setValidated(boolean validated) {
        this.validated = validated;
    }

    public void addTextbook(Item textbook) {
        textbooks.add(textbook);
    }

    public boolean removeTextbook(UUID itemId) {
        return textbooks.removeIf(item -> item.getId().equals(itemId));
    }

    public Item getTextbook(UUID itemId) {
        return textbooks.stream()
                .filter(p -> p.getId().equals(itemId))
                .findFirst()
                .orElse(null);
    }

    public List<Item> getAllTextbooks() {
        return new ArrayList<>(textbooks);
    }
}
