package dk.aau.cs.ds302e18.app.controllers;

import dk.aau.cs.ds302e18.app.auth.Account;
import dk.aau.cs.ds302e18.app.auth.AccountRespository;
import dk.aau.cs.ds302e18.app.auth.AuthGroup;
import dk.aau.cs.ds302e18.app.auth.AuthGroupRepository;
import dk.aau.cs.ds302e18.app.domain.*;
import dk.aau.cs.ds302e18.app.domain.Lesson;
import dk.aau.cs.ds302e18.app.domain.LessonModel;
import dk.aau.cs.ds302e18.app.domain.SignatureCanvas;
import dk.aau.cs.ds302e18.app.service.CourseService;
import dk.aau.cs.ds302e18.app.service.LessonService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.codec.digest.DigestUtils.md5Hex;

@Controller
@RequestMapping("/")
public class LessonController
{
    /* Mapping is what allows the java objects to be included and used by the html files.
       An object is available to the html file after it has been added as an attribute with the addAttribute method.
       What is returned is the html file it interacts with.
       The value assigned at the @GetMapping(value = ?) will be the url that redirects to that part of the website.
       @PreAuthorize defines what role is necessary to access the url. */

    private final LessonService lessonService;
    private final AccountRespository accountRespository;
    private final AuthGroupRepository authGroupRepository;
    private final CourseService courseService;


    public LessonController(LessonService lessonService, AccountRespository accountRespository,
                            AuthGroupRepository authGroupRepository, CourseService courseService) {
        super();
        this.lessonService = lessonService;
        this.accountRespository = accountRespository;
        this.authGroupRepository = authGroupRepository;
        this.courseService = courseService;
    }


    @GetMapping(value = "/canvas/{id}")
    public String getCanvasPage(HttpSession session, @PathVariable long id)
    {
        System.out.println("GETMAP" + id);
        System.out.println(session.getAttribute("testSession"));
        return "canvas";
    }

    @PostMapping(value = "/canvas/{id}")
    public String postCanvasPage(@RequestBody CanvasModel canvasModel, @PathVariable long id)
    {
        System.out.println("Received");
        System.out.println("Canvas ID" + id);

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = ((UserDetails)principal).getUsername();

        SignatureCanvas signatureCanvas = new SignatureCanvas();

        signatureCanvas.upload("p3-project", username, canvasModel.getDataUrl());

        return "canvas";
    }

    @GetMapping(value = "/lessons")
    @PreAuthorize("hasAnyRole('ROLE_STUDENT', 'ROLE_ADMIN', 'ROLE_INSTRUCTOR')")
    public String getLessons(Model model)
    {
        /* Creates an list of lessons from the return value of getAllLessons in LessonService(which is an function that gets lessons
        from the 8100 server and makes them into lesson objects and returns them as an list) */
        List<Lesson> lessons = this.lessonService.getAllLessons();

        List<Lesson> studentLessons = new ArrayList<>();
        // Iterates through all requests, adding the ones with state (0) into the filtered request list.

        //Fetches the username from the session
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = ((UserDetails) principal).getUsername();


        for (Lesson lesson : lessons)
        {
            String[] studentListArray = lesson.getStudentList().split(",");
            for (String studentUsername : studentListArray) {
                if (studentUsername.equals(username)) {
                    studentLessons.add(lesson);
                }
            }
        }

        model.addAttribute("lessons", lessons);
        model.addAttribute("specificLesson", studentLessons);
        return "lessons-view";
    }

    @GetMapping(value = "/lessons/add")
    @PreAuthorize("hasAnyRole('ROLE_INSTRUCTOR', 'ROLE_ADMIN')")
    public String getAddLessonForm(Model model)
    {
        ArrayList<Account> userAccounts = findAccountsOfType("USER");
        model.addAttribute("userAccountlist", userAccounts);

        ArrayList<Account> instrutorAccounts = findAccountsOfType("ADMIN");
        model.addAttribute("instructorAccountList", instrutorAccounts);

        List<Course> courses = this.courseService.getAllCourseRequests();
        model.addAttribute("courseList", courses);

        return "lesson-view";
    }

