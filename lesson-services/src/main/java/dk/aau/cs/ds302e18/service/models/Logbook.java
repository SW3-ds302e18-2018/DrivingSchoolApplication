package dk.aau.cs.ds302e18.service.models;

import javax.persistence.*;

@Entity
@Table(name="LOGBOOK")
public class Logbook {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="LOGBOOK_ID")
    private long id;
    @Column(name="LOGBOOK_COURSEID")
    private long courseID;
    @Column(name="LOGBOOK_STUDENT")
    private String student;
    @Column(name="LOGBOOK_ISACTIVE")
    private boolean isActive;

    public Logbook() {
        super();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getCourseID() {
        return courseID;
    }

    public void setCourseID(long courseID) {
        this.courseID = courseID;
    }

    public String getStudent() {
        return student;
    }

    public void setStudent(String student) {
        this.student = student;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
