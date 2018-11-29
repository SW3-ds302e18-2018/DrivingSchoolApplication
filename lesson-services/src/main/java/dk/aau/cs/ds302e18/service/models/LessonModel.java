package dk.aau.cs.ds302e18.service.models;

import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class LessonModel
{
    private LessonType lessonType;
    private String studentList;
    private String lessonInstructor;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date lessonDate;
    private String lessonLocation;
    //final so that lesson type cannot be changed once it's been set.
    //This is to prevent lessons from changing type after compeletion.
    //It lesson needs to be changed, a new lesson must be created.
    private boolean isSigned;
    private long courseId;
    private CourseType courseType;
    private LessonState lessonState;

    public CourseType getCourseType()
    {
        return courseType;
    }

    public void setCourseType(CourseType courseType)
    {
        this.courseType = courseType;
    }

    public LessonType getLessonType()
    {
        return lessonType;
    }

    public void setLessonType(LessonType lessonType)
    {
        this.lessonType = lessonType;
    }

    public String getStudentList()
    {
        return studentList;
    }

    public void setStudentList(String studentList)
    {
        this.studentList = studentList;
    }

    public String getLessonInstructor()
    {
        return lessonInstructor;
    }

    public void setLessonInstructor(String lessonInstructor)
    {
        this.lessonInstructor = lessonInstructor;
    }

    public Date getLessonDate()
    {
        return lessonDate;
    }

    public void setLessonDate(Date lessonDate)
    {
        this.lessonDate = lessonDate;
    }

    public String getLessonLocation()
    {
        return lessonLocation;
    }

    public void setLessonLocation(String lessonLocation)
    {
        this.lessonLocation = lessonLocation;
    }

    public LessonState getLessonState()
    {
        return lessonState;
    }

    public void setLessonState(LessonState lessonState)
    {
        this.lessonState = lessonState;
    }

    public long getCourseId()
    {
        return courseId;
    }

    public void setCourseId(long courseId)
    {
        this.courseId = courseId;
    }

    public Lesson translateModelToLesson(){
        Lesson lesson = new Lesson();
        lesson.setLessonType(this.lessonType);
        lesson.setStudentList(this.studentList);
        lesson.setLessonInstructor(this.lessonInstructor);
        lesson.setLessonDate(this.lessonDate);
        lesson.setLessonLocation(this.lessonLocation);
        lesson.setLessonState(this.lessonState);
        lesson.setCourseId(this.courseId);
        return lesson;
    }
}
