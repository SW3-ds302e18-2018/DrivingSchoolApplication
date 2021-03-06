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
public class NotificationScheduler
{
    private AccountRespository accountRespository;
    private LessonRepository lessonRepository;

    public NotificationScheduler(AccountRespository accountRespository, LessonRepository lessonRepository)
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
        // Converts all times to minutes, and takes the (Lesson Date Minus (-) Time the user want to receive the notification)
        // and matches it with current time in minutes.
        Calendar today = Calendar.getInstance();
        return TimeUnit.MILLISECONDS.toMinutes(lesson.getLessonDate().getTime()) - account.getNotificationInMinutes() ==
                TimeUnit.MILLISECONDS.toMinutes(today.getTime().getTime());
    }

}

