// package model;
// import model.contracts.LibraryContract;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import static org.junit.jupiter.api.Assertions.*;
// public class LibraryContractTest {

//     public final String userId = "user123";
//     public final String itemId = "item123";
//     public final String specificId = "specific123";
//     public final boolean enabledStatus = false;

//             LibraryContract RandomId = new LibraryContract(userId, itemId);

//             LibraryContract SpecificId = new LibraryContract(specificId, userId, itemId, enabledStatus);

//             @Test
//         void testContractWithRandomId() {
//             assertNotNull(RandomId);

//             assertEquals(userId, RandomId.getUserId(), "User ID should match the one provided");
//             assertEquals(itemId, RandomId.getItemId(), "Item ID should match the one provided");
//             assertTrue(RandomId.enabled, "Contract should be enabled by default");
//         }

//         @Test
//         void testContractWithSpecificId() {

//             assertEquals(specificId, SpecificId.getId(), "ID should match the one provided");
//             assertEquals(userId, SpecificId.getUserId(), "User ID should match the one provided");
//             assertEquals(itemId, SpecificId.getItemId(), "Item ID should match the one provided");
//             assertEquals(enabledStatus, SpecificId.enabled, "Enabled status should match the one provided");
//         }

//         @Test
//         void testSetUserId() {
//             String newUserId = "user456";
//             RandomId.setUserId(newUserId);
//             assertEquals(newUserId, RandomId.getUserId(), "User ID will updated");
//         }

//         @Test
//         void testSetItemId() {
//             String newItemId = "item456";
//             RandomId.setItemId(newItemId);
//             assertEquals(newItemId, RandomId.getItemId(), "Item ID will updated");
//         }
//     }

