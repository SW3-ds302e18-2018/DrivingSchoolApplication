package dk.aau.cs.ds302e18.service.models;

public class LogbookModel {

    private long courseID;
    private String student;
    private boolean isActive;

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

    public Logbook translateModelToLogbook(){
        Logbook logbook = new Logbook();
        logbook.setCourseID(this.courseID);
        logbook.setStudent(this.student);
        logbook.setActive(this.isActive);
        return logbook;
    }
}
