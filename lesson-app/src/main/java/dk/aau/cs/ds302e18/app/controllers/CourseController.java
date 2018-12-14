package dk.aau.cs.ds302e18.app.controllers;


import dk.aau.cs.ds302e18.app.SharedMethods;
import dk.aau.cs.ds302e18.app.domain.Account;
import dk.aau.cs.ds302e18.app.SortCoursesByCourseID;
import dk.aau.cs.ds302e18.app.auth.AuthGroupRepository;
import dk.aau.cs.ds302e18.app.domain.*;
import dk.aau.cs.ds302e18.app.service.AccountService;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.apache.commons.codec.digest.DigestUtils.md5Hex;

@Controller
@RequestMapping("/")
public class CourseController {

    private final CourseService courseService;
    private final LessonService lessonService;
    private final AccountService accountService;
    private final AuthGroupRepository authGroupRepository;
    private SharedMethods sharedMethods;

    public CourseController(CourseService courseService, LessonService lessonService, AccountService accountService, AuthGroupRepository authGroupRepository) {
        this.courseService = courseService;
        this.lessonService = lessonService;
        this.accountService = accountService;
        this.authGroupRepository = authGroupRepository;
        this.sharedMethods = new SharedMethods();
    }

    @GetMapping(value = "/course")
    @PreAuthorize("hasAnyRole('ROLE_INSTRUCTOR', 'ROLE_ADMIN')")
    public String getCourses(Model model) {
        List<Course> courses = this.courseService.getAllCourses();
        sharedMethods.setInstructorFullName(courses, accountService,  true);
        courses.sort(new SortCoursesByCourseID());



        model.addAttribute("adminAccounts", sharedMethods.findAccountsOfType("ADMIN", accountService, authGroupRepository));
        model.addAttribute("instructorAccounts", sharedMethods.findAccountsOfType("INSTRUCTOR", accountService, authGroupRepository));
        model.addAttribute("studentAccounts", sharedMethods.findAccountsOfType("STUDENT", accountService, authGroupRepository));
        model.addAttribute("courses", courses);
        return "courses-view";
    }


    @PostMapping(value = "/course/addCourse")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ModelAndView addCourse(@ModelAttribute CourseModel courseModel) {
        /* Students and start date is not set when a course is being created, but when the first student or initial
           theory lesson is added.
           Therefore the values are set to temporary dummy data to prevents that NULL is inserted, which spring jpa repository
           cannot handle when making an insert statement.
           After the course has been saved, the course is then updated to empty values. */
        courseModel.setStudentUsernames("temporal value");
        courseModel.setNumberStudents(0);

        Date temporaryDate = new Date();
        courseModel.setCourseStartDate(temporaryDate);

        courseModel.setWeekdays("temporal value");

        courseService.addCourse(courseModel);

        courseModel.setStudentUsernames("");
        courseModel.setCourseStartDate(null);
        courseModel.setWeekdays("");

        /* updateCourse requires an ID of the course that should be updated, which is found by finding the last
        *  course created. */
        Course latestCourse = courseService.getLastCourseOrderedByID();
        courseService.updateCourse(latestCourse.getCourseTableID(), courseModel);

        return new ModelAndView("redirect:/course/");
    }


