package model.assets;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Course {
    public final String courseId;
    private final List<String> students;
    private Item textbook;
    private String title;
    private LocalDate endDate;


    public Course(Item textbook, String title, LocalDate endDate) {
        this.textbook = textbook;
        this.students = new ArrayList<>();
        this.title = title;
        this.endDate = endDate;

        SecureRandom secureRandom = new SecureRandom();
        this.courseId = String.valueOf(secureRandom.nextInt());
    }


    public Item getTextbook() {
        return textbook;
    }

    // Setters
    public void setTextbook(Item textbook) {
        this.textbook = textbook;
    }

    public List<String> getStudents() {
        return new ArrayList<>(students); // Return a copy to prevent external modification
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return courseId;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    // Methods to manipulate students list
    public void addStudent(String studentId) {
        students.add(studentId);
    }

    public boolean removeStudent(String studentId) {
        return students.remove(studentId);
    }

    public boolean isStudentEnrolled(String studentId) {
        return students.contains(studentId);
    }

    // Utility method to check if the course is currently active
    public boolean isActive() {
        return LocalDate.now().isBefore(endDate) || LocalDate.now().isEqual(endDate);
    }


}