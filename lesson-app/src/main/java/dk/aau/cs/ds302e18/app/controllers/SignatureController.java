package dk.aau.cs.ds302e18.app.controllers;

import dk.aau.cs.ds302e18.app.domain.Account;
import dk.aau.cs.ds302e18.app.auth.AuthGroup;
import dk.aau.cs.ds302e18.app.auth.AuthGroupRepository;
import dk.aau.cs.ds302e18.app.domain.*;
import dk.aau.cs.ds302e18.app.service.AccountService;
import dk.aau.cs.ds302e18.app.service.CourseService;
import dk.aau.cs.ds302e18.app.service.LessonService;
import dk.aau.cs.ds302e18.app.service.LogbookService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.codec.digest.DigestUtils.md5Hex;

@Controller
public class SignatureController {
    private final LessonService lessonService;
    private final AccountService accountService;
    private final AuthGroupRepository authGroupRepository;
    private final LogbookService logbookService;


    public SignatureController(LessonService lessonService, AccountService accountService,
                            AuthGroupRepository authGroupRepository, CourseService courseService,
                               LogbookService logbookService) {
        super();
        this.lessonService = lessonService;
        this.accountService = accountService;
        this.authGroupRepository = authGroupRepository;
        this.logbookService = logbookService;
    }

    @GetMapping(value = "/logbook/export/{id}")
    public String getExportLogbook(Model model, @PathVariable long id) {
        SignatureCanvas signatureCanvas = new SignatureCanvas();

        List<Lesson> lessons = lessonService.getAllLessons();
        Logbook logbook = logbookService.getLogbook(id);

        List<LogbookExportModel> studentList = new ArrayList<>();
        List<LogbookExportModel> instructorList = new ArrayList<>();

        String username = logbook.getStudent();

        /*
         * Prepares student signatures object for the html page.
         */
        for (Lesson lesson : lessons) {
            if (lesson.getCourseId() == logbook.getCourseID() && lesson.getStudentList().contains(username)) {
                Account tempAccount = accountService.getAccount(logbook.getStudent());
                List<AuthGroup> tempAuthGroup = authGroupRepository.findByUsername(logbook.getStudent());
                LogbookExportModel logbookExportModel = new LogbookExportModel();
                logbookExportModel.setUsername(logbook.getStudent());
                logbookExportModel.setFirstName(tempAccount.getFirstName());
                logbookExportModel.setLastName(tempAccount.getLastName());
                logbookExportModel.setPosition(tempAuthGroup.get(0).getAuthGroup());
                logbookExportModel.setLessonType(String.valueOf(lesson.getLessonType()));
                logbookExportModel.setLessonId(String.valueOf(lesson.getId()));
                /* All lessons are default set to an image that was uploaded Sun Dec 02 23:30:02 CET 2018 when not yet signed. If that is the image for the
                 * lesson show that image. Else show the image belonging to the lesson. */
                if (signatureCanvas.getSignatureDate("p3-project", username + lesson.getId()).equals("Sun Dec 02 23:30:02 CET 2018")) {
                    logbookExportModel.setSignatureUrl("https://s3.eu-west-2.amazonaws.com/p3-project/notsigned.png");
                    logbookExportModel.setSignatureDate("Not signed");
                } else {
                    logbookExportModel.setSignatureUrl("https://s3.eu-west-2.amazonaws.com/p3-project/" + username + lesson.getId() + ".png");
                    logbookExportModel.setSignatureDate(signatureCanvas.getSignatureDate("p3-project", username + lesson.getId()));
                }
                studentList.add(logbookExportModel);

                /*
                 * Prepares instructor signatures object for the html page.
                 */
                Account tempIns = accountService.getAccount(lesson.getLessonInstructor());
                List<AuthGroup> tempInsAuth = authGroupRepository.findByUsername(lesson.getLessonInstructor());
                LogbookExportModel logbookExportModelIns = new LogbookExportModel();
                logbookExportModelIns.setUsername(lesson.getLessonInstructor());
                logbookExportModelIns.setFirstName(tempIns.getFirstName());
                logbookExportModelIns.setLastName(tempIns.getLastName());
                logbookExportModelIns.setPosition(tempInsAuth.get(0).getAuthGroup());
                logbookExportModelIns.setLessonType(String.valueOf(lesson.getLessonType()));
                logbookExportModelIns.setLessonId(String.valueOf(lesson.getId()));
                if (signatureCanvas.getSignatureDate("p3-project", lesson.getLessonInstructor() + lesson.getId()).equals("Sun Dec 02 23:30:02 CET 2018")) {
                    logbookExportModelIns.setSignatureUrl("https://s3.eu-west-2.amazonaws.com/p3-project/notsigned.png");
                    logbookExportModelIns.setSignatureDate("Not signed");
                } else {
                    logbookExportModelIns.setSignatureUrl("https://s3.eu-west-2.amazonaws.com/p3-project/" + lesson.getLessonInstructor() + lesson.getId() + ".png");
                    logbookExportModelIns.setSignatureDate(signatureCanvas.getSignatureDate("p3-project", lesson.getLessonInstructor() + lesson.getId()));
                }
                instructorList.add(logbookExportModelIns);
            }
        }
        model.addAttribute("SignatureList", studentList);
        model.addAttribute("SignatureListIns", instructorList);

        return "export-logbook";
    }

