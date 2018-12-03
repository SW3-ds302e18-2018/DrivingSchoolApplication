package dk.aau.cs.ds302e18.app.controllers;

import dk.aau.cs.ds302e18.app.SortLessonsByDateTime;
import dk.aau.cs.ds302e18.app.auth.AccountRespository;
import dk.aau.cs.ds302e18.app.domain.Lesson;
import dk.aau.cs.ds302e18.app.service.LessonService;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.apache.commons.codec.digest.DigestUtils.md5Hex;

@Controller
@RequestMapping
public class IndexController {
    private final LessonService lessonService;
    private final AccountRespository accountRespository;

    public IndexController(LessonService lessonService, AccountRespository accountRespository) {
        super();
        this.lessonService = lessonService;
        this.accountRespository = accountRespository;
    }

    /**
     * Get for the page.
     *
     * @param model
     * @return
     */
    @GetMapping(value = {"/", "/index"})
    @PreAuthorize("isAuthenticated()")
    public String getIndexLesson(Model model) {

        //Retrieves all lessons from the data base and sorts them by date
        List<Lesson> lessonList = this.lessonService.getAllLessons();
        lessonList.sort(new SortLessonsByDateTime());

        //Creating two new list, to store the specific user's lessons, one for today's lessons and one for upcoming lessons
        List<Lesson> todaysLessonList = new ArrayList<>();
        List<Lesson> upcomingLessonList = new ArrayList<>();

        //Creates two new dates for comparison. One for today's lessons and one for upcoming lessons
        //The nextDate date is used to find lessons after the current date. For this one full day is added to nextDate
        Date currDate = new Date();
        Date nextDate = new Date();
        //86400000 is one day in milliseconds
        nextDate.setTime(nextDate.getTime() + 86400000);

        //Fetches the username from the session
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = ((UserDetails) principal).getUsername();

        /**
         * If there is more then 9 lessons in the database, it limits the listed lesson to 9,
         * and only iterates through
         * */
        if (lessonList.size() >= 9) {
            //Iterates through all lessons, adding the ones with today's date to todaysLessonList
            for (int i = 0; i <= 8; i++) {
                if (lessonList.get(i).getLessonDate() != null &&
                        lessonList.get(i).getLessonDate().getYear() == currDate.getYear() &&
                        lessonList.get(i).getLessonDate().getMonth() == currDate.getMonth() &&
                        lessonList.get(i).getLessonDate().getDate() == currDate.getDate()) {

                    String[] studentListArray = lessonList.get(i).getStudentList().split(",");
                    for (String studentUsername : studentListArray) {
                        if (studentUsername.equals(username)) {
                            todaysLessonList.add(lessonList.get(i));
                        }
                    }
                }
            }
            //Iterates through all lessons, adding the upcoming lessons to upcomingLessonList
            for (int i = 0; i <= 8; i++) {
                if (lessonList.get(i).getLessonDate() != null &&
                        lessonList.get(i).getLessonDate().getYear() >= nextDate.getYear() &&
                        lessonList.get(i).getLessonDate().getMonth() >= nextDate.getMonth() &&
                        lessonList.get(i).getLessonDate().getDate() >= nextDate.getDate()) {

                    String[] studentListArray = lessonList.get(i).getStudentList().split(",");
                    for (String studentUsername : studentListArray) {
                        if (studentUsername.equals(username)) {
                            upcomingLessonList.add(lessonList.get(i));
                        }
                    }
                }
            }
        }

        /**
         * If there is less than 8 lessons in the database, no limiter is needed.
         * */
        if (lessonList.size() <= 8) {
            for (Lesson lesson : lessonList) {
                if (lesson.getLessonDate() != null &&
                        lesson.getLessonDate().getYear() == currDate.getYear() &&
                        lesson.getLessonDate().getMonth() == currDate.getMonth() &&
                        lesson.getLessonDate().getDate() == currDate.getDate()) {

                    String[] studentListArray = lesson.getStudentList().split(",");
                    for (String studentUsername : studentListArray) {
                        if (studentUsername.equals(username)) {
                            todaysLessonList.add(lesson);
                        }
                    }
                }
                if (lesson.getLessonDate() != null &&
                        lesson.getLessonDate().getYear() >= nextDate.getYear() &&
                        lesson.getLessonDate().getMonth() >= nextDate.getMonth() &&
                        lesson.getLessonDate().getDate() >= nextDate.getDate()) {

                    String[] studentListArray = lesson.getStudentList().split(",");
                    for (String studentUsername : studentListArray) {
                        if (studentUsername.equals(username)) {
                            upcomingLessonList.add(lesson);
                        }
                    }
                }
            }
        }

        //Models the lists as an attribute to the website
        model.addAttribute("todayLessonList", todaysLessonList);
        model.addAttribute("upcomingLessonList", upcomingLessonList);
        return "index";
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