    @PostMapping(value = "/course/addSeveralTheoryLessons/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ModelAndView addSeveralTheoryLessons(@ModelAttribute CourseModel courseModel, @PathVariable long id,
                                                @RequestParam("numberLessons") int numberLessons,
                                                @RequestParam("startingPoint") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") Date startingPoint,
                                                @RequestParam("weekdaysIntArray") ArrayList<Integer> weekdaysIntArray,
                                                @RequestParam("numberLessonsADay") int numberLessonsADay) {
        ArrayList<Calendar> lessonDates = createLessonDates(startingPoint, weekdaysIntArray, numberLessons, numberLessonsADay);
        /* Converts the weekdaysIntArray into an string */
        String weekdays = "";
        for (int weekday : weekdaysIntArray) {
            weekdays += Integer.toString(weekday) + ",";
        }
        Date firstCreatedLessonDate = null;

        Course course = courseService.getCourse(id);

        /* For every lesson date, a lesson will be created */
        for (int j = 0; j < lessonDates.size(); j++) {
            Calendar lessonDate = lessonDates.get(j);
            LessonModel lesson = new LessonModel();
            lesson.setLessonState(LessonState.PENDING);
            lesson.setLessonDate(lessonDate.getTime());          
            lesson.setLessonLocation(course.getCourseLocation());
            lesson.setLessonInstructor(course.getInstructorUsername());
            lesson.setStudentList(course.getStudentUsernames());
            lesson.setCourseId(course.getCourseTableID());
            lesson.setLessonType(LessonType.THEORY_LESSON);

            lessonService.addLesson(lesson);

            /* Notes the date of the first created lesson */
            if (j == 0) {
                firstCreatedLessonDate = lessonDate.getTime();
            }
        }

        CourseModel updatedCourse = course.translateCourseToModel();
        /* Updates the course with the start date of the first lesson created and weekdays with the string converted from the weekdaysArray. */
        updatedCourse.setWeekdays(convertWeekdaysIntStringToWeekdaysInWords(weekdays));
        updatedCourse.setCourseStartDate(firstCreatedLessonDate);
        courseService.updateCourse(course.getCourseTableID(), updatedCourse);

        return new ModelAndView("redirect:/course/" + id);
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
        sharedMethods.setInstructorFullName(course, accountService);
        List<Lesson> lessons = lessonService.getAllLessons();

        ArrayList<Lesson> lessonsMatchingCourse = new ArrayList<>();

        for (Lesson lesson : lessons) {
            if (lesson.getCourseId() == id) {
                lessonsMatchingCourse.add(lesson);
            }
        }
        sharedMethods.setInstructorFullName(lessons, accountService);

        /* Finds all student accounts and adds those that belongs to the course in a separate arrayList */
        List<Account> studentAccounts = sharedMethods.findAccountsOfType("STUDENT", accountService, authGroupRepository);
        List<Account> studentsBelongingToCourse = findStudentsBelongingToCourse(course);

        /* Adds instructor and admins for drop down list to change instructor. */
        List<Account> adminAccounts = sharedMethods.findAccountsOfType("ADMIN", accountService, authGroupRepository);
        List<Account> instructorAccounts = sharedMethods.findAccountsOfType("INSTRUCTOR", accountService, authGroupRepository);

        /* Depending on course type a number of theory lessons to create is recommended. An more readable
         * string of course type is also saved. */
        String readableCourseType = "";
        int suggestedNumberOfLessons = 0;

        if(course.getCourseType() == CourseType.TYPE_B_CAR){
            suggestedNumberOfLessons = 29;
            readableCourseType = "Car (B)";
        }
        if(course.getCourseType() == CourseType.TYPE_BE_CAR_TRAILER){
            suggestedNumberOfLessons = 4;
            readableCourseType = "Car with trailer (BE)";
        }
        if(course.getCourseType() == CourseType.TYPE_A_BIKE){
            suggestedNumberOfLessons = 26;
            readableCourseType = "Motorbike (A)";
        }


        model.addAttribute("course", course);
        model.addAttribute("adminAccounts", adminAccounts);
        model.addAttribute("instructorAccounts", instructorAccounts);
        model.addAttribute("studentAccounts", studentAccounts);
        model.addAttribute("lessonsMatchingCourse", lessonsMatchingCourse);
        model.addAttribute("studentAccountsBelongingToCourse", studentsBelongingToCourse);
        model.addAttribute("suggestNumberOfLessons", suggestedNumberOfLessons);
        model.addAttribute("readableCourseType", readableCourseType);

        return "course-view";
    }

    @RequestMapping(value = "/course/addStudent/{id}", method = RequestMethod.POST)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ModelAndView addStudent(@PathVariable long id, @ModelAttribute CourseModel courseModel,
                                   @RequestParam("studentToAdd") String studentToAdd) {
        Course courseBeforeUpdate = courseService.getCourse(id);
        /* Sets the courseModel with the values of the current course before it has been changed. */
        CourseModel updatedCourse = courseBeforeUpdate.translateCourseToModel();

        /* Checks if the student already is in the course, and adds the student if that is not the case. */
        boolean studentAlreadyExistsInCourse = false;
        String prevStudents = updatedCourse.getStudentUsernames();

        SharedMethods sharedMethods = new SharedMethods();
        studentAlreadyExistsInCourse = sharedMethods.isUsernameInString(studentToAdd, updatedCourse.getStudentUsernames());

        if(!studentAlreadyExistsInCourse){
            prevStudents += studentToAdd + ",";
            /* Increments the number of students by one. */
            updatedCourse.setNumberStudents(updatedCourse.getNumberStudents() + 1);
            /* Updates every lesson with the student in it. */
            sharedMethods.updateUsernamesAssociatedWithCourse(id, prevStudents, lessonService, false);
        }

        updatedCourse.setStudentUsernames(prevStudents);

        courseService.updateCourse(id, updatedCourse);



        return new ModelAndView("redirect:/course/" + id);
    }

    @RequestMapping(value = "/course/removeStudent/{id}", method = RequestMethod.POST)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ModelAndView removeStudent(@PathVariable long id, @ModelAttribute CourseModel courseModel,
                                      @RequestParam("studentToRemove") String studentToRemove) {
        Course courseBeforeUpdate = courseService.getCourse(id);
        /* Sets the courseModel with the values of the current course before it has been changed. */
        CourseModel updatedCourse = courseBeforeUpdate.translateCourseToModel();
        /* Removes the targeted student. */
        String studentUsernames = courseBeforeUpdate.getStudentUsernames();
        ArrayList<String> studentUsernamesArray = sharedMethods.saveStringsSeparatedByCommaAsArray(studentUsernames);
        studentUsernamesArray.remove(studentToRemove);

        String studentUsernamesWithoutRemovedStudent = sharedMethods.saveStringListAsSingleString(studentUsernamesArray);
        updatedCourse.setStudentUsernames(studentUsernamesWithoutRemovedStudent);
        /* Decrements studentNumber by one */
        updatedCourse.setNumberStudents(updatedCourse.getNumberStudents() - 1);

        courseService.updateCourse(id, updatedCourse);

        sharedMethods.updateUsernamesAssociatedWithCourse(id, studentUsernamesWithoutRemovedStudent, lessonService, false);
        return new ModelAndView("redirect:/course/" + id);
    }

