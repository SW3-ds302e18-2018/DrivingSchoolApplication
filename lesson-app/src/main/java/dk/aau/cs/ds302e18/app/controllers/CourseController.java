package dk.aau.cs.ds302e18.app.controllers;


import dk.aau.cs.ds302e18.app.SortByCourseID;
import dk.aau.cs.ds302e18.app.auth.Account;
import dk.aau.cs.ds302e18.app.auth.AccountRespository;
import dk.aau.cs.ds302e18.app.auth.AuthGroup;
import dk.aau.cs.ds302e18.app.auth.AuthGroupRepository;
import dk.aau.cs.ds302e18.app.domain.*;
import dk.aau.cs.ds302e18.app.service.CourseService;
import dk.aau.cs.ds302e18.app.service.LessonService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

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
    public String getCourses(Model model) {
        List<Course> courses = this.courseService.getAllCourseRequests();
        setInstructorFullName(courses);
        courses.sort(new SortByCourseID());

        model.addAttribute("instructorAccounts", findAccountsOfType("INSTRUCTOR"));
        model.addAttribute("studentAccounts", findAccountsOfType("STUDENT"));
        model.addAttribute("courses", courses);
        return "courses-view";
    }


    @PostMapping(value = "/course/addCourse")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ModelAndView addCourse(@ModelAttribute CourseModel courseModel) {
        /* Prevents that NULL is added as the value, which spring cannot handle when creating and posting the course.
           After the course has been saved, the course is updated to empty values. */
        if (courseModel.getStudentUsernames() == null) {
            courseModel.setStudentUsernames("temporary value");
        }
        if (courseModel.getCourseStartDate() == null) {
            Date temporaryDate = new Date();
            courseModel.setCourseStartDate(temporaryDate);
        }
        /* Weekdays is not set when adding a course, so it also needs to be an empty value*/
        courseModel.setWeekdays("temporary value");

        /* The username selected are separated as an string array so the arrays size can be counted. */
        ArrayList<String> studentUsernamesList = saveStringsSeparatedByCommaAsArray(courseModel.getStudentUsernames());
        courseModel.setNumberStudents(studentUsernamesList.size());
        courseService.addCourse(courseModel);

        courseModel.setStudentUsernames("");
        courseModel.setCourseStartDate(null);
        courseModel.setWeekdays("");
        Course latestCourse = courseService.getLastCourseOrderedByID();
        courseService.updateCourse(latestCourse.getCourseTableID(), courseModel);


        return new ModelAndView("redirect:/course/courseAddLessons");
    }

    /* When an course has been created the instructor will be redirected to this site, where they can decide
     * if they want to assign initial theory lessons. */
    @GetMapping(value = "/course/courseAddLessons")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String getCourseAddLessonsForm(Model model) {
        List<Course> courses = this.courseService.getAllCourseRequests();
        List<Account> instructorAccounts = findAccountsOfType("INSTRUCTOR");

        model.addAttribute("instructorAccounts", instructorAccounts);
        model.addAttribute("courses", courses);
        return "course-add-lessons-view";
    }

    @PostMapping(value = "/course/courseAddLessons")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ModelAndView courseAddLessons(@ModelAttribute CourseModel courseModel,
                                         @RequestParam("numberLessons") int numberLessons,
                                         @RequestParam("startingPoint") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") Date startingPoint,
                                         @RequestParam("weekdaysIntArray") ArrayList<Integer> weekdaysIntArray,
                                         @RequestParam("numberLessonsADay") int numberLessonsADay) {
        ArrayList<Date> lessonDates = createLessonDates(startingPoint, weekdaysIntArray, numberLessons, numberLessonsADay);
        /* Converts the weekdaysIntArray into an string */
        String weekdays = "";
        for (int weekday : weekdaysIntArray) {
            weekdays += Integer.toString(weekday) + ",";
        }
        Date firstCreatedLessonDate = null;

        Course lastCourseOrderedByID = courseService.getLastCourseOrderedByID();

        /* For every lesson date, a lesson will be created */
        for (int j = 0; j < lessonDates.size(); j++) {
            Date lessonDate = lessonDates.get(j);
            LessonModel lesson = new LessonModel();
            lesson.setLessonState(LessonState.PENDING);
            lesson.setLessonDate(lessonDate);
            lesson.setLessonLocation(lastCourseOrderedByID.getCourseLocation());
            lesson.setLessonInstructor(lastCourseOrderedByID.getInstructorUsername());
            lesson.setStudentList(lastCourseOrderedByID.getStudentUsernames());
            lesson.setCourseId(lastCourseOrderedByID.getCourseTableID());
            lesson.setLessonType(LessonType.THEORY_LESSON);

            lessonService.addLesson(lesson);

            /* Notes the date of the first created lesson */
            if (j == 0) {
                firstCreatedLessonDate = new Date(lessonDate.getTime());
            }
        }


        CourseModel updatedCourse = lastCourseOrderedByID.translateCourseToModel();
        /* Updates the created latest created course start_date with the date of the first lesson created and weekdays with the string converted from the weekdaysArray. */
        updatedCourse.setWeekdays(weekdays);
        updatedCourse.setCourseStartDate(firstCreatedLessonDate);
        courseService.updateCourse(lastCourseOrderedByID.getCourseTableID(), updatedCourse);

        return new ModelAndView("redirect:/lessons");
    }


    @GetMapping(value = "/course/delete/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ModelAndView deleteCourse(@PathVariable long id, @ModelAttribute CourseModel courseModel) {
        if (courseModel.isDeleteAssociatedLessons()) {
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
    public String getCourse(Model model, @PathVariable long id) {
        Course course = this.courseService.getCourse(id);
        List<Lesson> lessons = lessonService.getAllLessons();
        ArrayList<Lesson> lessonsMatchingCourse = new ArrayList<>();
        for (Lesson lesson : lessons) {
            if (lesson.getCourseId() == id)
                lessonsMatchingCourse.add(lesson);
        }

        List<Account> studentAccounts = findAccountsOfType("STUDENT");
        List<Account> studentsBelongingToCourse = findStudentsBelongingToCourse(course);
        /* Finds all user accounts and adds those that belongs to the course in a separate arrayList */
        model.addAttribute("course", course);
        model.addAttribute("studentAccounts", studentAccounts);
        model.addAttribute("lessonsMatchingCourse", lessonsMatchingCourse);
        model.addAttribute("studentAccountsBelongingToCourse", studentsBelongingToCourse);

        return "course-view";
    }


    /* Finds student accounts. Saves the student usernames in the course as an string array. Checks if any of the
       student usernames equals any of the student accounts, and adds them to an the array studentsAccountsBelongToCourse. */
    private List<Account> findStudentsBelongingToCourse(Course course) {
        List<Account> studentAccounts = findAccountsOfType("STUDENT");
        List<String> studentUsernamesInCourseAsStringArray = saveStringsSeparatedByCommaAsArray(course.getStudentUsernames());
        List<Account> studentAccountsBelongingToCourse = new ArrayList<>();
        for (Account studentAccount : studentAccounts) {
            for (String studentUsernameInCourse : studentUsernamesInCourseAsStringArray) {
                if (studentUsernameInCourse.equals((studentAccount.getUsername())))
                    studentAccountsBelongingToCourse.add(studentAccount);
            }

        }
        return studentAccountsBelongingToCourse;
    }

    @RequestMapping(value = "/course/addStudent/{id}", method = RequestMethod.POST)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public RedirectView addStudent(@PathVariable long id, @ModelAttribute CourseModel courseModel,
                                   @RequestParam("studentToAdd") String studentToAdd) {
        Course courseBeforeUpdate = courseService.getCourse(id);
        /* Sets the courseModel with the values of the current course before it has been changed. */
        CourseModel updatedCourse = courseBeforeUpdate.translateCourseToModel();
        /* Adds the new student. */
        String studentUsernames = updatedCourse.getStudentUsernames();
        studentUsernames += studentToAdd + ",";

        updatedCourse.setStudentUsernames(studentUsernames);
        /* Increments studentNumber by one */
        updatedCourse.setNumberStudents(updatedCourse.getNumberStudents() + 1);

        courseService.updateCourse(id, updatedCourse);


        /* Updates every lesson with the student in it. */
        updateUsernamesAssociatedWithCourse(id, studentUsernames);

        return new RedirectView("redirect:/course/");
    }

    @RequestMapping(value = "/course/removeStudent/{id}", method = RequestMethod.POST)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public RedirectView removeStudent(@PathVariable long id, @ModelAttribute CourseModel courseModel,
                                      @RequestParam("studentToRemove") String studentToRemove) {
        Course courseBeforeUpdate = courseService.getCourse(id);
        /* Sets the courseModel with the values of the current course before it has been changed. */
        CourseModel updatedCourse = courseBeforeUpdate.translateCourseToModel();
        /* Removes the targeted student */
        String studentUsernames = courseBeforeUpdate.getStudentUsernames();
        String studentUsernamesWithoutRemovedStudent = studentUsernames.replace(studentToRemove + ",", "");
        updatedCourse.setStudentUsernames(studentUsernamesWithoutRemovedStudent);
        /* Decrements studentNumber by one */
        updatedCourse.setNumberStudents(updatedCourse.getNumberStudents() - 1);

        courseService.updateCourse(id, updatedCourse);

        updateUsernamesAssociatedWithCourse(id, studentUsernamesWithoutRemovedStudent);
        return new RedirectView("redirect:/course/");
    }

    /* Replaces studentUsernames in every lesson with the studentToUpdate username in it and the courseID specified, with the updatedUsernamesString */
    public void updateUsernamesAssociatedWithCourse(long courseID, String updatedUsernamesString) {
        List<Lesson> lessons = lessonService.getAllLessons();
        for (Lesson lesson : lessons) {
            /* If the student that is being added to the course is in a lesson associated with that courseID, update it. */
            System.out.println(lesson.getCourseId() == courseID);
            if (lesson.getCourseId() == courseID) {
                /* Sets the LessonModel with the values of the current lesson before it has been changed. */
                LessonModel updatedLesson = lesson.translateLessonToModel();
                /* Updates the student list */
                updatedLesson.setStudentList(updatedUsernamesString);
                lessonService.updateLesson(lesson.getId(), updatedLesson);
            }

        }
    }

    public ArrayList<Date> createLessonDates(Date startDate, ArrayList<Integer> weekdaysArrays, int numberLessonsToDistribute,
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
            if (weekdaysArrays.contains(currentDayDate.getDay())) {
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

    private ArrayList<String> saveStringsSeparatedByCommaAsArray(String string) {
        ArrayList<String> studentList = new ArrayList<>();
        String[] parts = string.split(",");
        for (String part : parts) {
            studentList.add(part);
        }
        return studentList;
    }


    private void setInstructorFullName(List<Course> courseList) {
        /*  Finds and sets the full name for every instructor in a courseList */
        for (Course course : courseList) {
            String firstName = accountRespository.findByUsername(course.getInstructorUsername()).getFirstName();
            String lastName = accountRespository.findByUsername(course.getInstructorUsername()).getLastName();
            String fullName = firstName + " " + lastName;
            course.setInstructorFullName(fullName);
        }
    }

    @ModelAttribute("gravatar")
    public String gravatar() {

        //Models Gravatar
        String gravatar = ("http://0.gravatar.com/avatar/" + md5Hex(accountRespository.findByUsername(getAccountUsername()).getEmail()));
        return (gravatar);
    }

    public String getAccountUsername() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = ((UserDetails) principal).getUsername();
        return username;
    }

    private List<Account> findAccountsOfType(String accountType) {

        List<AuthGroup> authGroups = this.authGroupRepository.findAll();
        List<Account> accountList = this.accountRespository.findAll();

        List<Account> accountsOfSelectedType = new ArrayList<>();

        /* When an account is created it is at the same time added to authGroup. Elements in a result-set are per default
           fetched with the order they were entered in the database, so account[0] will be the same as the
           authGroup[0]. This means that we do not have to manually check which authGroups matches which accounts. */
        for (int i = 0; i < accountList.size(); i++) {
            if (authGroups.get(i).getAuthGroup().equals(accountType))
                accountsOfSelectedType.add(accountList.get(i));
        }

        return accountsOfSelectedType;
    }
}
