package model;
    import model.assets.Course;
    import model.assets.Item;
    import model.clients.Faculty;
    import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

    import java.time.LocalDate;

    import static org.junit.jupiter.api.Assertions.*;

public class FacultyTest  {

    public final String title = "testbook123";
    public final LocalDate endDate = LocalDate.now().plusDays(15);
    public Item textbook;
    public final String email = "faculty123@yorku";
    public final String username = "professor123";
    public final String passwordHash = "password123";

    Faculty faculty = new Faculty(email, username, passwordHash);
    Course course123 = new Course(textbook,title,endDate);



        @Test
        void testFacultyConstructor() {
            assertEquals(email, faculty.getEmail(), "should equal");
            assertEquals(username, faculty.getUsername(), "should equal");
            assertEquals(passwordHash, faculty.getPasswordHash(), "should equal");
            assertFalse(faculty.isValidated(), "should not equal");
            assertTrue(faculty.getallTeaching().isEmpty(), "should empty");
            assertTrue(faculty.getPreviousBooks().isEmpty(), "should empty");
        }

        @Test
        void testValidatedSetterGetter() {
            faculty.setValidated(true);
            assertTrue(faculty.isValidated(), "should true");
        }
        @Test
        void testAddCourse() {
            faculty.addCourse(course123);
            assertTrue(faculty.getallTeaching().contains(course123), "should be one true");
        }

        @Test
        void testRemoveCourse() {
            faculty.addCourse(course123);
            faculty.removeCourse(course123);
            assertFalse(faculty.getallTeaching().contains(course123), "one will be remove");
        }

        @Test
        void testAddPreviousBook() {
            faculty.addPreviousBook(textbook);
            assertTrue(faculty.getPreviousBooks().contains(textbook), "should contain book");
        }

        @Test
        void testRemovePreviousBook() {
            faculty.addPreviousBook(textbook);
            faculty.removePreviousBook(textbook);
            assertFalse(faculty.getPreviousBooks().contains(textbook), "should not contain book");
        }
}
