package dk.aau.cs.ds302e18.app;

import dk.aau.cs.ds302e18.app.domain.Lesson;

import java.util.Comparator;

//Sorting class that sorts lessons by date with smallest date first
public class SortLessonsByDateTime implements Comparator<Lesson> {

    @Override
    public int compare(Lesson lesson1, Lesson lesson2) {
        return lesson1.getLessonDate().compareTo(lesson2.getLessonDate());
    }
}