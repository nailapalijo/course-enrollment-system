// Handles special registration requests 
package managers;

import models.SpecialRegistrationRequest;
import models.Student;
import models.Course;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RequestManager {
    private List<SpecialRegistrationRequest> requests;
    private EnrollmentManager enrollmentManager;
    private CourseManager courseManager;
    private UserManager userManager;
    private int requestCounter;

    public RequestManager(EnrollmentManager enrollmentManager, CourseManager courseManager,
                          UserManager userManager) {
        this.requests = new ArrayList<>();
        this.enrollmentManager = enrollmentManager;
        this.courseManager = courseManager;
        this.userManager = userManager;
        this.requestCounter = 1;
    }

    public String submitRequest(String studentId, String courseCode) {
        Course course = courseManager.getCourse(courseCode);
        Student student = userManager.getStudent(studentId);

        if (course == null || student == null) {
            return "Invalid course or student.";
        }

        // Check if already has a pending request
        for (SpecialRegistrationRequest req : requests) {
            if (req.getStudentId().equals(studentId) &&
                    req.getCourseCode().equals(courseCode) &&
                    req.getStatus().equals("PENDING")) {
                return "You already have a pending request for this course.";
            }
        }

        String requestId = "REQ" + String.format("%04d", requestCounter++);
        SpecialRegistrationRequest request = new SpecialRegistrationRequest(requestId, studentId, courseCode);
        requests.add(request);
        return "Special registration request submitted successfully. Request ID: " + requestId;
    }

    public String approveRequest(String requestId, String comments) {
        SpecialRegistrationRequest request = getRequest(requestId);

        if (request == null) {
            return "Request not found.";
        }

        if (!request.getStatus().equals("PENDING")) {
            return "Request has already been processed.";
        }

        request.approve(comments);

        // Enroll the student (bypass capacity check)
        Student student = userManager.getStudent(request.getStudentId());
        Course course = courseManager.getCourse(request.getCourseCode());

        if (student != null && course != null) {
            student.addRegisteredCourse(request.getCourseCode());
            course.addStudent(request.getStudentId());
        }

        return "Request approved and student enrolled.";
    }

    public String denyRequest(String requestId, String comments) {
        SpecialRegistrationRequest request = getRequest(requestId);

        if (request == null) {
            return "Request not found.";
        }

        if (!request.getStatus().equals("PENDING")) {
            return "Request has already been processed.";
        }

        request.deny(comments);
        return "Request denied.";
    }

    public SpecialRegistrationRequest getRequest(String requestId) {
        for (SpecialRegistrationRequest req : requests) {
            if (req.getRequestId().equals(requestId)) {
                return req;
            }
        }
        return null;
    }

    public List<SpecialRegistrationRequest> getPendingRequests() {
        return requests.stream()
                .filter(r -> r.getStatus().equals("PENDING"))
                .collect(Collectors.toList());
    }

    public List<SpecialRegistrationRequest> getRequestsByStudent(String studentId) {
        return requests.stream()
                .filter(r -> r.getStudentId().equals(studentId))
                .collect(Collectors.toList());
    }

    public List<SpecialRegistrationRequest> getAllRequests() {
        return new ArrayList<>(requests);
    }

    public void setRequests(List<SpecialRegistrationRequest> requests) {
        this.requests = requests;
        updateRequestCounter();
    }

    private void updateRequestCounter() {
        int maxId = 0;
        for (SpecialRegistrationRequest req : requests) {
            String id = req.getRequestId().substring(3);
            try {
                int num = Integer.parseInt(id);
                if (num > maxId) {
                    maxId = num;
                }
            } catch (NumberFormatException e) {
                // Skip invalid IDs
            }
        }
        requestCounter = maxId + 1;
    }
}