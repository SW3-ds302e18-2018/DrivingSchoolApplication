package dk.aau.cs.ds302e18.app.controllers;


import dk.aau.cs.ds302e18.app.SortByCourseID;
import dk.aau.cs.ds302e18.app.auth.Account;
import dk.aau.cs.ds302e18.app.auth.AccountRespository;
import dk.aau.cs.ds302e18.app.auth.AuthGroup;
import dk.aau.cs.ds302e18.app.auth.AuthGroupRepository;
import dk.aau.cs.ds302e18.app.domain.*;
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
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.apache.commons.codec.digest.DigestUtils.md5Hex;

@Controller
@RequestMapping("/")
public class CourseController {

    private final CourseService courseService;
    private final LessonService lessonService;
    private final AccountRespository accountRespository;
    private final AuthGroupRepository authGroupRepository;

    public CourseController(CourseService courseService, LessonService lessonService, AccountRespository accountRespository, AuthGroupRepository authGroupRepository) {
        this.courseService = courseService;
        this.lessonService = lessonService;
        this.accountRespository = accountRespository;
        this.authGroupRepository = authGroupRepository;
    }

    @GetMapping(value = "/course")
    @PreAuthorize("hasAnyRole('ROLE_INSTRUCTOR', 'ROLE_ADMIN')")
    public String getCourses(Model model)
    {
        List<Course> courses = this.courseService.getAllCourseRequests();
        setInstructorFullName(courses);
        courses.sort(new SortByCourseID());

        model.addAttribute("instructorAccounts", findInstructors());
        model.addAttribute("studentAccounts", findStudents());
        model.addAttribute("courses", courses);
        return "courses-view";
    }


    @PostMapping(value = "/course/addCourse")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ModelAndView addCourse(@ModelAttribute CourseModel courseModel) {
        System.out.println("The value is: " + courseModel.getStudentUsernames());
        courseService.addCourse(courseModel);
        return new ModelAndView("redirect:/course/courseAddLessons");
    }

    /* When an course has been created the instructor will be redirected to this site, where they can decide
      * if they want to assign initial theory lessons. */
    @GetMapping(value = "/course/courseAddLessons")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String getCourseAddLessonsForm(Model model)
    {
        List<Course> courses = this.courseService.getAllCourseRequests();
        List<Account> instructorAccounts = findInstructors();

        model.addAttribute("instructorAccounts", instructorAccounts);
        model.addAttribute("courses", courses);
        return "course-add-lessons-view";
    }

    @PostMapping(value = "/course/courseAddLessons")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ModelAndView courseAddLessons(@ModelAttribute CourseModel courseModel) {
        ArrayList<Date> lessonDates = createLessonDates(courseModel.getStartingPoint(), courseModel.getWeekdays(), courseModel.getNumberLessons(), courseModel.getNumberLessonsADay());

        Course lastCourseOrderedByID = courseService.getLastCourseOrderedByID();

        /* For every lesson date, a lesson will be created */
        for (int j = 0; j < lessonDates.size(); j++) {
            Date lessonDate = lessonDates.get(j);
            LessonModel lesson = new LessonModel();
            lesson.setLessonState(LessonState.PENDING);
            lesson.setLessonDate(lessonDate);
            lesson.setLessonLocation(courseModel.getLocation());
            lesson.setLessonInstructor(lastCourseOrderedByID.getInstructorUsername());
            lesson.setStudentList(lastCourseOrderedByID.getStudentUsernames());
            lesson.setCourseId(lastCourseOrderedByID.getCourseTableID());
            lesson.setLessonType(LessonType.THEORY_LESSON);

            lessonService.addLesson(lesson);

            /* Updates the created course start_date with the date of the first lesson created. */
            if(j==0){
                CourseModel updatedCourse = new CourseModel();
                updatedCourse.setStudentUsernames(lastCourseOrderedByID.getStudentUsernames());
                updatedCourse.setInstructorUsername(lastCourseOrderedByID.getInstructorUsername());
                updatedCourse.setCourseType(lastCourseOrderedByID.getCourseType());
                updatedCourse.setCourseStartDate(lessonDate);
                courseService.updateCourse(lastCourseOrderedByID.getCourseTableID(), updatedCourse);
            }
        }
        return new ModelAndView("redirect:/lessons");
    }


