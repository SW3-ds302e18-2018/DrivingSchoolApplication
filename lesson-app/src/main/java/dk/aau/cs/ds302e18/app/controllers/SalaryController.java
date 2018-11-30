package dk.aau.cs.ds302e18.app.controllers;

import dk.aau.cs.ds302e18.app.domain.Lesson;
import dk.aau.cs.ds302e18.app.domain.LessonState;
import dk.aau.cs.ds302e18.app.service.LessonService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping
public class SalaryController {
    private final LessonService lessonService;

    public SalaryController(LessonService lessonService) {
        super();
        this.lessonService = lessonService;
    }

    @GetMapping(value = {"/salary"})
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String getInstructorLogbookLessons(Model model) {
        List<Lesson> lessonList = this.lessonService.getAllLessons();
        //Creates a list, to store the admin's completed lessons for the salary
        List<Lesson> salaryLessonList = new ArrayList<>();

        //Fetches the username from the session
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = ((UserDetails) principal).getUsername();

        for (Lesson lesson : lessonList) {
            if (lesson.getLessonInstructor().equals(username) && (!lesson.getLessonState().equals(LessonState.PENDING))) {
                salaryLessonList.add(lesson);
            }
        }

        model.addAttribute("salaryLessonList", salaryLessonList);
        return "salary";
    }
}
