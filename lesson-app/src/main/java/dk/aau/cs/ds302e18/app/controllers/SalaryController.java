package dk.aau.cs.ds302e18.app.controllers;

import dk.aau.cs.ds302e18.app.SortLessonsByCourseId;
import dk.aau.cs.ds302e18.app.auth.Account;
import dk.aau.cs.ds302e18.app.auth.AccountRespository;
import dk.aau.cs.ds302e18.app.auth.AuthGroup;
import dk.aau.cs.ds302e18.app.auth.AuthGroupRepository;
import dk.aau.cs.ds302e18.app.domain.Course;
import dk.aau.cs.ds302e18.app.domain.CourseType;
import dk.aau.cs.ds302e18.app.domain.Lesson;
import dk.aau.cs.ds302e18.app.domain.LessonState;
import dk.aau.cs.ds302e18.app.service.CourseService;
import dk.aau.cs.ds302e18.app.service.LessonService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.apache.commons.codec.digest.DigestUtils.md5Hex;

@Controller
@RequestMapping
public class SalaryController {
    private final LessonService lessonService;
    private final CourseService courseService;
    private final AccountRespository accountRespository;
    private final AuthGroupRepository authGroupRepository;

    public SalaryController(LessonService lessonService, CourseService courseService, AccountRespository accountRespository, AuthGroupRepository authGroupRepository) {
        super();
        this.lessonService = lessonService;
        this.courseService = courseService;
        this.accountRespository = accountRespository;
        this.authGroupRepository = authGroupRepository;
    }

    @GetMapping(value = {"/salary"})
    @PreAuthorize("hasRole('ROLE_INSTRUCTOR')")
    public String getInstructorSalary(Model model) {
        List<Lesson> lessonList = this.lessonService.getAllLessons();
        //Creates a list, to store the admin's completed lessons for the salary
        List<Lesson> salaryLessonList;
        //Creates three ints to store the number of lessons of a specific courseType
        //and an int to store the total number of unique course ids from the found lessons
        int courseTypeBLessonTotal, courseTypeBELessonTotal, courseTypeALessonTotal, uniqueCourseIdTotal;

        //Creates a date and sets it's date, hour, minute and seconds to a specific time of the month of the year
        //This is to get a default date with the current year and month
        Date thisMonthDate = new Date();
        thisMonthDate.setDate(20);
        thisMonthDate.setHours(23);
        thisMonthDate.setMinutes(59);
        thisMonthDate.setSeconds(0);

        //Creates a date and sets it's to the same as thisMonthDate and moves it's month, one month forward
        //This is to get a default date with the current year and month
        Date nextMonthDate = new Date(thisMonthDate.getTime());
        nextMonthDate.setMonth(thisMonthDate.getMonth() + 1);

        //Gets username from private function
        String username = getAccountUsername();

        //Gets salary lesson list from private function
        salaryLessonList = getSalaryLessonList(lessonList, thisMonthDate, nextMonthDate, username);

        //Gets the course type totals from a private function, the function returns a ordered list
        List<Integer> courseTypeTotal = getCourseTypeTotal(salaryLessonList);
        courseTypeBLessonTotal = courseTypeTotal.get(0);
        courseTypeBELessonTotal = courseTypeTotal.get(1);
        courseTypeALessonTotal = courseTypeTotal.get(2);

        //Finds unique course ids from the salary lesson list
        uniqueCourseIdTotal = findUniqueCourseIds(salaryLessonList);

        //Models variables from java for usage in html
        model.addAttribute("uniqueCourseIdTotal", uniqueCourseIdTotal);
        model.addAttribute("courseTypeBLessonTotal", courseTypeBLessonTotal);
        model.addAttribute("courseTypeBELessonTotal", courseTypeBELessonTotal);
        model.addAttribute("courseTypeALessonTotal", courseTypeALessonTotal);
        model.addAttribute("salaryLessonList", salaryLessonList);
        model.addAttribute("salaryLessonTotal", salaryLessonList.size());
        return "salary-instructor";
    }

