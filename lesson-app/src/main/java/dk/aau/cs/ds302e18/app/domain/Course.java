package dk.aau.cs.ds302e18.app.domain;

import org.springframework.format.annotation.DateTimeFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Course {
    private long courseTableID;
    private String studentUsernames;
    private String instructorUsername;
    private CourseType courseType;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date courseStartDate;
    private String courseLocation;
    private int numberStudents;
    private String weekdays;

    private String instructorFullName;

    public String getInstructorFullName() {
        return instructorFullName;
    }

    public void setInstructorFullName(String instructorFullName) {
        this.instructorFullName = instructorFullName;
    }

    public String getInstructorUsername() {
        return instructorUsername;
    }

    public void setInstructorUsername(String instructorUsername) {
        this.instructorUsername = instructorUsername;
    }

    public long getCourseTableID() {
        return courseTableID;
    }

    public void setCourseTableID(long courseTableID) {
        this.courseTableID = courseTableID;
    }

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

    public String getCourseLocation() {
        return courseLocation;
    }

    public void setCourseLocation(String courseLocation) {
        this.courseLocation = courseLocation;
    }

    public CourseType getCourseType() {
        return courseType;
    }

    public void setCourseType(CourseType courseType) {
        this.courseType = courseType;
    }

    public int getNumberStudents() {
        return numberStudents;
    }

    public void setNumberStudents(int numberStudents) {
        this.numberStudents = numberStudents;
    }

    public String getWeekdays() {
        return weekdays;
    }

    public void setWeekdays(String weekdays) {
        this.weekdays = weekdays;
    }

    public CourseModel translateCourseToModel(){
        CourseModel courseModel = new CourseModel();
        courseModel.setStudentUsernames(this.studentUsernames);
        courseModel.setInstructorUsername(this.instructorUsername);
        courseModel.setCourseType(this.courseType);
        courseModel.setCourseStartDate(this.courseStartDate);
        courseModel.setCourseLocation(this.courseLocation);
        courseModel.setNumberStudents(this.numberStudents);
        courseModel.setWeekdays(this.weekdays);
        return courseModel;
    }

    public Course translateModelToCourse(){
        Course course = new Course();
        course.setCourseStartDate(this.courseStartDate);
        course.setStudentUsernames(this.studentUsernames);
        course.setCourseTableID(this.courseTableID);
        course.setCourseType(this.courseType);
        course.setCourseLocation(this.courseLocation);
        course.setNumberStudents(this.numberStudents);
        return course;
    }

    @Override
    public String toString() {
        return "Course{" +
                "courseTableID=" + courseTableID +
                ", studentUsernames='" + studentUsernames + '\'' +
                ", instructorUsername='" + instructorUsername + '\'' +
                ", courseType=" + courseType +
                ", courseStartDate=" + courseStartDate +
                ", courseLocation='" + courseLocation + '\'' +
                ", numberStudents=" + numberStudents +
                ", weekdays='" + weekdays + '\'' +
                '}';
    }
}
