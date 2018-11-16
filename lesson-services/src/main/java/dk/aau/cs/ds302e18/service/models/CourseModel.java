package dk.aau.cs.ds302e18.service.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CourseModel {

    private String studentUsernames;
    private Date startDate;
    private ArrayList<Integer> weekdays;
    private int numberLessons;
    private int numberLessonsADay;
    private String studentList;
    private byte lessonType;
    private int courseTick;
    private String location;
    private String instructorName;
    private boolean isSigned;

    public String getStudentUsernames() {
        return studentUsernames;
    }

    public void setStudentUsernames(String studentUsernames) {
        this.studentUsernames = studentUsernames;
    }


    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public ArrayList<Integer> getWeekdays() {
        return weekdays;
    }

    public int getNumberLessons() {
        return numberLessons;
    }

    public int getNumberLessonsADay() {
        return numberLessonsADay;
    }

    public String getStudentList() {
        return studentList;
    }

    public byte getLessonType() {
        return lessonType;
    }

    public int getCourseTick() {
        return courseTick;
    }

    public String getLocation() {
        return location;
    }

    public String getInstructorName() {
        return instructorName;
    }

    public boolean isSigned() {
        return isSigned;
    }

    public Course translateModelToCourse(){
        Course course = new Course();
        course.setStudentUsernames(this.studentUsernames);
        return course;
    }


}
