package dk.aau.cs.ds302e18.app.controllers;

import dk.aau.cs.ds302e18.app.auth.AccountRespository;
import dk.aau.cs.ds302e18.app.domain.CalendarViewModel;
import dk.aau.cs.ds302e18.app.domain.Lesson;
import dk.aau.cs.ds302e18.app.domain.LessonType;
import dk.aau.cs.ds302e18.app.service.LessonService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.codec.digest.DigestUtils.md5Hex;

@Controller
public class CalendarController {
    private final LessonService lessonService;
    private final AccountRespository accountRespository;

    public CalendarController(LessonService lessonService, AccountRespository accountRespository) {
        super();
        this.lessonService = lessonService;
        this.accountRespository = accountRespository;
    }

    @GetMapping(value = "/calendar")
    public String getCalendar() {
        return "calendar";
    }

    @RequestMapping("/calendar/getData")
    public @ResponseBody
    ArrayList<CalendarViewModel> getCalendarData() {

        // Get the Spring Security user details of the currently logged in user
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        List<Lesson> lessons = this.lessonService.getAllLessons();
        ArrayList<CalendarViewModel> lessonArrayModels = new ArrayList<>();
        for (Lesson lesson : lessons) {
            // Currently a glitch with usernames contained in other usernames, but will be fixed at later time
            if (lesson.getStudentList().contains(userDetails.getUsername())) {
                String lessonType = "";
                String lessonColor = "";
                if (lesson.getLessonType() == LessonType.THEORY_LESSON) {
                    lessonType = "Theory lesson";
                    lessonColor = "CYAN";
                }
                if (lesson.getLessonType() == LessonType.PRACTICAL_LESSON) {
                    lessonType = "Practical lesson";
                    lessonColor = "GREEN";
                }
                CalendarViewModel calendarViewModel = new CalendarViewModel(lesson.getCourseId(), lessonColor, lessonType, lesson.getLessonDate(), "Location : " + lesson.getLessonLocation());

                lessonArrayModels.add(calendarViewModel);
            }
        }
        return lessonArrayModels;
    }

    /**
     * @param response The response to be returned to the user as a file
     * @throws IOException If an error with file or inputStream occurs
     */
    @RequestMapping(value = "/calendar/exportCalendar", method = RequestMethod.GET)
    @ResponseBody
    @PreAuthorize("hasAnyRole('ROLE_INSTRUCTOR', 'ROLE_ADMIN')")
    public void exportCalendar(HttpServletResponse response) throws IOException {
        // Create the csv file
        File file = new File("lesson-calendar-export.csv");

        // Create FileWriter object, it will not append to the file
        FileWriter fileWriter = new FileWriter(file, false);

        // Create CSV headers which Google Calendar accepts
        fileWriter.write("Subject, Start date, Start time\n");

        List<Lesson> lessonList = this.lessonService.getAllLessons();

        String username = getAccountUsername();

        for (Lesson lesson : lessonList) {
            if (lesson.getLessonInstructor().equals(username)) {
                String lessonDate = (lesson.getLessonDate().getYear() + 1900) + "-" + (lesson.getLessonDate().getMonth() + 1) + "-" + lesson.getLessonDate().getDate();
                String lessonTime = lesson.getLessonDate().getHours() + ":" + lesson.getLessonDate().getMinutes() + ":" + lesson.getLessonDate().getSeconds();
                fileWriter.write("Lesson, " + lessonDate + ", " + lessonTime + "\n");
            }
        }

        fileWriter.close();

        response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");
        response.setContentLength((int) file.length());

        InputStream inputStream = new BufferedInputStream(new FileInputStream(file));
        FileCopyUtils.copy(inputStream, response.getOutputStream());
    }

    @ModelAttribute("gravatar")
    public String gravatar() {
        //Models Gravatar
        return "http://0.gravatar.com/avatar/" + md5Hex(accountRespository.findByUsername(getAccountUsername()).getEmail());
    }

    private String getAccountUsername() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ((UserDetails) principal).getUsername();
    }
}
