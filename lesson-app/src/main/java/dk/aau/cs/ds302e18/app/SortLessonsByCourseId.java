package dk.aau.cs.ds302e18.app;

import dk.aau.cs.ds302e18.app.domain.Lesson;
import java.util.Comparator;

//Sorting class that sorts lessons by CourseId with smallest id first
public class SortLessonsByCourseId implements Comparator<Lesson> {

    @Override
    public int compare(Lesson lesson1, Lesson lesson2) {
        return (int) (lesson1.getCourseId() - lesson2.getCourseId());
    }
}
