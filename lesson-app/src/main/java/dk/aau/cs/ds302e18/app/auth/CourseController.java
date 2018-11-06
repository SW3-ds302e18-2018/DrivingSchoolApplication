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
        for (User user : userList) {
            combinedString += user.getUsername() + ":";
        }
        return combinedString;
    }

    /* Separates a string into usernames and creates an list of users with those usernames. */
    public ArrayList<User> saveStringAsUsers(String studentUsernames) {
        ArrayList<User> userList = new ArrayList<>();
        String[] parts = studentUsernames.split(":");
        for (int i = 0; i < parts.length; i++) {
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
            ResultSet rs = st.executeQuery("SELECT `student_usernames` FROM `course` WHERE `course_id` = " + courseID + "");
            rs.next();
            /* Finds the current list of students */
            String usernames = rs.getString("student_usernames");
            /* Adds the new student */
            usernames += studentUser.getUsername() + ":";
            st.executeUpdate("UPDATE `course` SET`student_usernames`='" + usernames + "' WHERE `course_id` = '" + courseID + "'");
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeStudent(int courseID, User studentUser) {
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT `student_usernames` FROM `course` WHERE `course_id` = " + courseID + "");
            rs.next();
            /* Finds the current list of students */
            String usernames = rs.getString("student_usernames");
            /* Removes the targeted student */
            String usernamesWithoutStudent = usernames.replace(studentUser.getUsername() + ":", "");
            st.executeUpdate("UPDATE `course` SET`student_usernames`='" + usernamesWithoutStudent + "' WHERE `course_id` = '" + courseID + "'");
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteCourse(int courseID) {
        try {
            Statement st = conn.createStatement();
            st.executeUpdate("DELETE FROM `course` WHERE `course_id` = '" + courseID + "'");
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* Get list of students from course database */
    /* Prepare lesson date, lesson ID, instructor, location, type. DONE */
    /* needs to sort lessons after ID, and pick the ID incremented by one. DONE */
    /* Still needs number of hours at a time and start date should be the exact time the first lesson starts */
    /* every lesson needs an associated courseID if there are any */
    public void courseAddLessons(Date startDate, ArrayList<Integer> lessonPlacementsFromOffset, int numberLessons,
                                 ArrayList<User> userList, int courseID,
                                 String location, String instructorName, int lessonType) {

        String usernamesString = saveUsernamesAsString(userList);
        ArrayList<Date> lessonDates = createLessonDates(startDate, lessonPlacementsFromOffset, numberLessons);
        /* All added lessons will be initialized as active */
        int lessonState = 1;
        int lastEnteredLessonID;
        try {
            Statement st = conn.createStatement();
            /* Gets the ID of the last lesson created, which will be the first ID of the created course lessons */
            ResultSet rs = st.executeQuery("SELECT `lesson_id` FROM `lesson`");
            rs.last();
            lastEnteredLessonID = rs.getInt("lesson_id");

            for(int j = 0; j<lessonDates.size(); j++) {
                int lessonID = lastEnteredLessonID + 1 + j;
                String lessonDate = reformatDate(lessonDates.get(j));
                st.executeUpdate(   "INSERT INTO `lesson`(`lesson_id`, `state`, `lesson_date`, `instructor`, " +
                                        "`lesson_location`, `lesson_type`, `student_list`, `course_id`) " + "VALUES (" + lessonID + ","+ lessonState +",'"+ lessonDate +"','"+ instructorName +"','"+
                                        location + "',"+ lessonType +",'"+ usernamesString +"',"+ courseID +")");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* Creates an ArrayList of dates with the dates a course should contain. The dates are set to start at startDate
       and lessons will be distributed with the offset specified in lessonPlacementsFromOffset, with a 7 days interval.
       So if startDate is a monday, an lesson will be distributed every monday if 0 is in lessonPlacementsFromOffset.
       If the startDate is an monday and you want to add lessons every tuesday and thursday lessonPlacementsFromOffset should contain
       1 and 3, and so on.
       The dates will continue to be distributed until all lessons are distributed.
       */

    public ArrayList<Date> createLessonDates(Date startDate, ArrayList<Integer> lessonPlacementsFromOffset, int numberLessonsLeft) {

        ArrayList<Date> lessonDates = new ArrayList<>();
        int oneDayInMilliseconds = 86400000;
        Date currentWeekStart = startDate;

        /* While all lessons has not yet been distributed */
        while (numberLessonsLeft > 0) {
            int weekCount = 1;
            /* For every lessonPlacementsFromOffset add an corresponding lesson date. */
            for (int k = 0; k<lessonPlacementsFromOffset.size(); k++) {
                /* Since the for loop can add multiple lessons it is necessary to make a check before each loop if
                   there is any lessons left to be distributed. */
                if (numberLessonsLeft > 0) {
                    /* depending on the offset number  */
                    lessonDates.add(new Date(currentWeekStart.getTime() + (oneDayInMilliseconds * lessonPlacementsFromOffset.get(k))));
                    numberLessonsLeft--;
                }
            }
            /* 604800000 is an week in milliseconds. 604800000 * weekCount is not contained in a variable due to the possible of reaching an overflow of most
               data-types. Date is by default suitable to handle very large numbers. */
            currentWeekStart = new Date(currentWeekStart.getTime() + 604800000 * weekCount);
        }
        return lessonDates;
    }

    //Reformats the Java Date format to the mySQL datetime format and returns it in a String.
    //This functions works based on the Java Date format for generating a Date.
    //This means that this function counterworks the addition of +1900 in allocating a new Date.
    //Therefor any pre-formatted dates won't work with this function.
    private String reformatDate(Date date) {
        if(date == null) {
            try {
                throw new Exception("Invalid date.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        String formattetDate = (date.getYear() + 1900) + "-" + (date.getMonth() + 1) + "-" + date.getDate()
                + " " + date.getHours() + ":" + date.getMinutes() + ":" + date.getSeconds();
        return formattetDate;
    }
}
