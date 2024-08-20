package data;

import data.binary.datums.*;
import model.assets.Course;
import model.assets.PhysicalItem;
import model.clients.Faculty;
import model.clients.Student;
import model.clients.User;
import model.contracts.LibraryContract;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum ObjectToDatumUtil {
    ;

    public static Datum getDatum(Object obj) {

        if (obj instanceof User) {
            return createUserDatum((User) obj);
        } else if (obj instanceof PhysicalItem) {
            return createItemDatum((PhysicalItem) obj);
        } else if (obj instanceof Course) {
            return createCourseDatum((Course) obj);
        } else if (obj instanceof LibraryContract) {
            return createLibraryContractDatum((LibraryContract) obj);
        }
        return null;
    }

    private static UserDatum createUserDatum(User user) {
        UserDatum datum = new UserDatum();
        datum.userId = Integer.parseInt(user.getId());
        datum.email = user.getEmail();
        datum.username = user.getUsername();
        datum.passwordHash = user.getPasswordHash();

        if (user instanceof Student) {
            fillStudentSpecificData((Student) user, datum);
        } else if (user instanceof Faculty) {
            fillFacultySpecificData((Faculty) user, datum);
        }

        return datum;
    }


    private static void fillStudentSpecificData(Student student, UserDatum datum) {
        datum.enabled = student.isValidated() ? (byte) 1 : 0;
        datum.setUserType(USERTYPE.STUDENT);
        List<Long> textbookIds = student.getAllTextbooks().stream()
                .map(item -> Long.parseLong(item.getId()))
                .collect(Collectors.toList());
        System.arraycopy(textbookIds.stream().mapToLong(i -> i).toArray(), 0, datum.textbooks, 0, Math.min(textbookIds.size(), 5));

        List<Long> possessionIds = student.getAllPossessions().stream()
                .map(possession -> Long.parseLong(possession.getId()))
                .collect(Collectors.toList());
        System.arraycopy(possessionIds.stream().mapToLong(i -> i).toArray(), 0, datum.possessions, 0, Math.min(possessionIds.size(), 10));
    }

    private static void fillFacultySpecificData(Faculty faculty, UserDatum datum) {
        datum.enabled = faculty.isValidated() ? (byte) 1 : 0;
        datum.setUserType(USERTYPE.FACULTY);
        List<Integer> teachingIds = faculty.getallTeaching().stream()
                .map(course -> Integer.parseInt(course.getId()))
                .collect(Collectors.toList());
        System.arraycopy(teachingIds.stream().mapToInt(i -> i).toArray(), 0, datum.teaching, 0, Math.min(teachingIds.size(), 5));

        List<Long> previousBookIds = faculty.getPreviousBooks().stream()
                .map(item -> Long.parseLong(item.getId()))
                .collect(Collectors.toList());
        System.arraycopy(previousBookIds.stream().mapToLong(i -> i).toArray(), 0, datum.previousBooks, 0, Math.min(previousBookIds.size(), 10));
    }


    private static ItemDatum createItemDatum(PhysicalItem item) {
        ItemDatum datum = new ItemDatum();
        datum.itemId = Long.parseLong(item.getId());
        datum.title = item.getTitle();
        datum.author = item.getAuthor();
        datum.enabled = item.isEnabled();
        datum.setCopiesAvailable(item.getCopiesAvailable());
        datum.setLost(item.isLost());
        datum.setLocation(item.getLocation());
        return datum;
    }

    private static CourseDatum createCourseDatum(Course course) {
        CourseDatum datum = new CourseDatum();
        datum.courseId = Integer.parseInt(course.getId());
        datum.textbookId = Long.parseLong(course.getTextbook().getId());
        datum.title = course.getTitle();
        datum.endDate = course.getEndDate().toEpochDay();

        // Assuming Course.getStudents() returns List<String> of student IDs
        List<String> studentIdStrings = course.getStudents(); // This is a List<String>
        int[] studentIds = new int[studentIdStrings.size()];

        for (int i = 0; i < studentIdStrings.size(); i++) {
            try {
                studentIds[i] = Integer.parseInt(studentIdStrings.get(i));
            } catch (NumberFormatException e) {
                // Handle the case where the string cannot be parsed to an int,
                // maybe log an error or set a default value
                studentIds[i] = -1; // Using -1 or another value to indicate an issue
            }
        }

        // Assuming CourseDatum.students is an int array and can directly take studentIds
        // Ensure that datum.students is large enough to hold all IDs or adjust logic as needed
        datum.students = studentIds.length <= datum.students.length ? studentIds : Arrays.copyOf(studentIds, datum.students.length);

        return datum;
    }


    private static LibraryContractDatum createLibraryContractDatum(LibraryContract contract) {
        LibraryContractDatum datum = new LibraryContractDatum();
        datum.id = contract.getId();
        datum.userId = contract.getUserId();
        datum.itemId = contract.getItemId();
        datum.enabled = contract.enabled ? (byte) 1 : 0;
        return datum;
    }

}
