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


    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date startingPoint;
    private ArrayList<Integer> weekdays;
    private int numberLessons;
    private int numberLessonsADay;
    private ArrayList<String> studentList;
    private String location;
    private String instructorName;
    private LessonType lessonType;
    private List<String> StudentNameList;
    private String studentFullNames;
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

    public void setNumberLessons(int numberLessons) {
        this.numberLessons = numberLessons;
    }

    public int getNumberLessonsADay() {
        return numberLessonsADay;
    }

    public void setNumberLessonsADay(int numberLessonsADay) {
        this.numberLessonsADay = numberLessonsADay;
    }

    public ArrayList<String> getStudentList() {
        return studentList;
    }

    public void setStudentList(ArrayList<String> studentList) {
        this.studentList = studentList;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getInstructorName() {
        return instructorName;
    }

    public void setInstructorName(String instructorName) {
        this.instructorName = instructorName;
    }

    public LessonType getLessonType() {
        return lessonType;
    }

    public void setLessonType(LessonType lessonType) {
        this.lessonType = lessonType;
    }

    public List<String> getStudentNameList() {
        return StudentNameList;
    }

    public void setStudentNameList(List<String> studentNameList) {
        StudentNameList = studentNameList;
    }

    public String getStudentNamesString() {
        return studentFullNames;
    }

    public void setStudentNamesString(String studentNamesString) {
        this.studentFullNames = studentNamesString;
    }

    public CourseType getCourseType() {
        return courseType;
    }

    public void setCourseType(CourseType courseType) {
        this.courseType = courseType;
    }

    public String getStudentFullNames() {
        return studentFullNames;
    }

    public void setStudentFullNames(String studentFullNames) {
        this.studentFullNames = studentFullNames;
    }

    public Course translateModelToCourse(){
        Course course = new Course();
        course.setCourseStartDate(this.courseStartDate);
        course.setStudentUsernames(this.studentUsernames);
        course.setCourseTableID(this.courseTableID);
        course.setCourseType(this.courseType);
        return course;
    }
}
