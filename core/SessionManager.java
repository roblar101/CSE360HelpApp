package core;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class SessionManager {
    private static SessionManager instance;
    private Map<String, Session> sessions;

    private SessionManager() {
        sessions = new HashMap<>();
    }

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }
 // Create a new class 
    public boolean createClass(String className, User admin) {
        if (!sessions.containsKey(className)) {
            sessions.put(className, new Session(className, admin));
            return true;  // Class created 
        }
        return false;  // Class already exists
    }

    // Enroll a student in a class
    public boolean enrollStudent(String className, User student) {

        
        Session enrollClass = sessions.get(className); //Get the classes

        //Class was not found
        if (enrollClass == null) {
            return false;
        }

        //Enroll the student
        return enrollClass.addStudent(student); 

    }

    // Unenroll a student from a class
    public boolean unenrollStudent(String className, User student) {

        
        Session unenrollClass = sessions.get(className); //Get the classes

        //Class was not found
        if (unenrollClass == null) {
            return false;
        }
        
        unenrollClass.removeStudent(student);  //Unenroll the student
        return true;
    }


    // Get a list of all class names
    public Set<String> getAllClassNames() {

        return sessions.keySet();  // Returns all class names
    }
}
