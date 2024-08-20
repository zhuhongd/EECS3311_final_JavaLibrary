package model.clients;

import model.Storable;
import model.contracts.LibraryContract;
import model.contracts.Possession;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public abstract class User implements Storable {
    private final String userId;
    private final List<LibraryContract> possessions = new ArrayList<>();
    private String email;
    private String username;
    private String passwordHash;

    public User(String email, String username, String passwordHash) {
        this.email = email;
        this.username = username;
        this.passwordHash = passwordHash;

        SecureRandom secureRandom = new SecureRandom();
        this.userId = String.valueOf(secureRandom.nextInt());

    }

    public User(String email, String username, String passwordHash, String userId) {
        this.email = email;
        this.username = username;
        this.passwordHash = passwordHash;

        SecureRandom secureRandom = new SecureRandom();
        this.userId = userId;

    }

    public void addContract(LibraryContract possession) {
        possessions.add(possession);
    }

    public boolean removePossession(UUID itemId) {
        return possessions.removeIf(item -> item.getId().equals(itemId));
    }

    public Possession getPossession(UUID itemId) {
        return possessions.stream()
                .filter(p -> p.getId().equals(itemId))
                .findFirst()
                .orElse(null);
    }

    public List<Possession> getAllPossessions() {
        return new ArrayList<>(possessions);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getId() {
        return userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(userId, user.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }
}