    @GetMapping(value = "/course/delete/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ModelAndView deleteCourse(@PathVariable long id, @ModelAttribute CourseModel courseModel){
        if(courseModel.isDeleteAssociatedLessons()) {
            List<Lesson> allLessons = lessonService.getAllLessons();
            ArrayList<Lesson> lessonsInCourse = new ArrayList<>();
            for (Lesson lesson : allLessons) {
                if (lesson.getCourseId() == id) {
                    lessonsInCourse.add(lesson);
                }
            }
            for (Lesson lesson : lessonsInCourse) {
                lessonService.deleteLesson(lesson.getId());
            }
        }
        courseService.deleteCourse(id);
        return new ModelAndView("redirect:/course/");
    }


    @GetMapping(value = "/course/{id}")
    @PreAuthorize("hasAnyRole('ROLE_INSTRUCTOR', 'ROLE_ADMIN')")
    public String getCourse(Model model, @PathVariable long id)
    {
        Course course = this.courseService.getCourse(id);
        List<Lesson> lessons = lessonService.getAllLessons();
        ArrayList<Lesson> lessonsMatchingCourse = new ArrayList<>();
        for(Lesson lesson: lessons){
            if(lesson.getCourseId() == id)
                lessonsMatchingCourse.add(lesson);
        }

        List<Account> studentAccounts = findStudents();
        List<Account> studentsBelongingToCourse = findAccountTypeBelongingToCourse(course, "STUDENT");
        /* Finds all user accounts and adds those that belongs to the course in a separate arrayList */
        model.addAttribute("course", course);
        model.addAttribute("studentAccounts", studentAccounts);
        model.addAttribute("lessonsMatchingCourse", lessonsMatchingCourse);
        model.addAttribute("studentAccountsBelongingToCourse", studentsBelongingToCourse);

        return "course-view";
    }


    private List<Account> findAccountTypeBelongingToCourse(Course course, String accountType){
        List<Account> studentAccounts = findStudents();
        List<String> studentsInCourseAsStringArray = saveUsernameStringAsList(course.getStudentUsernames());
        List<Account> studentsBelongingToCourse = new ArrayList<>();
        for(Account studentAccount: studentAccounts){
            if(studentsInCourseAsStringArray.contains(studentAccount.getUsername()))
                studentsBelongingToCourse.add(studentAccount);
        }
        return studentsBelongingToCourse;
    }

    @RequestMapping(value = "/course/addStudent/{id}", method=RequestMethod.POST)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public RedirectView addStudent(HttpServletRequest request, @PathVariable long id, @ModelAttribute CourseModel courseModel)
    {
        Course course = courseService.getCourse(id);
        /* Sets courseType and courseStartDate from the course current values so they stay unchanged. */
        courseModel.setCourseType(course.getCourseType());
        courseModel.setCourseStartDate(course.getCourseStartDate());
        /* Sets the instructor so it doesn't get changed */
        courseModel.setInstructorUsername(course.getInstructorUsername());
        /* Finds the current list of students. If the entry is "empty", then remove it before adding the first student. */
        String studentUsernames = course.getStudentUsernames();
        if(studentUsernames.equals("empty")){
            studentUsernames = "";
        }
        /* Adds the new student */
        studentUsernames += "," + courseModel.getStudentToUpdate();
        courseModel.setStudentUsernames(studentUsernames);

        courseService.updateCourse(id, courseModel);

        request.setAttribute(View.RESPONSE_STATUS_ATTRIBUTE, HttpStatus.TEMPORARY_REDIRECT);
        return new RedirectView("redirect:/course/");
    }

    @RequestMapping(value = "/course/removeStudent/{id}", method=RequestMethod.POST)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public RedirectView removeStudent(HttpServletRequest request, @PathVariable long id, @ModelAttribute CourseModel courseModel)
    {
        Course course = courseService.getCourse(id);
        /* Sets the instructor so it doesn't get changed */
        courseModel.setInstructorUsername(course.getInstructorUsername());
        /* Finds the current list of students */
        String studentUsernames = course.getStudentUsernames();
        /* Removes the targeted student */
        String studentUsernamesWithoutRemovedStudent = studentUsernames.replace("," + courseModel.getStudentToUpdate(), "");
        courseModel.setStudentUsernames(studentUsernamesWithoutRemovedStudent);

        request.setAttribute(View.RESPONSE_STATUS_ATTRIBUTE, HttpStatus.TEMPORARY_REDIRECT);
        courseService.updateCourse(id, courseModel);
        return new RedirectView("redirect:/course/");
    }

