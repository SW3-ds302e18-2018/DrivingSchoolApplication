package dk.aau.cs.ds302e18.app.controllers;

import dk.aau.cs.ds302e18.app.Notification;
import dk.aau.cs.ds302e18.app.domain.Account;
import dk.aau.cs.ds302e18.app.domain.*;
import dk.aau.cs.ds302e18.app.service.AccountService;
import dk.aau.cs.ds302e18.app.service.CourseService;
import dk.aau.cs.ds302e18.app.service.LogbookService;
import dk.aau.cs.ds302e18.app.service.StoreService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.codec.digest.DigestUtils.md5Hex;

@Controller
@RequestMapping
public class StoreController {
    private final StoreService storeService;
    private final AccountService accountService;
    private final CourseService courseService;
    private final LogbookService logbookService;

    public StoreController(StoreService storeService, AccountService accountService,
                           CourseService courseService, LogbookService logbookService){
        super();
        this.storeService = storeService;
        this.accountService = accountService;
        this.courseService = courseService;
        this.logbookService = logbookService;
    }

    /**
     * Stores all the requests in the storeadmin ArrayList, and then iterates them through to the list ararylist to only
     * get the requets with the pending tag (0), and present it for the admins.
     *
     * @param model
     * @return
     */
    @GetMapping(value = "/storeadmin")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String getStores(Model model) {
        // Fetching all requests in a list
        List<Store> storeadmin = this.storeService.getAllStoreRequests();
        // Creating a new list, to store the filtered requests
        List<Store> list = new ArrayList<>();
        // Iterates through all requests, adding the ones with state (0) into the filtered request list.
        for (Store store : storeadmin) if (store.getState() == 0) list.add(store);
        model.addAttribute("storeadmin", list);
        return "storeadmin-view";
    }

    @GetMapping(value = "/store")
    @PreAuthorize("hasAnyRole('ROLE_STUDENT', 'ROLE_ADMIN', 'ROLE_INSTRUCTOR')")
    public String getStore(Model model) {
        // Fetching all requests in a list
        List<Store> storeadmin = this.storeService.getAllStoreRequests();
        // Creating a new list, to store the filtered requests
        List<Store> list = new ArrayList<>();
        // Iterates through all requests, adding the ones with state (0) into the filtered request list.
        for (Store store : storeadmin) if (store.getState() == 0) list.add(store);

        List<Course> courses = this.courseService.getAllCourseRequests();

        List<Course> BType = new ArrayList<>();
        List<Course> BEType = new ArrayList<>();
        List<Course> AType = new ArrayList<>();

        for (Course course : courses) {
            if ((course.getCourseType() == CourseType.TYPE_B_CAR)) {
                BType.add(course);
            }
            if ((course.getCourseType() == CourseType.TYPE_BE_CAR_TRAILER)) {
                BEType.add(course);
            }
            if ((course.getCourseType() == CourseType.TYPE_A_BIKE)) {
                AType.add(course);
            }
        }

        model.addAttribute("b_car_list", BType);
        model.addAttribute("be_car_list", BEType);
        model.addAttribute("a_bike_list", AType);

        model.addAttribute("store", list);

        return "store-page";
    }

    /**
     * Get for the page.
     *
     * @return
     */
    @GetMapping(value = "/storeadmin/add")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String getAddStoreForm() {
        return "store-view";
    }


    /**
     * Posts a newly added store in the requests list on the website
     *
     * @param request
     * @param model
     * @param storeModel
     * @return
     */
    @PostMapping(value = "/storeadmin")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ModelAndView addStore(HttpServletRequest request, Model model, @ModelAttribute StoreModel storeModel) {
        /* The newly added store object is retrieved from the 8200 server.  */
        Store store = this.storeService.addStoreRequest(storeModel);
        model.addAttribute("store", store);
        request.setAttribute(View.RESPONSE_STATUS_ATTRIBUTE, HttpStatus.TEMPORARY_REDIRECT);
        return new ModelAndView("redirect:/storeadmin/" + store.getId());
    }

