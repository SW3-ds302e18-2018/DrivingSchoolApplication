package dk.aau.cs.ds302e18.app;

import dk.aau.cs.ds302e18.app.domain.Course;
import java.util.Comparator;

//Sorting class that sorts lessons by date with smallest date first
public class SortByCourseID implements Comparator<Course> {

    @Override
    public int compare(Course course1, Course course2) {
        return (int)(course1.getCourseTableID()-course2.getCourseTableID());
    }
}