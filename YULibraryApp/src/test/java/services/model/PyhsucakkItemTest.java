package model;


    import model.assets.PhysicalItem;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PyhsucakkItemTest {


    public final String title = "title123";
    public final String author = "author123";
    public final String location = "location123";
    public final int copies = 10;
    public final boolean isLost = false;
    public PhysicalItem item123 = new PhysicalItem(title,author);


        @Test
        void testPhysicalItemConstructor() {
            assertEquals(title, item123.getTitle());
            assertEquals(author, item123.getAuthor());
        }



        @Test
        void testLocationSetterGetter() {
            item123.setLocation(location);
            assertEquals(location, item123.getLocation());
        }



        @Test
        void testCopiesAvailableSetterGetter() {
            item123.setCopiesAvailable(copies);
            assertEquals(copies, item123.getCopiesAvailable());
        }
        @Test
        void testIsLostSetterGetter() {
            item123.setLost(isLost);
            assertEquals(isLost, item123.isLost());
        }
    }

