package dk.aau.cs.ds302e18.app.domain;

import org.springframework.format.annotation.DateTimeFormat;

import java.util.ArrayList;
import java.util.Date;

public class CourseModel {
    private String studentUsernames;
    private long courseTableID;
    private CourseType courseType;
    private Date courseStartDate;
    private String courseLocation;
    private String weekdays;
    private int numberStudents;


    /* Part of courseModel that can be changed to path variables */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date startingPoint;
    private ArrayList<Integer> weekdaysIntArray;
    private int numberLessons;
    private int numberLessonsADay;
    private String studentList;
    private LessonType lessonType;
    private String instructorUsername;
    private boolean isSigned;
    private ArrayList<String> StudentNameList;
    private boolean deleteAssociatedLessons;
    private String studentToUpdate;


    public CourseType getCourseType()
    {
        return courseType;
    }

    public void setCourseType(CourseType courseType)
    {
        this.courseType = courseType;
    }

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

    public ArrayList<Integer> getWeekdaysIntArray() {
        return weekdaysIntArray;
    }

    public void setWeekdaysIntArray(ArrayList<Integer> weekdaysIntArray) {
        this.weekdaysIntArray = weekdaysIntArray;
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
                ", weekdaysIntArray=" + weekdaysIntArray +
                ", numberLessons=" + numberLessons +
                ", numberLessonsADay=" + numberLessonsADay +
                ", studentList='" + studentList + '\'' +
                ", lessonType=" + lessonType +
                ", courseLocation='" + courseLocation + '\'' +
                ", instructorUsername='" + instructorUsername + '\'' +
                ", isSigned=" + isSigned +
                ", StudentNameList=" + StudentNameList +
                '}';
    }
}
