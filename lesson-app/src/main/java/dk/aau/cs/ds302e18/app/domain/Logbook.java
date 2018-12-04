package dk.aau.cs.ds302e18.app.domain;

public class Logbook {
    private long id;
    private long courseID;
    private String student;
    private boolean isActive;
    private String logbookType;

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

    public String getLogbookType() {
        return logbookType;
    }

    public void setLogbookType(String logbookType) {
        this.logbookType = logbookType;
    }
}