    @RequestMapping(value = "/course/changeInstructor/{id}", method = RequestMethod.POST)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ModelAndView changeInstructor(@PathVariable long id, @ModelAttribute CourseModel courseModel,
                                      @RequestParam("instructorToChangeTo") String instructorToChangeTo) {
        Course courseBeforeUpdate = courseService.getCourse(id);
        /* Sets the courseModel with the values of the current course before it has been changed. */
        CourseModel updatedCourse = courseBeforeUpdate.translateCourseToModel();
        /* Changes the instructor in the course */
        updatedCourse.setInstructorUsername(instructorToChangeTo);
        courseService.updateCourse(id, updatedCourse);

        sharedMethods.updateUsernamesAssociatedWithCourse(id, instructorToChangeTo, lessonService, true);
        return new ModelAndView("redirect:/course/" + id);
    }

    /* Function that converts an string of ints from 0-6 to weekdays in words with regards to the Date java class.
    Also adds a space and "," between each word. */
    private String convertWeekdaysIntStringToWeekdaysInWords(String weekdaysAsIntegers) {
        ArrayList<String> weekdaysAsIntegersArray = sharedMethods.saveStringsSeparatedByCommaAsArray(weekdaysAsIntegers);
        String weekDaysInWords = "";
        for (int i = 0; i < weekdaysAsIntegersArray.size(); i++) {
            switch (weekdaysAsIntegersArray.get(i)) {
                case "0":
                    weekDaysInWords += "Sunday";
                    break;
                case "1":
                    weekDaysInWords += "Monday";
                    break;
                case "2":
                    weekDaysInWords += "Tuesday";
                    break;
                case "3":
                    weekDaysInWords += "Wednesday";
                    break;
                case "4":
                    weekDaysInWords += "Thursday";
                    break;
                case "5":
                    weekDaysInWords += "Friday";
                    break;
                case "6":
                    weekDaysInWords += "Saturday";
                    break;
            }
            /* Refrains from adding an "," at the end for appearances sake */
            if (i != weekdaysAsIntegersArray.size() - 1) {
                weekDaysInWords += ", ";
            }
        }
        return weekDaysInWords;
    }

    /* Finds student accounts. Saves the student usernames in the course as an string array. Checks if any of the
       student usernames equals any of the student accounts, and adds them to an the array studentsAccountsBelongToCourse. */
    private List<Account> findStudentsBelongingToCourse(Course course) {
        List<Account> studentAccounts = sharedMethods.findAccountsOfType("STUDENT", accountService, authGroupRepository);
        List<String> studentUsernamesInCourseAsStringArray = sharedMethods.saveStringsSeparatedByCommaAsArray(course.getStudentUsernames());
        List<Account> studentAccountsBelongingToCourse = new ArrayList<>();
        for (Account studentAccount : studentAccounts) {
            for (String studentUsernameInCourse : studentUsernamesInCourseAsStringArray) {
                if (studentUsernameInCourse.equals((studentAccount.getUsername()))) {
                    studentAccountsBelongingToCourse.add(studentAccount);
                }
            }
        }
        return studentAccountsBelongingToCourse;
    }

    private ArrayList<Calendar> createLessonDates(Date startingPoint, ArrayList<Integer> weekdaysArrays, int numberLessonsToDistribute,
                                              int numberLessonsADay) {
        Calendar currentDayDate = Calendar.getInstance();
        currentDayDate.setTime(startingPoint);
        ArrayList<Calendar> lessonDates = new ArrayList<>();


        /* A lesson should minimum be 45 minutes according to law, and the two interviewed driving schools had a 45
           minute lesson duration. */
        int lessonDurationMinutes = 45;

        /* While all lessons has not yet been distributed */
        while (numberLessonsToDistribute > 0) {
            currentDayDate.add(Calendar.DATE, 1);   // number of days to add
            if (weekdaysArrays.contains(currentDayDate.get(Calendar.DAY_OF_WEEK))) {
                /* If it is one of the weekdays specified in the weekdays array, add an number of lessons equal to
                 * number lessons a day. Also since it adds a several lessons in a loop it needs to check before each lesson
                 * is added if the necessary lessons have been distributed. */
                for (int g = 0; g < numberLessonsADay; g++) {
                    if (numberLessonsToDistribute > 0) {
                        Calendar lessonDate = Calendar.getInstance();
                        lessonDate.setTime(currentDayDate.getTime());
                        /* Adds lesson duration in minutes to the date. */
                        lessonDate.add(Calendar.MINUTE, lessonDurationMinutes * g);
                        lessonDates.add(lessonDate);
                        numberLessonsToDistribute--;
                    }
                }
            }
        }
        return lessonDates;
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