    public ArrayList<Date> createLessonDates(Date startDate, ArrayList<Integer> weekdays, int numberLessonsToDistribute,
                                             int numberLessonsADay) {
        ArrayList<Date> lessonDates = new ArrayList<>();
        Date currentDayDate = startDate;
        int dayCount = 0;
        /* A lesson should minimum be 45 minutes according to law, and the two interviewed driving schools had a 45
           minute lesson duration. */
        int lessonDurationMinutes = 45;

        int oneMinuteInMilliseconds = 60000;
        int lessonDurationMilliseconds = oneMinuteInMilliseconds * lessonDurationMinutes;

        /* While all lessons has not yet been distributed */
        while (numberLessonsToDistribute > 0) {
            /* 86400000 * dayCount is not contained in a variable due to the possible of reaching an overflow of most
               data-types. Date is by default suitable to handle very large numbers. */
            currentDayDate = new Date(startDate.getTime() + 86400000 * dayCount);
            if (weekdays.contains(currentDayDate.getDay())) {
                /* If it is one of the weekdays specified in the weekdays array, add an number of lessons equal to
                 * number lessons a day. Also since it adds a several lessons in a loop it needs to check before each lesson
                 * is added if the necessary lessons have been distributed. */
                for (int g = 0; g < numberLessonsADay; g++) {
                    if (numberLessonsToDistribute > 0) {
                        lessonDates.add(new Date(currentDayDate.getTime() + lessonDurationMilliseconds * g));
                        numberLessonsToDistribute--;
                    }
                }
            }
            dayCount += 1;
        }
        return lessonDates;
    }

    private String saveStringListAsSingleString(ArrayList<String> studentNameList) {
        String combinedString = "";
        for (String student : studentNameList) {
            combinedString += student + ",";
        }
        return combinedString;
    }

    private ArrayList<String> saveUsernameStringAsList(String usernames) {
        ArrayList<String> studentList = new ArrayList<>();
        String[] parts = usernames.split(",");
        for(String part: parts){
            studentList.add(part);
        }
        return studentList;
    }

    private void setInstructorFullName(List<Course> courseList){
        /*  Finds and sets the full name for every instructor in a courseList */
        for(Course course: courseList){
            String firstName = accountRespository.findByUsername(course.getInstructorUsername()).getFirstName();
            String lastName = accountRespository.findByUsername(course.getInstructorUsername()).getLastName();
            String fullName = firstName + " " + lastName;
            course.setInstructorFullName(fullName);
        }
    }

    @ModelAttribute("gravatar")
    public String gravatar() {

        //Models Gravatar
        System.out.println(accountRespository.findByUsername(getAccountUsername()).getEmail());
        String gravatar = ("http://0.gravatar.com/avatar/"+md5Hex(accountRespository.findByUsername(getAccountUsername()).getEmail()));
        return (gravatar);
    }

    public String getAccountUsername()
    {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = ((UserDetails) principal).getUsername();
        return username;
    }

    private List<Account> findStudents(){

        List<AuthGroup> authGroups = this.authGroupRepository.findAll();
        List<Account> accountList = this.accountRespository.findAll();

        List<Account> studentAccounts = new ArrayList<>();

        for (int i = 0; i < accountList.size(); i++)
        {
            if (authGroups.get(i).getAuthGroup().contains("STUDENT")) studentAccounts.add(accountList.get(i));
        }

        return studentAccounts;
    }

    private List<Account> findInstructors(){

        List<AuthGroup> authGroups = this.authGroupRepository.findAll();
        List<Account> accountList = this.accountRespository.findAll();

        List<Account> instructorAccounts = new ArrayList<>();

        for (int i = 0; i < accountList.size(); i++)
        {
            if (authGroups.get(i).getAuthGroup().contains("INSTRUCTOR")) instructorAccounts.add(accountList.get(i));
        }

        return instructorAccounts;
    }
}
