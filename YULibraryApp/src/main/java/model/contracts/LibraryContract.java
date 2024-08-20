package model.contracts;

import java.security.SecureRandom;

public class LibraryContract implements Possession {
    private final String id; // Generic identifier for possession
    public boolean enabled;
    private String userId;
    private String itemId;

    public LibraryContract(String userId, String itemId) {
        SecureRandom secureRandom = new SecureRandom();
        this.userId = userId;
        this.itemId = itemId;
        this.id = String.valueOf(secureRandom.nextInt());
        this.enabled = true;
    }

    public LibraryContract(String id, String userId, String itemId, boolean enabled) {
        this.userId = userId;
        this.itemId = itemId;
        this.id = id;
        this.enabled = enabled;
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }
}
