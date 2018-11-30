package dk.aau.cs.ds302e18.app.domain;

import org.springframework.format.annotation.DateTimeFormat;

import java.util.ArrayList;
import java.util.Date;

public class CourseModel {
    private String studentUsernames;
    private long courseTableID;
    private CourseType courseType;
    private Date courseStartDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date startingPoint;
    private ArrayList<Integer> weekdays;
    private int numberLessons;
    private int numberLessonsADay;
    private String studentList;
    private LessonType lessonType;
    private String location;
    private String instructorUsername;
    private boolean isSigned;
    private ArrayList<String> StudentNameList;
    private boolean deleteAssociatedLessons;
    private String studentToUpdate;


    public String getStudentUsernames() {
        return studentUsernames;
    }

    public void setStudentUsernames(String studentUsernames) {
        this.studentUsernames = studentUsernames;
    }

    public Date getStartingPoint() {
        return startingPoint;
    }

    public void setStartingPoint(Date startingPoint) {
        this.startingPoint = startingPoint;
    }

    public ArrayList<Integer> getWeekdays() {
        return weekdays;
    }

    public void setWeekdays(ArrayList<Integer> weekdays) {
        this.weekdays = weekdays;
    }

    public int getNumberLessons() {
        return numberLessons;
    }

    public Date getCourseStartDate() {
        return courseStartDate;
    }

    public void setCourseStartDate(Date courseStartDate) {
        this.courseStartDate = courseStartDate;
    }

    public void setNumberLessons(int numberLessons) {
        this.numberLessons = numberLessons;
    }

    public int getNumberLessonsADay() {
        return numberLessonsADay;
    }

    public void setNumberLessonsADay(int numberLessonsADay) {
        this.numberLessonsADay = numberLessonsADay;
    }

    public String getStudentList() {
        return studentList;
    }

    public void setStudentList(String studentList) {
        this.studentList = studentList;
    }

    public LessonType getLessonType() {
        return lessonType;
    }

    public void setLessonType(LessonType lessonType) {
        this.lessonType = lessonType;
    }

    public long getCourseTableID() {
        return courseTableID;
    }

    public void setCourseTableID(long courseTableID) {
        this.courseTableID = courseTableID;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getInstructorUsername() {
        return instructorUsername;
    }

    public void setInstructorUsername(String instructorUsername) {
        this.instructorUsername = instructorUsername;
    }

    public boolean isSigned() {
        return isSigned;
    }

    public void setSigned(boolean signed) {
        isSigned = signed;
    }

    public ArrayList<String> getStudentNameList() {
        return StudentNameList;
    }

    public void setStudentNameList(ArrayList<String> studentNameList) {
        StudentNameList = studentNameList;
    }

    public boolean isDeleteAssociatedLessons() {
        return deleteAssociatedLessons;
    }

    public void setDeleteAssociatedLessons(boolean deleteAssociatedLessons) {
        this.deleteAssociatedLessons = deleteAssociatedLessons;
    }

    public String getStudentToUpdate() {
        return studentToUpdate;
    }

    public void setStudentToUpdate(String studentToUpdate) {
        this.studentToUpdate = studentToUpdate;
    }

    public CourseType getCourseType() {
        return courseType;
    }

    public void setCourseType(CourseType courseType) {
        this.courseType = courseType;
    }

    public Course translateModelToCourse(){
        Course course = new Course();
        course.setInstructorUsername(this.instructorUsername);
        course.setStudentUsernames(this.studentUsernames);
        return course;
    }

    @Override
    public String toString() {
        return "CourseModel{" +
                "studentUsernames='" + studentUsernames + '\'' +
                ", courseTableID=" + courseTableID +
                ", startingPoint=" + startingPoint +
                ", weekdays=" + weekdays +
                ", numberLessons=" + numberLessons +
                ", numberLessonsADay=" + numberLessonsADay +
                ", studentList='" + studentList + '\'' +
                ", lessonType=" + lessonType +
                ", location='" + location + '\'' +
                ", instructorUsername='" + instructorUsername + '\'' +
                ", isSigned=" + isSigned +
                ", StudentNameList=" + StudentNameList +
                '}';
    }
}
