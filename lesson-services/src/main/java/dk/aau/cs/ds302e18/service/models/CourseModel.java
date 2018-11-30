package dk.aau.cs.ds302e18.service.models;

import java.util.Date;

public class CourseModel {

    private String studentUsernames;
    private String instructorUsername;
    private long courseTableID;
    private Date courseStartDate;
    private CourseType courseType;
    private String studentToDelete;

    public String getInstructorUsername() {
        return instructorUsername;
    }

    public void setInstructorUsername(String instructorUsername) {
        this.instructorUsername = instructorUsername;
    }

    public String getStudentUsernames() {
        return studentUsernames;
    }

    public void setStudentUsernames(String studentUsernames) {
        this.studentUsernames = studentUsernames;
    }

    public String getStudentToDelete() {
        return studentToDelete;
    }

    public void setStudentToDelete(String studentToDelete) {
        this.studentToDelete = studentToDelete;
    }

    public long getCourseTableID() {
        return courseTableID;
    }

    public void setCourseTableID(long courseTableID) {
        this.courseTableID = courseTableID;
    }

    public Date getCourseStartDate() {
        return courseStartDate;
    }

    public void setCourseStartDate(Date courseStartDate) {
        this.courseStartDate = courseStartDate;
    }

    public CourseType getCourseType() {
        return courseType;
    }

    public void setCourseType(CourseType courseType) {
        this.courseType = courseType;
    }

    public Course translateModelToCourse(){
        Course course = new Course();
        course.setCourseStartDate(this.courseStartDate);
        course.setStudentUsernames(this.studentUsernames);
        course.setInstructorUsername(this.instructorUsername);
        course.setCourseType(this.courseType);
        return course;
    }
}
