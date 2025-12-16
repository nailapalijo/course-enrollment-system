// Manages user-related operations 
package managers;

import models.User;
import models.Student;
import models.Advisor;
import models.Administrator;
import java.util.ArrayList;
import java.util.List;

public class UserManager {
    private List<Student> students;
    private List<Advisor> advisors;
    private List<Administrator> administrators;

    public UserManager() {
        this.students = new ArrayList<>();
        this.advisors = new ArrayList<>();
        this.administrators = new ArrayList<>();
    }

    public boolean addUser(User user) {
        if (user instanceof Student) {
            if (getStudent(user.getUserId()) == null) {
                students.add((Student) user);
                return true;
            }
        } else if (user instanceof Advisor) {
            if (getAdvisor(user.getUserId()) == null) {
                advisors.add((Advisor) user);
                return true;
            }
        } else if (user instanceof Administrator) {
            if (getAdministrator(user.getUserId()) == null) {
                administrators.add((Administrator) user);
                return true;
            }
        }
        return false;
    }

    public boolean removeUser(String userId, String role) {
        switch (role.toUpperCase()) {
            case "STUDENT":
                return students.removeIf(s -> s.getUserId().equals(userId));
            case "ADVISOR":
                return advisors.removeIf(a -> a.getUserId().equals(userId));
            case "ADMINISTRATOR":
                return administrators.removeIf(a -> a.getUserId().equals(userId));
            default:
                return false;
        }
    }

    public User authenticateUser(String userId, String password) {
        for (Student s : students) {
            if (s.getUserId().equals(userId) && s.getPassword().equals(password)) {
                return s;
            }
        }
        for (Advisor a : advisors) {
            if (a.getUserId().equals(userId) && a.getPassword().equals(password)) {
                return a;
            }
        }
        for (Administrator a : administrators) {
            if (a.getUserId().equals(userId) && a.getPassword().equals(password)) {
                return a;
            }
        }
        return null;
    }

    public Student getStudent(String studentId) {
        for (Student s : students) {
            if (s.getUserId().equals(studentId)) {
                return s;
            }
        }
        return null;
    }

    public Advisor getAdvisor(String advisorId) {
        for (Advisor a : advisors) {
            if (a.getUserId().equals(advisorId)) {
                return a;
            }
        }
        return null;
    }

    public Administrator getAdministrator(String adminId) {
        for (Administrator a : administrators) {
            if (a.getUserId().equals(adminId)) {
                return a;
            }
        }
        return null;
    }

    public List<Student> getAllStudents() {
        return new ArrayList<>(students);
    }

    public List<Advisor> getAllAdvisors() {
        return new ArrayList<>(advisors);
    }

    public List<Administrator> getAllAdministrators() {
        return new ArrayList<>(administrators);
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }

    public void setAdvisors(List<Advisor> advisors) {
        this.advisors = advisors;
    }

    public void setAdministrators(List<Administrator> administrators) {
        this.administrators = administrators;
    }
}