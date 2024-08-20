package client;

import model.clients.User;
import model.contracts.LibraryContract;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

//  didn't include getPossession and removePossession
public class Usertest {

    private User user;

    @BeforeEach
    public void setUp() {
        user = new User("test@google.com", "testUser", "123456") {
            @Override
            public void addContract(LibraryContract possession) {
                super.addContract(possession);
            }
        };
    }

    @Test
    public void testRemovePossession() {
        assertFalse(user.removePossession(UUID.randomUUID()), "Should return true ");
        assertNull(user.getPossession(UUID.randomUUID()), "should not be found");
    }

    @Test
    public void testGetPossession() {
        //      assertEquals(false, user.getPossession(UUID), "Should return the correct possession by ID");
        //    assertEquals(false, user.getPossession(UUID), "Should return the correct possession by ID");
        assertNull(user.getPossession(UUID.randomUUID()), "Should return null");
    }

    @Test
    public void testUserCreation() {
        assertNotNull(user.getId(), "should not null");
        assertEquals("test@google.com", user.getEmail(), "Email should match");
        assertEquals("testUser", user.getUsername(), "Username should match");
        assertEquals("123456", user.getPasswordHash(), "Password hash should match");
    }

    @Test
    public void testSetEmail() {
        user.setEmail("newemail@google.com");
        assertEquals("newemail@google.com", user.getEmail(), "Email should updated");
    }

    @Test
    public void testSetUsername() {
        user.setUsername("newUsername");
        assertEquals("newUsername", user.getUsername(), "Username should updated");
    }

    @Test
    public void testSetPasswordHash() {
        user.setPasswordHash("newHash");
        assertEquals("newHash", user.getPasswordHash(), "Password should updated");
    }
}

