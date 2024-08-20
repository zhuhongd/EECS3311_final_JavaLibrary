package model;
import model.assets.Course;
import model.assets.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CourseTest {

    public Item textbook; ; // Assuming constructor and required methods are present in Item
    public final String title = "testbook123";
    public final LocalDate endDate = LocalDate.now().plusDays(15);
    public final String studentId = "student123";
    public Course course = new Course(textbook,title,endDate);



    @Test
        void testCourseConstructor() {
            assertNotNull(course.getId(), "not null");
            assertEquals(textbook, course.getTextbook(), "Textbook should same");
            assertEquals(title, course.getTitle(), "Course title should same");
            assertEquals(endDate, course.getEndDate(), "Course end date same");
        }

        @Test
        void testGettersAndSetters() {
            Item newTextbook =textbook; // Again, assuming Item's constructor and required methods
            course.setTextbook(newTextbook);
            assertEquals(newTextbook, course.getTextbook(), "update the textbook");

            String newTitle = "test456";
            course.setTitle(newTitle);
            assertEquals(newTitle, course.getTitle(), "update the title");

            LocalDate newEndDate = LocalDate.now().plusDays(60);
            course.setEndDate(newEndDate);
            assertEquals(newEndDate, course.getEndDate(), "update the end date");
        }

        @Test
        void testStudentManipulationMethods() {
            // Test adding a student
            course.addStudent(studentId);
            assertTrue(course.getStudents().contains(studentId), "Added student should in the list");

            // Test removing a student
            course.removeStudent(studentId);
            assertFalse(course.getStudents().contains(studentId), "Removed student should can't find");

            // Test checking if a student is enrolled
            course.addStudent(studentId);
            assertTrue(course.isStudentEnrolled(studentId), "should return true");
        }

        @Test
        void testIsActiveMethod() {
            assertTrue(course.isActive(), "Course should active ");

            course.setEndDate(LocalDate.now().minusDays(1));
            assertFalse(course.isActive(), "Course should not active ");
        }
}