    /**
     * Used for admins to hard change values in each request - view the information in each request
     *
     * @param model
     * @param id
     * @return
     */
    @GetMapping(value = "/storeadmin/{id}")
    @PreAuthorize("hasRole('ROLE_STUDENT')")
    public String getStore(Model model, @PathVariable long id) {
        Store store = this.storeService.getStoreRequest(id);
        model.addAttribute("store", store);
        return "store-view";
    }

    /**
     * Used for admins to hard change values in each request - updates the request in the databasse with the id.
     *
     * @param model
     * @param id
     * @param storeModel
     * @return
     */
    @PostMapping(value = "/storeadmin/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String updateRequest(Model model, @PathVariable long id, @ModelAttribute StoreModel storeModel) {
        /* Returns an lesson that is read from the 8200 server through updateLesson. */
        Store store = this.storeService.acceptStoreRequest(id, storeModel);
        model.addAttribute("store", store);
        model.addAttribute("storeModel", new StoreModel());
        return "store-view";
    }

    /**
     * Accept Application is a RequestMapping from the accept button, taking the request id, and setting the state to 1,
     * which is accepted.
     * <p>
     * Pending ID is 0.
     *
     * @param appId
     * @param courseId
     * @param studentUsername
     * @param model
     * @param storeModel
     * @return RedirectView (storeadmin)
     */
    @RequestMapping(value = "/accept", method = RequestMethod.POST)
    public RedirectView acceptAppState(@RequestParam("appId") long appId, @RequestParam("courseIdAccept") long courseId,
                                       @RequestParam("studentUsernameAccept") String studentUsername, Model model,
                                       @ModelAttribute StoreModel storeModel,
                                       @ModelAttribute CourseModel courseModel,
                                       @ModelAttribute LogbookModel logbookModel) {
        // Predefining byte to state 1 (accepted application)
        Byte b = 1;

        // Setting the application state, course id and studentname from the request into the update model.
        storeModel.setState(b);
        storeModel.setCourseId(courseId);
        storeModel.setStudentUsername(studentUsername);

        // Creating the storemodel with the set values above, and updaing it.
        Store store = this.storeService.acceptStoreRequest(appId, storeModel);



        /* Finds the course the student requested to join. */
        Course course = courseService.getCourse(courseId);

        /* Creates an courseObject with that course´s values. */
        CourseModel updatedCourse = course.translateCourseToModel();

        /* Adds the student. */
        String prevStudents = updatedCourse.getStudentUsernames();
        prevStudents += studentUsername + ",";
        updatedCourse.setStudentUsernames(prevStudents);

        /* Increments the number of students by one. */
        updatedCourse.setNumberStudents(course.getNumberStudents()+1);

        /* Updates the course. */
        courseService.updateCourse(courseId, updatedCourse);
      
        /* Send an notification to the student */
        Account acceptedStudent = accountService.getAccount(studentUsername);
        String studentEmail = acceptedStudent.getEmail();
        String studentFirstname = acceptedStudent.getFirstName();

        Account instructor = accountService.getAccount(course.getInstructorUsername());
        String instructorFullName = instructor.getFirstName() + " " + instructor.getLastName();


        new Notification("Hello "+studentFirstname+ ".\n" +
                "The course will start the "+course.getCourseStartDate()+" at "+course.getCourseLocation()+".\n Your instructor" +
                "will be "+instructorFullName+". If you are unable to attend this course, " +
                "please contact us as soon as possible, and at least 24 hours before the first lesson..\n" +
                "Kind regards .\n" +
                "Driving School A/S "
                , "ds302e18@gmail.com", studentEmail);

      
        /*
        Creates a logbook for the student
         */

        logbookModel.setActive(true);
        logbookModel.setCourseID(courseId);
        logbookModel.setStudent(studentUsername);
        logbookModel.setLogbookType(course.getCourseType().toString());

        logbookService.addLogbook(logbookModel);

        model.addAttribute("store", store);
        model.addAttribute("storeModel", new StoreModel());

        return new RedirectView("storeadmin");
    }

