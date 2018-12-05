package dk.aau.cs.ds302e18.app.domain;

public class StoreModel {
    private long courseId;
    private String studentUsername;
    private byte state; // state 0 pending, state 1 accepted, state 2 declined

    public long getCourseId() {
        return courseId;
    }

    public void setCourseId(long courseId) {
        this.courseId = courseId;
    }

    public String getStudentUsername() {
        return studentUsername;
    }

    public void setStudentUsername(String studentUsername) {
        this.studentUsername = studentUsername;
    }

    public byte getState() {
        return state;
    }

    public void setState(byte state) {
        this.state = state;
    }

    Store translateModelToStore() {
        Store store = new Store();
        store.setCourseId(this.courseId);
        store.setStudentUsername(this.studentUsername);
        store.setState(this.state);
        return store;
    }
}