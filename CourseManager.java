// Handles course-related operations 
package managers;

import models.Course;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CourseManager {
    private List<Course> courses;

    public CourseManager() {
        this.courses = new ArrayList<>();
    }

    public boolean addCourse(Course course) {
        if (getCourse(course.getCourseCode()) == null) {
            courses.add(course);
            return true;
        }
        return false;
    }

    public boolean removeCourse(String courseCode) {
        return courses.removeIf(c -> c.getCourseCode().equals(courseCode));
    }

    public boolean updateCourse(Course updatedCourse) {
        for (int i = 0; i < courses.size(); i++) {
            if (courses.get(i).getCourseCode().equals(updatedCourse.getCourseCode())) {
                courses.set(i, updatedCourse);
                return true;
            }
        }
        return false;
    }

    public Course getCourse(String courseCode) {
        for (Course course : courses) {
            if (course.getCourseCode().equals(courseCode)) {
                return course;
            }
        }
        return null;
    }

    public List<Course> getAllCourses() {
        return new ArrayList<>(courses);
    }

    public List<Course> getAvailableCourses() {
        return courses.stream()
                .filter(Course::hasAvailableSeats)
                .collect(Collectors.toList());
    }

    public void setCourses(List<Course> courses) {
        this.courses = courses;
    }
}