    /**
     * Deny Application is a RequestMapping from the deny button, taking the request id, and setting the state to 2,
     * which is denied.
     *
     * @param appId
     * @param courseId
     * @param studentUsername
     * @param model
     * @param storeModel
     * @return RedirectView (storeadmin)
     */
    @RequestMapping(value = "/deny", method = RequestMethod.POST)
    public RedirectView denyAppState(@RequestParam("appIdDeny") long appId, @RequestParam("courseIdDeny") long courseId,
                                     @RequestParam("studentUsernameDeny") String studentUsername, Model model,
                                     @ModelAttribute StoreModel storeModel) {
        // Predefining byte to state 2 (denied application)
        Byte b = 2;

        // Setting the application state, course id and studentname from the request into the update model.
        storeModel.setState(b);
        storeModel.setCourseId(courseId);
        storeModel.setStudentUsername(studentUsername);

        // Creating the storemodel with the set values above, and updaing it.
        Store store = this.storeService.acceptStoreRequest(appId, storeModel);

        Account acceptedStudent = accountService.getAccount(studentUsername);
        String studentEmail = acceptedStudent.getEmail();
        String studentFirstname = acceptedStudent.getFirstName();

        new Notification("Hello "+studentFirstname+ ".\n" +
               "You have been sadly declined of your request because you have not met the requirements.\n" +
                "Kind regards .\n" +
                        "Driving School A/S "
                , "ds302e18@gmail.com", studentEmail);

        model.addAttribute("store", store);
        model.addAttribute("storeModel", new StoreModel());
        return new RedirectView("storeadmin");
    }

    /**
     * Apply Post Mapping, for every time a student want to apply for a course, they post a request, where it checks
     * the id of the course, and post the request for the instructor.
     *
     * @param request
     * @param model
     * @param storeModel
     * @return index page
     */
    @PostMapping(value = "/apply")
    public String createAppState(HttpServletRequest request, Model model, @ModelAttribute StoreModel storeModel) {
        // Set the state of a lesson to Pending
        byte b = 0;
        storeModel.setState(b);

        // Setting the username of the student who applied
        storeModel.setStudentUsername(getAccountUsername());

        // Creating the store mode, to be sent to the rest server
        Store store = this.storeService.addStoreRequest(storeModel);
        String studentEmail = accountService.getAccount(getAccountUsername()).getEmail();
        new Notification("Hello .\n Kind regards .\n Driving School A/S ", "ds302e18@gmail.com", studentEmail);

        model.addAttribute("store", store);
        request.setAttribute(View.RESPONSE_STATUS_ATTRIBUTE, HttpStatus.TEMPORARY_REDIRECT);

        // Returning index after you've applied
        return "index";
    }

    @RequestMapping(value = "/applyExtraLesson", method = RequestMethod.POST)
    public RedirectView requestExtraLesson(@RequestParam("day") String date,
                                     @RequestParam("month") String month,
                                     @RequestParam("year") String year,
                                     @RequestParam("customRange2") String lessons,
                                     @ModelAttribute StoreModel storeModel) {
        String message = ("User : " + getAccountUsername() + " would like to request " + lessons + " lessons on the following date : "
        + date + " / " + month + " - " + year + ".");
        new Notification(message, "ds302e18@cs.aau.dk", "ds302e18@cs.aau.dk");
        return new RedirectView("index");
    }

    @ModelAttribute("gravatar")
    public String gravatar() {
        //Models Gravatar
        String gravatar = ("http://0.gravatar.com/avatar/" + md5Hex(accountService.getAccount(getAccountUsername()).getEmail()));
        return (gravatar);
    }

    private String getAccountUsername() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ((UserDetails) principal).getUsername();
    }

}