    @GetMapping(value = "/signature/{id}")
    public String getSignaturePage(Model model, @PathVariable long id, LessonModel lessonModel) {
        SignatureCanvas signatureCanvas = new SignatureCanvas();

        List<Lesson> lessons = lessonService.getAllLessons();
        List<SignatureModel> signatureList = new ArrayList<>();

        int signed = 0;
        int unsigned = 0;

        for (Lesson lesson : lessons) {
            if (lesson.getId() == id) {
                /*
                 * Prepares student signatures object for the html page.
                 */
                String[] studentListArray = lesson.getStudentList().split(",");
                for (String username: studentListArray)
                {
                    Account tempAccount = accountService.getAccount(username);
                    List<AuthGroup> tempAuthGroup = authGroupRepository.findByUsername(username);
                    SignatureModel signatureModel = new SignatureModel();
                    signatureModel.setUsername(username);
                    signatureModel.setFirstName(tempAccount.getFirstName());
                    signatureModel.setLastName(tempAccount.getLastName());
                    signatureModel.setPosition(tempAuthGroup.get(0).getAuthGroup());
                    /* All lessons are default set to an image that was uploaded Sun Dec 02 23:30:02 CET 2018 when not yet signed. If that is the image for the
                     * lesson show that image. Else show the image belonging to the lesson. */
                    if (signatureCanvas.getSignatureDate("p3-project", username + id).equals("Sun Dec 02 23:30:02 CET 2018")) {
                        signatureModel.setSignatureUrl("https://s3.eu-west-2.amazonaws.com/p3-project/notsigned.png");
                        signatureModel.setSignatureDate("Not signed");
                        unsigned++;
                    } else {
                        signatureModel.setSignatureUrl("https://s3.eu-west-2.amazonaws.com/p3-project/" + username + id + ".png");
                        signatureModel.setSignatureDate(signatureCanvas.getSignatureDate("p3-project", username + id));
                        signed++;
                    }
                    signatureList.add(signatureModel);
                }

                /*
                 * Prepares instructor signatures object for the html page.
                 */
                Account tempAccount = accountService.getAccount(lesson.getLessonInstructor());
                SignatureModel signatureModel = new SignatureModel();
                signatureModel.setUsername(lesson.getLessonInstructor());
                signatureModel.setFirstName(tempAccount.getFirstName());
                signatureModel.setLastName(tempAccount.getLastName());
                signatureModel.setPosition("Instructor");
                /* All lessons are default set to an image that was uploaded Sun Dec 02 23:30:02 CET 2018 when not yet signed. If that is the image for the
                 * lesson show that image. Else show the image belonging to the lesson. */
                if (signatureCanvas.getSignatureDate("p3-project", lesson.getLessonInstructor() + id).equals("Sun Dec 02 23:30:02 CET 2018")) {
                    signatureModel.setSignatureUrl("https://s3.eu-west-2.amazonaws.com/p3-project/notsigned.png");
                    signatureModel.setSignatureDate("Not signed");
                    unsigned++;
                } else {
                    signatureModel.setSignatureUrl("https://s3.eu-west-2.amazonaws.com/p3-project/" + lesson.getLessonInstructor() + id + ".png");
                    signatureModel.setSignatureDate(signatureCanvas.getSignatureDate("p3-project", lesson.getLessonInstructor() + id));
                    signed++;
                }
                signatureList.add(signatureModel);
            }
        }

        String signedNotSigned = ("( " + String.valueOf(signed) + " ) have signed this lesson - ( " + String.valueOf(unsigned) + " ) have not signed this lesson.");
        model.addAttribute("pathid", id);
        model.addAttribute("SignatureList", signatureList);
        model.addAttribute("SNS", signedNotSigned);


        if (unsigned == 0) {
            Lesson lesson = this.lessonService.getLesson(id);
            lessonModel = lesson.translateLessonToModel();
            lessonModel.setLessonState(LessonState.COMPLETED_SIGNED);
            this.lessonService.updateLesson(id, lessonModel);
        }

        return "signature";
    }

    @GetMapping(value = "/canvas/{id}")
    public String getCanvasPage(@PathVariable long id) {
        return "canvas";
    }

    @PostMapping(value = "/canvas/{id}")
    public String postCanvasPage(@RequestBody CanvasModel canvasModel, @PathVariable long id) {
        SignatureCanvas signatureCanvas = new SignatureCanvas();
        String signatureId = getAccountUsername() + id;

        signatureCanvas.upload("p3-project", signatureId, canvasModel.getDataUrl());
        return "canvas";
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
