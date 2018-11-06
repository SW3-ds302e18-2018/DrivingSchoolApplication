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
    public void courseAddLessons(Date startDate, int numberLessons,
                                 String location, String instructorName, int type) {
        /* Gets the ID of the last lesson created, which will be the first ID of the created course lessons */
        int lastEnteredLessonID = 0;
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT `lesson_id` FROM `lesson`");
            rs.last();
            lastEnteredLessonID = rs.getInt("lesson_id");
        } catch (Exception e) {
            e.printStackTrace();
        }
        /* All added lessons will be initialized as active */
        int lessonState = 1;


        ArrayList<Lesson> lessonList = new ArrayList<>();
        /* Expected value */
        Lesson lesson = new Lesson();
    }

    /* Creates an ArrayList of dates with the dates an course should contain. The dates are set to start at
     * the same time at specified weekdays starting from a specified startDate retrieved from an Date object. */
    public ArrayList<Date> createLessonDates(Date startDate, ArrayList<Integer> weekdays, int numberLessonsLeft) {
        ArrayList<Date> lessonDates = new ArrayList<>();

        int oneDayInMilliseconds = 86400000;
        final int oneDay = oneDayInMilliseconds;
        int twoDays = oneDayInMilliseconds * 2;
        int threeDays = oneDayInMilliseconds * 3;
        int fourDays = oneDayInMilliseconds * 4;
        int fiveDays = oneDayInMilliseconds * 5;
        int sixDays = oneDayInMilliseconds * 6;

        Date currentWeekStart = startDate;

        /* While all lessons has not yet been distributed */
        while (numberLessonsLeft > 0) {
            int weekCount = 1;
            /* For every weekday add an corresponding lesson date. */
            for (int k = 0; k<weekdays.size(); k++) {
                /* Since the for loop can add multiple lessons it is necessary to make a check before each loop if
                   there is any lessons left to be distributed. */
                if(numberLessonsLeft > 0) {
                    switch (weekdays.get(k)) {
                        case 0:
                            lessonDates.add(new Date(currentWeekStart.getTime()));
                            numberLessonsLeft--;
                            break;
                        case 1:
                            lessonDates.add(new Date(currentWeekStart.getTime() + oneDay));
                            numberLessonsLeft--;
                            break;
                        case 2:
                            lessonDates.add(new Date(currentWeekStart.getTime() + twoDays));
                            numberLessonsLeft--;
                            break;
                        case 3:
                            lessonDates.add(new Date(currentWeekStart.getTime() + threeDays));
                            numberLessonsLeft--;
                            break;
                        case 4:
                            lessonDates.add(new Date(currentWeekStart.getTime() + fourDays));
                            numberLessonsLeft--;
                            break;
                        case 5:
                            lessonDates.add(new Date(currentWeekStart.getTime() + fiveDays));
                            numberLessonsLeft--;
                            break;
                        case 6:
                            lessonDates.add(new Date(currentWeekStart.getTime() + sixDays));
                            numberLessonsLeft--;
                            break;
                    }
                }
            }
            /* 604800000 * weekCount is not contained in a variable due to the possible of reaching an overflow of most
               data-types. Date is by default suitable to handle very large numbers. */
            currentWeekStart = new Date(currentWeekStart.getTime() + 604800000 * weekCount);
        }
        return lessonDates;
    }
}