    @RequestMapping(value = "/salary", method = RequestMethod.POST)
    @PreAuthorize("hasRole('ROLE_INSTRUCTOR')")
    public String updateInstructorSalary(Model model,
                                         @RequestParam("instructorSalaryFromDate") String instructorSalaryFromDate,
                                         @RequestParam("instructorSalaryToDate") String instructorSalaryToDate) {
        List<Lesson> lessonList = this.lessonService.getAllLessons();
        //Creates a list, to store the admin's completed lessons for the salary
        List<Lesson> salaryLessonList;
        //Creates three ints to store the number of lessons of a specific courseType
        //and an int to store the total number of unique course ids from the found lessons
        int courseTypeBLessonTotal, courseTypeBELessonTotal, courseTypeALessonTotal, uniqueCourseIdTotal;

        //Gets username from private function
        String username = getAccountUsername();

        //Creates two dates to store the input dates from the html
        Date fromDateInput = null;
        Date toDateInput = null;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            fromDateInput = formatter.parse(instructorSalaryFromDate);
            toDateInput = formatter.parse(instructorSalaryToDate);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Gets salary lesson list from private function
        salaryLessonList = getSalaryLessonList(lessonList, fromDateInput, toDateInput, username);

        //Gets the course type totals from a private function, the function returns a ordered list
        List<Integer> courseTypeTotal = getCourseTypeTotal(salaryLessonList);
        courseTypeBLessonTotal = courseTypeTotal.get(0);
        courseTypeBELessonTotal = courseTypeTotal.get(1);
        courseTypeALessonTotal = courseTypeTotal.get(2);

        //Finds unique course ids from the salary lesson list
        uniqueCourseIdTotal = findUniqueCourseIds(salaryLessonList);

        //Models variables from java for usage in html
        model.addAttribute("uniqueCourseIdTotal", uniqueCourseIdTotal);
        model.addAttribute("courseTypeBLessonTotal", courseTypeBLessonTotal);
        model.addAttribute("courseTypeBELessonTotal", courseTypeBELessonTotal);
        model.addAttribute("courseTypeALessonTotal", courseTypeALessonTotal);
        model.addAttribute("salaryLessonList", salaryLessonList);
        model.addAttribute("salaryLessonTotal", salaryLessonList.size());
        return "salary-instructor";
    }


    @GetMapping(value = {"/salary/admin"})
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String getAdminSalary(Model model) {
        List<Lesson> lessonList = this.lessonService.getAllLessons();
        //Creates a list, to store the admin's completed lessons for the salary
        List<Lesson> salaryLessonList;
        //Creates three ints to store the number of lessons of a specific courseType
        //and an int to store the total number of unique course ids from the found lessons
        int courseTypeBLessonTotal, courseTypeBELessonTotal, courseTypeALessonTotal, uniqueCourseIdTotal;

        //Creates a date and sets it's date, hour, minute and seconds to a specific time of the month of the year
        //This is to get a default date with the current year and month
        Date thisMonthDate = new Date();
        thisMonthDate.setDate(20);
        thisMonthDate.setHours(23);
        thisMonthDate.setMinutes(59);
        thisMonthDate.setSeconds(0);

        //Creates a date and sets it's to the same as thisMonthDate and moves it's month, one month forward
        //This is to get a default date with the current year and month
        Date nextMonthDate = new Date(thisMonthDate.getTime());
        nextMonthDate.setMonth(thisMonthDate.getMonth() + 1);

        //Finds all instructors and stores them in a list
        List<Account> instructorList = findAllInstructors();

        //Gets the username from the first instructor in the instructorList (as a default)
        String instructorUsername = instructorList.get(0).getUsername();

        //Gets salary lesson list from private function
        salaryLessonList = getSalaryLessonList(lessonList, thisMonthDate, nextMonthDate, instructorUsername);

        //Gets the course type totals from a private function, the function returns a ordered list
        List<Integer> courseTypeTotal = getCourseTypeTotal(salaryLessonList);
        courseTypeBLessonTotal = courseTypeTotal.get(0);
        courseTypeBELessonTotal = courseTypeTotal.get(1);
        courseTypeALessonTotal = courseTypeTotal.get(2);

        //Finds unique course ids from the salary lesson list
        uniqueCourseIdTotal = findUniqueCourseIds(salaryLessonList);

        //Models variables from java for usage in html
        model.addAttribute("instructorList", instructorList);
        model.addAttribute("uniqueCourseIdTotal", uniqueCourseIdTotal);
        model.addAttribute("courseTypeBLessonTotal", courseTypeBLessonTotal);
        model.addAttribute("courseTypeBELessonTotal", courseTypeBELessonTotal);
        model.addAttribute("courseTypeALessonTotal", courseTypeALessonTotal);
        model.addAttribute("salaryLessonList", salaryLessonList);
        model.addAttribute("salaryLessonTotal", salaryLessonList.size());
        return "salary-admin";
    }

