package dk.aau.cs.ds302e18.app.auth;
import dk.aau.cs.ds302e18.app.domain.Lesson;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;


public class CourseController {
    private Connection conn;

    public CourseController() {
        this.conn = new DBConnector().createConnectionObject();
    }

    public String saveUsernamesAsString(ArrayList<User> userList) {
        String combinedString = "";
        for(User user : userList) {
            combinedString += user.getUsername() + ":";
        }
        return combinedString;
    }

    /* Separates a string into usernames and creates an list of users with those usernames. */
    public ArrayList<User> saveStringAsUsers(String studentUsernames) {
        ArrayList<User> userList = new ArrayList<>();
        String[] parts = studentUsernames.split(":");
        for(int i = 0; i< parts.length; i++){
            userList.add(new User());
            userList.get(i).setUsername(parts[i]);
        }
        return userList;
    }


    public void createEmptyCourse(int courseID) {
        try {
            /* Creates a mysql database connection */
            Statement st = conn.createStatement();
            st.executeUpdate("INSERT INTO `course`(`course_id`, `student_usernames`) VALUES (" + courseID + ",'')");

            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /* Takes an list of student users that will initially be assigned to the course as input */
    public void createEmptyCourse(int courseID, ArrayList<User> studentList) {
        try {
            Statement st = conn.createStatement();
            String usernamesAsString = saveUsernamesAsString(studentList);
            st.executeUpdate("INSERT INTO `course`(`course_id`, `student_usernames`) VALUES (" + courseID + ",'" + usernamesAsString + "')");
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void addStudent(int courseID, User studentUser) {
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT `student_usernames` FROM `course` WHERE `course_id` = "+ courseID +"");
            rs.next();
            /* Finds the current list of students */
            String usernames = rs.getString("student_usernames");
            /* Adds the new student */
            usernames += studentUser.getUsername() + ":";
            st.executeUpdate("UPDATE `course` SET`student_usernames`='"+ usernames +"' WHERE `course_id` = '"+ courseID +"'");
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeStudent(int courseID, User studentUser) {
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT `student_usernames` FROM `course` WHERE `course_id` = "+ courseID +"");
            rs.next();
            /* Finds the current list of students */
            String usernames = rs.getString("student_usernames");
            /* Removes the targeted student */
            String usernamesWithoutStudent = usernames.replace(studentUser.getUsername() + ":", "");
            st.executeUpdate("UPDATE `course` SET`student_usernames`='"+ usernamesWithoutStudent +"' WHERE `course_id` = '"+ courseID +"'");
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteCourse(int courseID) {
        try {
            Statement st = conn.createStatement();
            st.executeUpdate("DELETE FROM `course` WHERE `course_id` = '"+ courseID +"'");
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* Get list of students from course database */
    /* Create a  */
    public void courseAddLessons(Date startDate, int numberLessons, ArrayList<Integer> weekdays) {
        ArrayList<Lesson> lessonList = new ArrayList<>();
        Date tempoaryDate = new Date();
        //startDate = new Date(2018, 5, 12, 4, 3);
        while(numberLessons > 0){
            Lesson lesson = new Lesson();
            Date lessonDate =
            lesson.setLessonDate();

            numberLessons--;
        }
    }
}
