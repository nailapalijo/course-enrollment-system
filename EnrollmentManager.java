// Handles enrollment logic 
package managers;

import models.Student;
import models.Course;
import java.util.*;

public class EnrollmentManager {
    private CourseManager courseManager;
    private UserManager userManager;

    public EnrollmentManager(CourseManager courseManager, UserManager userManager) {
        this.courseManager = courseManager;
        this.userManager = userManager;
    }

    public String enrollStudent(String studentId, String courseCode) {
        Student student = userManager.getStudent(studentId);
        Course course = courseManager.getCourse(courseCode);

        if (student == null) {
            return "Student not found.";
        }
        if (course == null) {
            return "Course not found.";
        }

        // Check if already enrolled
        if (student.getRegisteredCourses().contains(courseCode)) {
            return "Already registered for this course.";
        }

        // Check capacity
        if (!course.hasAvailableSeats()) {
            return "Course is full. Consider submitting a special registration request.";
        }

        // Check schedule conflict
        if (hasScheduleConflict(student, course)) {
            return "Schedule conflict detected.";
        }

        // Check prerequisites
        if (!hasCompletedPrerequisites(student, course)) {
            return "Prerequisites not completed.";
        }

        // Enroll student
        student.addRegisteredCourse(courseCode);
        course.addStudent(studentId);
        return "Successfully enrolled in " + course.getCourseTitle();
    }

    public String dropCourse(String studentId, String courseCode) {
        Student student = userManager.getStudent(studentId);
        Course course = courseManager.getCourse(courseCode);

        if (student == null || course == null) {
            return "Student or course not found.";
        }

        if (!student.getRegisteredCourses().contains(courseCode)) {
            return "Not registered for this course.";
        }

        student.removeRegisteredCourse(courseCode);
        course.removeStudent(studentId);
        return "Successfully dropped " + course.getCourseTitle();
    }

    public boolean hasScheduleConflict(Student student, Course newCourse) {
        String newSchedule = newCourse.getSchedule();

        for (String courseCode : student.getRegisteredCourses()) {
            Course registeredCourse = courseManager.getCourse(courseCode);
            if (registeredCourse != null) {
                if (schedulesOverlap(newSchedule, registeredCourse.getSchedule())) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean schedulesOverlap(String schedule1, String schedule2) {
        // Schedule format: "Day StartTime-EndTime" (e.g., "MW 10:00-11:30")
        if (schedule1 == null || schedule2 == null || schedule1.isEmpty() || schedule2.isEmpty()) {
            return false;
        }

        String[] parts1 = schedule1.split(" ");
        String[] parts2 = schedule2.split(" ");

        if (parts1.length < 2 || parts2.length < 2) {
            return false;
        }

        String days1 = parts1[0];
        String days2 = parts2[0];

        // Check if days overlap
        for (char day : days1.toCharArray()) {
            if (days2.indexOf(day) != -1) {
                // Days overlap, check times
                String[] times1 = parts1[1].split("-");
                String[] times2 = parts2[1].split("-");

                if (times1.length == 2 && times2.length == 2) {
                    if (timeOverlaps(times1[0], times1[1], times2[0], times2[1])) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean timeOverlaps(String start1, String end1, String start2, String end2) {
        int s1 = timeToMinutes(start1);
        int e1 = timeToMinutes(end1);
        int s2 = timeToMinutes(start2);
        int e2 = timeToMinutes(end2);

        return (s1 < e2 && e1 > s2);
    }

    private int timeToMinutes(String time) {
        String[] parts = time.split(":");
        return Integer.parseInt(parts[0]) * 60 + Integer.parseInt(parts[1]);
    }

    public boolean hasCompletedPrerequisites(Student student, Course course) {
        List<String> prerequisites = course.getPrerequisites();
        List<String> completed = student.getCompletedCourses();

        for (String prereq : prerequisites) {
            if (!completed.contains(prereq)) {
                return false;
            }
        }
        return true;
    }

    public List<Course> getStudentCourses(String studentId) {
        Student student = userManager.getStudent(studentId);
        if (student == null) {
            return new ArrayList<>();
        }

        List<Course> courses = new ArrayList<>();
        for (String courseCode : student.getRegisteredCourses()) {
            Course course = courseManager.getCourse(courseCode);
            if (course != null) {
                courses.add(course);
            }
        }
        return courses;
    }

    // Recursive method to list all prerequisites
    public List<String> listAllPrerequisites(String courseCode, Set<String> visited) {
        List<String> allPrerequisites = new ArrayList<>();

        if (visited.contains(courseCode)) {
            return allPrerequisites;
        }

        visited.add(courseCode);
        Course course = courseManager.getCourse(courseCode);

        if (course == null || course.getPrerequisites().isEmpty()) {
            return allPrerequisites;
        }

        for (String prereq : course.getPrerequisites()) {
            allPrerequisites.add(prereq);
            allPrerequisites.addAll(listAllPrerequisites(prereq, visited));
        }

        return allPrerequisites;
    }
}