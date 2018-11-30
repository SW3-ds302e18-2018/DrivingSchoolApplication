package dk.aau.cs.ds302e18.app.controllers;

import dk.aau.cs.ds302e18.app.auth.AccountRespository;
import dk.aau.cs.ds302e18.app.auth.AuthGroup;
import dk.aau.cs.ds302e18.app.auth.AuthGroupRepository;
import dk.aau.cs.ds302e18.app.domain.*;
import dk.aau.cs.ds302e18.app.service.LessonService;
import dk.aau.cs.ds302e18.app.service.LogbookService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.codec.digest.DigestUtils.md5Hex;

@Controller
@RequestMapping
public class LogbookController {
    private final LessonService lessonService;
    private final LogbookService logbookService;
    private final AuthGroupRepository authGroupRepository;
    private final AccountRespository accountRespository;

    public LogbookController(LessonService lessonService, LogbookService logbookService,
                             AuthGroupRepository authGroupRepository, AccountRespository accountRespository) {
        super();
        this.lessonService = lessonService;
        this.accountRespository = accountRepository;
        this.logbookService = logbookService;
        this.authGroupRepository = authGroupRepository;
        this.accountRespository = accountRespository;
    }

    /**
     * Get for the page.
     *
     * @param model
     * @return
     */
    @GetMapping(value = {"/logbook/student"})
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public String getAllStudentLogbooks(Model model) {
        List<Logbook> logbookList = this.logbookService.getAllLogbooks();
        //Creates a list to store all of the student's logbooks
        List<Logbook> studentLogbookList = new ArrayList<>();

        //Compares all logbooks with the user's username, if they are the same add the logbook to the user's logbook list
        for (Logbook logbook : logbookList) {
            if (logbook.getStudent().equals(getAccountUsername())) {
                studentLogbookList.add(logbook);
            }
        }

        //Models the lists and username as an attribute to the website
        model.addAttribute("studentLogbookList", studentLogbookList);
        model.addAttribute("username", getAccountUsername());
        return "logbook-student";
    }


    @GetMapping(value = {"/logbook/instructor"})
    @PreAuthorize("hasAnyRole('ROLE_INSTRUCTOR', 'ROLE_ADMIN')")
    public String getInstructorLogbookLessons(Model model) {
        List<Logbook> logbookList = this.logbookService.getAllLogbooks();
        //Creates a list to store all logbooks
        List<Logbook> allLogbooksList = new ArrayList<>();

        allLogbooksList.addAll(logbookList);

        //Models the lists as an attribute to the website
        model.addAttribute("allLogbookList", allLogbooksList);
        model.addAttribute("username", getAccountUsername());

        return "logbook-instructor";
    }

    /**
     * Retrieves details about a student's specific logbook and models it for the html site
     * @param model
     * @param id
     * @return
     */
    @GetMapping(value = "/logbook/{id}")
    @PreAuthorize("hasAnyRole('ROLE_STUDENT', 'ROLE_INSTRUCTOR', 'ROLE_ADMIN')")
    public String getStudentLogbook(Model model, @PathVariable long id) {
        Logbook logbook = this.logbookService.getLogbook(id);
        List<Lesson> lessonList = this.lessonService.getAllLessons();
        //Creates a list, to store the user's lessons in the logbook
        List<Lesson> logbookLessonList = new ArrayList<>();

        //Fetches the auth group of the user. This should return a list with a single entry, since a user only has one role
        List<AuthGroup> userAuthGroupList = authGroupRepository.findByUsername(getAccountUsername());

        //If the user trying to access a logbook is it's owner or an admin, the user is allowed to access the logbook
        if (logbook.getStudent().equals(getAccountUsername()) || userAuthGroupList.get(0).getAuthGroup().equals("ADMIN")
                || userAuthGroupList.get(0).getAuthGroup().equals("INSTRUCTOR")) {

            //Compares every lesson with the logbook's courseId to sort out every unnecessary lesson. Only same Id can be added
            for (Lesson lesson : lessonList) {
                if (logbook.getCourseID() == lesson.getCourseId()) {
                    String[] lessonStudentList = lesson.getStudentList().split(",");

                    //Compares every student on the lesson's student list, if the logbook owner is on it, add the lesson to the logbook
                    for (String student : lessonStudentList) {
                       if (logbook.getStudent().equals(student)) {
                           logbookLessonList.add(lesson);
                       }
                    }
                }
            }

            //Models the lists, username and logbook id as an attribute to the website
            model.addAttribute("logbookLessonList", logbookLessonList);
            model.addAttribute("username", getAccountUsername());
            model.addAttribute("logbookId", id);
            return "logbook-view";
        }

        //Models the lists as an attribute to the website
        model.addAttribute("logbookLessonList", logbookLessonList);
        return "logbook-instructor";
    }

    @ModelAttribute("gravatar")
    public String gravatar() {

        //Models Gravatar
        System.out.println(accountRespository.findByUsername(getAccountUsername()).getEmail());
        String gravatar = ("http://0.gravatar.com/avatar/"+md5Hex(accountRespository.findByUsername(getAccountUsername()).getEmail()));
        return (gravatar);
    }

    private String getAccountUsername()
    {
        UserDetails principal = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return principal.getUsername();
    }
}