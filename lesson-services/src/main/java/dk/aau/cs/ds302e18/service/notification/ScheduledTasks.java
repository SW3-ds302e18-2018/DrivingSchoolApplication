package dk.aau.cs.ds302e18.service.notification;

import dk.aau.cs.ds302e18.service.DBConnector;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.*;

@Component
public class ScheduledTasks {
    //private static final Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);

    // This scheduler runs once every minute, therefore 60000 miliseconds
    @Scheduled(fixedRate = 60000)
    public void scheduleSendLessonNotifications() {

        // Initialize MySQL connection
        Connection conn = new DBConnector().createConnectionObject();

        try {

            Statement statement = conn.createStatement();

            // Only select lessons that are still actual
            ResultSet lessonResultSet = statement.executeQuery("SELECT `lesson_date`, `lesson_location`, `student_list` FROM `lesson` WHERE `lesson_date` >= NOW()");

            while (lessonResultSet.next()) {

                // Get the lesson date from the lesson, and set its seconds to 0 to correctly get a whole minute
                Timestamp lessonDate = lessonResultSet.getTimestamp("lesson_date");
                lessonDate.setSeconds(0);

                // Retrieve the rest of the column values from the lesson table
                String lessonLocation = lessonResultSet.getString("lesson_location");
                String studentList = lessonResultSet.getString("student_list");
                String[] studentListArray = studentList.split(",");

                for(String studentUsername : studentListArray) {

                    Statement usernameResultStatement = conn.createStatement();

                    ResultSet usernameResultSet = usernameResultStatement.executeQuery("SELECT `email`, `phone_number`, `notification_reminder` FROM `account` WHERE `username` = '" + studentUsername + "'");

                    while(usernameResultSet.next()) {

                        // Retrieve the time in minutes before the student should receive a notification
                        int notificationReminderInMinutes = usernameResultSet.getInt("notification_reminder");

                        if (notificationReminderInMinutes == 0) // This student has set their notification reminder to 0, which means to skip them.
                            continue;

                        Timestamp currentTime = new Timestamp(System.currentTimeMillis()); // Create TimeStamp, set its nanoseconds and seconds to 0, along with setting it according to the notification time
                        currentTime.setSeconds(0);
                        currentTime.setNanos(0);
                        currentTime = new Timestamp(currentTime.getTime() + notificationReminderInMinutes * 60 * 1000);

                        if (!currentTime.equals(lessonDate))
                            continue; // If the student is not supposed to receive a notification, go to the next student.

                        // This student needs a notification. Retrieve the last two columns.
                        String email = usernameResultSet.getString("email");
                        String phoneNumber = usernameResultSet.getString("phone_number");

                        String message = "Hello " + studentUsername + ",\n You have a lesson at " + lessonLocation + " on the\n" + lessonDate;
                        new Notification(message, "ds302e18@gmail.com", email, "+45" + phoneNumber); // +45 is the region code for Denmark.
                    }

                    usernameResultSet.close();
                }
            }

            lessonResultSet.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}