    @RequestMapping(value = "/salary/admin", method = RequestMethod.POST)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String updateAdminSalary(Model model,
                                    @RequestParam("adminSalaryFromDate") String adminSalaryFromDate,
                                    @RequestParam("adminSalaryToDate") String adminSalaryToDate,
                                    @RequestParam("adminSalarySelect") String adminSalarySelect) {
        List<Lesson> lessonList = this.lessonService.getAllLessons();
        //Creates a list, to store the admin's completed lessons for the salary
        List<Lesson> salaryLessonList;
        //Creates three ints to store the number of lessons of a specific courseType
        //and an int to store the total number of unique course ids from the found lessons
        int courseTypeBLessonTotal, courseTypeBELessonTotal, courseTypeALessonTotal, uniqueCourseIdTotal;

        //Finds all instructors and stores them in a list
        List<Account> instructorList = findAllInstructors();

        //Creates two dates to store the input dates from the html
        Date fromDateInput = null;
        Date toDateInput = null;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            fromDateInput = formatter.parse(adminSalaryFromDate);
            toDateInput = formatter.parse(adminSalaryToDate);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //Gets salary lesson list from private function
        salaryLessonList = getSalaryLessonList(lessonList, fromDateInput, toDateInput, adminSalarySelect);

        //Gets the course type totals from a private function, the function returns a ordered list
        List<Integer> courseTypeTotal = getCourseTypeTotal(salaryLessonList);
        courseTypeBLessonTotal = courseTypeTotal.get(0);
        courseTypeBELessonTotal = courseTypeTotal.get(1);
        courseTypeALessonTotal = courseTypeTotal.get(2);

        //Finds unique course ids from the salary lesson list
        uniqueCourseIdTotal = findUniqueCourseIds(salaryLessonList);

        //Models variables from java for usage in html
        model.addAttribute("instructorList", instructorList);
        model.addAttribute("uniqueCourseIdTotal", uniqueCourseIdTotal);
        model.addAttribute("courseTypeBLessonTotal", courseTypeBLessonTotal);
        model.addAttribute("courseTypeBELessonTotal", courseTypeBELessonTotal);
        model.addAttribute("courseTypeALessonTotal", courseTypeALessonTotal);
        model.addAttribute("salaryLessonList", salaryLessonList);
        model.addAttribute("salaryLessonTotal", salaryLessonList.size());
        return "salary-admin";
    }

    private List<Lesson> getSalaryLessonList(List<Lesson> lessonList, Date fromDate, Date toDate, String username) {
        //Creates a list, to store the admin's completed lessons for the salary
        List<Lesson> salaryLessonList = new ArrayList<>();
        //Checks all lessons and finds any which fits within a specific time period,
        //if the instructor is the same as the instructor for the lesson and if the lesson is in a completed state
        for (Lesson lesson : lessonList) {
            if (lesson.getLessonDate().after(fromDate) && lesson.getLessonDate().before(toDate)) {
                if (lesson.getLessonInstructor().equals(username) && ((lesson.getLessonState().equals(LessonState.COMPLETED_UNSIGNED)) ||
                        (lesson.getLessonState().equals(LessonState.COMPLETED_SIGNED)) ||
                        (lesson.getLessonState().equals(LessonState.STUDENT_ABSENT)))) {
                    //Adds the lesson to salaryLessonList
                    salaryLessonList.add(lesson);
                }
            }
        }
        return salaryLessonList;
    }

    private List<Integer> getCourseTypeTotal(List<Lesson> lessonList) {
        List<Integer> courseTypeTotalList = new ArrayList<>();
        int courseTypeBLessonTotal = 0, courseTypeBELessonTotal = 0, courseTypeALessonTotal = 0;
        for (Lesson lesson : lessonList) {
            //Checks which type of course the lesson is in and counts up the total of that type
            Course course = courseService.getCourse(lesson.getCourseId());
            if (course.getCourseType().equals(CourseType.TYPE_B_CAR)) {
                courseTypeBLessonTotal++;
            }
            if (course.getCourseType().equals(CourseType.TYPE_BE_CAR_TRAILER)) {
                courseTypeBELessonTotal++;
            }
            if (course.getCourseType().equals(CourseType.TYPE_A_BIKE)) {
                courseTypeALessonTotal++;
            }
        }
        courseTypeTotalList.add(courseTypeBLessonTotal);
        courseTypeTotalList.add(courseTypeBELessonTotal);
        courseTypeTotalList.add(courseTypeALessonTotal);
        return courseTypeTotalList;
    }

    private int findUniqueCourseIds(List<Lesson> lessonList) {
        //Sorts the founds lessons by id and iterates through them to find unique course ids
        int uniqueCourseIdTotal = 0;
        long currCourseId;
        Long nextCourseId = null;

        lessonList.sort(new SortLessonsByCourseId());
        for (Lesson lesson : lessonList) {
            currCourseId = lesson.getCourseId();
            if (nextCourseId == null) {
                uniqueCourseIdTotal++;
                nextCourseId = currCourseId;
            }
            if (nextCourseId != currCourseId) {
                uniqueCourseIdTotal++;
                nextCourseId = currCourseId;
            }
        }
        return uniqueCourseIdTotal;
    }

    private List<Account> findAllInstructors() {
        //Finds all accounts and compares their auth group to find the ones who are instructors
        List<AuthGroup> authGroups = this.authGroupRepository.findAll();
        List<Account> accountList = this.accountRespository.findAll();
        List<Account> instructorList = new ArrayList<>();
        for (int i = 0; i < accountList.size(); i++) {
            if (authGroups.get(i).getAuthGroup().equals("INSTRUCTOR")) {
                instructorList.add(accountList.get(i));
            }
        }
        return instructorList;
    }

    @ModelAttribute("gravatar")
    public String gravatar() {
        //Models Gravatar
        String gravatar = ("http://0.gravatar.com/avatar/" + md5Hex(accountRespository.findByUsername(getAccountUsername()).getEmail()));
        return (gravatar);
    }

    private String getAccountUsername() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ((UserDetails) principal).getUsername();
    }
}
