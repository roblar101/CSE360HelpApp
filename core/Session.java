//import java.util.HashMap;
package core;



import java.util.ArrayList;

import java.util.Set;

//import java.util.HashSet;

/***
 * 
 * <p> Session </p>
 * 
 * <p> Description: This code creates Sessions, adds Users to sessions and deletes Users
 *  from sessions. We opted to name this class 'Sessions' rather than have
 *  a class names 'Class' to spare any confusion.</p>
 * 
 * 
 * @author Robert Larsen
 * 
 * @version 1.00	2024-10-18
 * 
 */

/**
 * Session class definition. This class creates new Sessions, adds Users to those sessions,
 * and can delete Users from those sessions.
 */
public class Session {
	public String name;
	public User admin;
	public static Set<User> students;
	/**
	 * This class makes new sessions. Defines there name and the admin.
	 * @param names
	 * @param admins
	 */
	/*public void Session(String namess, User admins) {
		
	}*/
	
	public Session(String namess, User admins) {
		this.name = namess;
		this.admin = admins;
	}
	/**
	 * this class adds students to the Set of students.
	 * @param student
	 * @return boolean
	 */
	public boolean addStudent(User student) {
		if (students.contains(student)) {
			return false;
		} else {
			students.add(student);
			return true;
		}
			
	}
	/**
	 * this class removes students from the Set of students.
	 * @param student
	 * @return boolean
	 */
	
	public boolean removeStudent(User student) {
		if (students.contains(student)) {
			students.remove(student);
			return true;
		} else {
			return false;
		}
	}

}
