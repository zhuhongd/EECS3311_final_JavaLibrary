package model.assets;

import model.Storable;

import java.security.SecureRandom;

public abstract class Item implements Storable {
    private final String itemId;
    private String title;
    private String author;
    private boolean enabled;

    public Item(String title, String author) {
        this.title = title;
        this.author = author;

        SecureRandom secureRandom = new SecureRandom();
        this.itemId = String.valueOf(secureRandom.nextInt());

        this.enabled = true; //enabled by default as this is the expected result of creating an item
    }

    public Item(String title, String author, String itemId, boolean enabled) {
        this.title = title;
        this.author = author;
        this.itemId = itemId;
        this.enabled = enabled;

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getId() {
        return itemId;
    }
}
