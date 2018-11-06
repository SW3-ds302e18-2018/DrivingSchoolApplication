package dk.aau.cs.ds302e18.app.auth;

import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class CourseControllerTest {
    private ArrayList<User> userList;
    private CourseController courseController;
    private Date startDate;

    @Before
    public void setUp() throws Exception {
        courseController = new CourseController();
        User user1 = new User();
        user1.setUsername("username1");

        User user2 = new User();
        user2.setUsername("username2");

        User user3 = new User();
        user3.setUsername("username3");

        userList = new ArrayList<>();
        userList.add(user1);
        userList.add(user2);
        userList.add(user3);
        startDate = new Date(118,10,6,16,0);
    }

    @Test
    public void saveUsernamesAsString() {
        String combinedUsernames = courseController.saveUsernamesAsString(userList);
        assertEquals("username1:username2:username3:", combinedUsernames);
    }

    @Test
    public void saveStringAsUsers() {
        String combinedUsernames = courseController.saveUsernamesAsString(userList);
        ArrayList<User> returnedUserList = courseController.saveStringAsUsers(combinedUsernames);
        /* Checks that the list has 3 objects */
        assertEquals(3, returnedUserList.size());
        /* Checks that every user has the proper username */
        String usernamesFromEachAccount = "";
        for(User usernames : returnedUserList) {
            usernamesFromEachAccount += usernames.getUsername() + " ";
        }
        assertEquals("username1 username2 username3 ", usernamesFromEachAccount);
    }


    @Test
    public void createNewEmptyCourse() {
        courseController.createEmptyCourse(5);
    }

    @Test
    public void createNewCourseWithInitialStudents(){
        courseController.createEmptyCourse(7,userList);
    }

    @Test
    public void addStudent() {
        courseController.addStudent(7, userList.get(1));
    }

    @Test
    public void deleteCourse() {
        courseController.deleteCourse(3);
    }

    @Test
    public void createLessonDates() {
        ArrayList<Integer> lessonPlacementsFromOffset = new ArrayList<>();
        lessonPlacementsFromOffset.add(0);
        lessonPlacementsFromOffset.add(1);
        lessonPlacementsFromOffset.add(6);
        /* An uneven amount of lessons also checks that the if statement in the for loop prevents the loop from creating
           unnecessary extra lessons. */
        ArrayList<Date> dateList = courseController.createLessonDates(startDate, lessonPlacementsFromOffset,5);
        for(Date date: dateList) {
            System.out.println("lesson date:" + date.getDate() + " weekday: " + date.getDay());
        }
    }

    @Test
    public void courseAddLessons() {
        ArrayList<Integer> lessonPlacementsFromOffset = new ArrayList<>();
        lessonPlacementsFromOffset.add(0);
        lessonPlacementsFromOffset.add(1);
        courseController.courseAddLessons(startDate,lessonPlacementsFromOffset,4, userList, 7,"AAU", "Instructor Samuel", 1);
    }
}