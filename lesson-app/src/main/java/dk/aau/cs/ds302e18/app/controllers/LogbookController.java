package dk.aau.cs.ds302e18.app.controllers;

import dk.aau.cs.ds302e18.app.auth.AccountRespository;
import dk.aau.cs.ds302e18.app.domain.Lesson;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.codec.digest.DigestUtils.md5Hex;

@Controller
@RequestMapping
public class LogbookController {
    private final LessonService lessonService;
    private final AccountRespository accountRespository;
    private final LogbookService logbookService;
    private final AuthGroupRepository authGroupRepository;

    public LogbookController(LessonService lessonService, LogbookService logbookService, AuthGroupRepository authGroupRepository, AccountRespository accountRepository) {
        super();
        this.lessonService = lessonService;
        this.accountRespository = accountRepository;
        this.logbookService = logbookService;
        this.authGroupRepository = authGroupRepository;
    }

    /**
     * Get for the page.
     *
     * @param model
     * @return
     */
    @GetMapping(value = {"/logbook/student"})
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public String getStudentLogbookLessons(Model model) {
        List<Lesson> lessonList = this.lessonService.getAllLessons();
        //Creates a list, to store the user's lessons in the logbook
        List<Lesson> logbookLessonList = new ArrayList<>();

        //Fetches the username from the session
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = ((UserDetails) principal).getUsername();


        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        GrantedAuthority role = userDetails.getAuthorities().iterator().next();

        for(int i = 0; i < lessonList.size(); i++) {
            if(lessonList.get(i).getStudentList().contains(username)) {
                logbookLessonList.add(lessonList.get(i));
            }
        }

        //Models the lists as an attribute to the website
        model.addAttribute("logbookLessonList", logbookLessonList);
        return "logbook-student";
    }


    @GetMapping(value = {"/logbook/instructor"})
    @PreAuthorize("hasAnyRole('ROLE_INSTRUCTOR', 'ROLE_ADMIN')")
    public String getInstructorLogbookLessons(Model model) {
        List<Lesson> lessonList = this.lessonService.getAllLessons();
        //Creates a list, to store the user's lessons in the logbook
        List<Lesson> logbookLessonList = new ArrayList<>();

        //Fetches the username from the session
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = ((UserDetails) principal).getUsername();

        for(int i = 0; i < lessonList.size(); i++) {
            if(lessonList.get(i).getStudentList().contains(username)) {
                logbookLessonList.add(lessonList.get(i));
            }
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
