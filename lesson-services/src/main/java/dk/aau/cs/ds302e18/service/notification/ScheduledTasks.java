package dk.aau.cs.ds302e18.service.notification;

import dk.aau.cs.ds302e18.service.models.Account;
import dk.aau.cs.ds302e18.service.models.AccountRespository;
import dk.aau.cs.ds302e18.service.models.Lesson;
import dk.aau.cs.ds302e18.service.models.LessonRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class ScheduledTasks
{
    private AccountRespository accountRespository;
    private LessonRepository lessonRepository;

    public ScheduledTasks(AccountRespository accountRespository, LessonRepository lessonRepository)
    {
        this.accountRespository = accountRespository;
        this.lessonRepository = lessonRepository;
    }

    // This scheduler runs once every minute, therefore 60000 miliseconds
    @Scheduled(fixedRate = 60000)
    public void scheduleSendLessonNotifications()
    {

        List<Account> accountList = this.accountRespository.findAll();
        List<Lesson> lessonList = this.lessonRepository.findAll();

        for (Lesson lesson : lessonList)
        {
            for (Account account : accountList)
            {
                if (isApartofLesson(lesson, account) && withinNotificationTimer(lesson, account))
                {
                    System.out.println("TRIGGERED");
                    String message = "Hello " + account.getUsername() + ",\n You have a lesson at " +
                            lesson.getLessonLocation() + " on the\n" + lesson.getLessonDate();
                    new Notification(message, "ds302e18@gmail.com", account.getEmail(),
                            "+45" + account.getPhoneNumber());
                    // +45 is the region code for Denmark.
                }

            }
        }

    }

    private boolean isApartofLesson(Lesson lesson, Account account)
        {
            return lesson.getLessonInstructor().equals(account.getUsername()) ||
                    lesson.getStudentList().contains(account.getUsername());
        }

        private boolean withinNotificationTimer(Lesson lesson, Account account)
        {
            Calendar today = Calendar.getInstance();

            System.out.println("Current Time" + TimeUnit.MILLISECONDS.toMinutes(today.getTime().getTime()));
            System.out.println("Lesson Time" + TimeUnit.MILLISECONDS.toMinutes(lesson.getLessonDate().getTime())+ "For Lesson " + lesson.getId());
            System.out.println("Account Notification Minutes" + account.getNotificationInMinutes() + " For Account " + account.getUsername());

            return TimeUnit.MILLISECONDS.toMinutes(lesson.getLessonDate().getTime()) - account.getNotificationInMinutes() ==
                    TimeUnit.MILLISECONDS.toMinutes(today.getTime().getTime());

        }

}

