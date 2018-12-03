package dk.aau.cs.ds302e18.app.domain;

import org.springframework.format.annotation.DateTimeFormat;

import java.util.ArrayList;
import java.util.Date;

public class CourseModel {
    private String studentUsernames;
    private String instructorUsername;
    private long courseTableID;
    private CourseType courseType;
    private Date courseStartDate;
    private String courseLocation;
    private String weekdays;
    private int numberStudents;

    /* Spring does not recognize an checkbox RequestParameter if it is unchecked. One of the ways to avoid that is to
       bind the boolean value to an variable in a model. */
    private boolean deleteAssociatedLessons;


    public String getStudentUsernames() {
        return studentUsernames;
    }

    public void setStudentUsernames(String studentUsernames) {
        this.studentUsernames = studentUsernames;
    }


    public Date getCourseStartDate() {
        return courseStartDate;
    }

    public void setCourseStartDate(Date courseStartDate) {
        this.courseStartDate = courseStartDate;
    }

    public long getCourseTableID() {
        return courseTableID;
    }

    public void setCourseTableID(long courseTableID) {
        this.courseTableID = courseTableID;
    }

    public String getCourseLocation() {
        return courseLocation;
    }

    public void setCourseLocation(String courseLocation) {
        this.courseLocation = courseLocation;
    }

    public String getInstructorUsername() {
        return instructorUsername;
    }

    public void setInstructorUsername(String instructorUsername) {
        this.instructorUsername = instructorUsername;
    }


    public boolean isDeleteAssociatedLessons() {
        return deleteAssociatedLessons;
    }

    public void setDeleteAssociatedLessons(boolean deleteAssociatedLessons) {
        this.deleteAssociatedLessons = deleteAssociatedLessons;
    }

    public CourseType getCourseType() {
        return courseType;
    }

    public void setCourseType(CourseType courseType) {
        this.courseType = courseType;
    }

    public String getWeekdays() {
        return weekdays;
    }

    public void setWeekdays(String weekdays) {
        this.weekdays = weekdays;
    }

    public int getNumberStudents() {
        return numberStudents;
    }

    public void setNumberStudents(int numberStudents) {
        this.numberStudents = numberStudents;
    }

    @Override
    public String toString() {
        return "CourseModel{" +
                "studentUsernames='" + studentUsernames + '\'' +
                ", instructorUsername='" + instructorUsername + '\'' +
                ", courseTableID=" + courseTableID +
                ", courseType=" + courseType +
                ", courseStartDate=" + courseStartDate +
                ", courseLocation='" + courseLocation + '\'' +
                ", weekdays='" + weekdays + '\'' +
                ", numberStudents=" + numberStudents +
                ", deleteAssociatedLessons=" + deleteAssociatedLessons +
                '}';
    }
}