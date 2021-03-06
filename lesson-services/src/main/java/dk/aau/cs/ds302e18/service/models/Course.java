package dk.aau.cs.ds302e18.service.models;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="COURSE")
public class Course
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="COURSE_TABLE_ID")
    private long courseTableID;

    @Column(name="STUDENT_USERNAMES", nullable = false)
    private String studentUsernames;

    @Column(name="INSTRUCTOR_USERNAME")
    private String instructorUsername;

    @Column(name = "COURSE_START_DATE")
    private Date courseStartDate;

    @Column(name="COURSE_TYPE")
    @Enumerated(EnumType.STRING)
    private CourseType courseType;

    @Column(name = "COURSE_LOCATION")
    private String courseLocation;

    @Column(name = "WEEKDAYS")
    private String weekdays;

    @Column(name = "NUMBER_STUDENTS")
    private int numberStudents;

    public Course() {
        super();
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

    public String getInstructorUsername() {
        return instructorUsername;
    }

    public void setInstructorUsername(String instructorUsername) {
        this.instructorUsername = instructorUsername;
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

    public String getCourseLocation() {
        return courseLocation;
    }

    public void setCourseLocation(String courseLocation) {
        this.courseLocation = courseLocation;
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
}