    /* Posts a newly added lesson in the lessons list on the website */
    @PostMapping(value = "/lessons")
    @PreAuthorize("hasAnyRole('ROLE_INSTRUCTOR', 'ROLE_ADMIN')")
    public ModelAndView addLesson(HttpServletRequest request, Model model, @ModelAttribute LessonModel lessonModel)
    {
        /* The newly added lesson object is retrieved from the 8100 server.  */
        Lesson lesson = this.lessonService.addLesson(lessonModel);

        if (lesson.getStudentList().isEmpty() | lesson.getLessonInstructor().isEmpty() | lesson.getLessonLocation().isEmpty())
        {
            if (lesson.getLessonType() != LessonType.THEORY_LESSON || lesson.getLessonType() != LessonType.PRACTICAL_LESSON)
            {
                throw new RuntimeException();
            }

            model.addAttribute("lesson", lesson);

            request.setAttribute(View.RESPONSE_STATUS_ATTRIBUTE, HttpStatus.TEMPORARY_REDIRECT);
        }
        return new ModelAndView("redirect:/lessons/" + lesson.getId());
    }

    @GetMapping(value = "/lessons/{id}")
    @PreAuthorize("hasAnyRole('ROLE_INSTRUCTOR', 'ROLE_ADMIN')")
    public String getLesson(Model model, @PathVariable long id)
    {
        Lesson lesson = this.lessonService.getLesson(id);

        ArrayList<Account> userAccounts = findAccountsOfType("USER");
        model.addAttribute("userAccountlist", userAccounts);

        ArrayList<Account> instrutorAccounts = findAccountsOfType("ADMIN");
        model.addAttribute("instructorAccountList", instrutorAccounts);

        model.addAttribute("lesson", lesson);

        List<Course> courses = this.courseService.getAllCourseRequests();
        model.addAttribute("courseList", courses);

        String lessonYear = String.valueOf(Math.addExact(1900,lesson.getLessonDate().getYear()));
        String calendar = ("" + lessonYear+"-"+lesson.getLessonDate().getMonth()+
                "-"+lesson.getLessonDate().getDate()+" "+lesson.getLessonDate().getHours()+
                ":"+lesson.getLessonDate().getMinutes());

        model.addAttribute("calendarDate", calendar);

        return "lesson-view";
    }

    @PostMapping(value = "/lessons/{id}")
    @PreAuthorize("hasAnyRole('ROLE_INSTRUCTOR', 'ROLE_ADMIN')")
    public String updateLesson(Model model, @PathVariable long id, @ModelAttribute LessonModel lessonModel)
    {
        /* Returns an lesson that is read from the 8100 server through updateCourse. */
        Lesson lesson = this.lessonService.updateLesson(id, lessonModel);
        model.addAttribute("lesson", lesson);
        model.addAttribute("lessonModel", new LessonModel());

        return "lesson-view";
    }

    @GetMapping(value = "/gdpr")
    public String getGdprPage()
    {
        return "gdpr";
    }

    @GetMapping(value = "/deletelesson/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ModelAndView deleteLesson(Model model, @PathVariable long id)
    {
        Lesson lesson = this.lessonService.getLesson(id);
        model.addAttribute("dlesson", lesson);
        this.lessonService.deleteLesson(id);
        return new ModelAndView("redirect:/lessons/");
    }


    private ArrayList<Account> findAccountsOfType(String accountType){
        List<AuthGroup> users = authGroupRepository.findAll();
        ArrayList<AuthGroup> studentsAuthList = new ArrayList<>();
        for(AuthGroup user: users){
            if(user.getAuthGroup().equals(accountType)){
                studentsAuthList.add(user);
            }
        }
        ArrayList<Account> studentAccounts = new ArrayList<>();
        for(AuthGroup studentAuth : studentsAuthList){
            studentAccounts.add(accountRespository.findByUsername(studentAuth.getUsername()));
        }
        return studentAccounts;
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
