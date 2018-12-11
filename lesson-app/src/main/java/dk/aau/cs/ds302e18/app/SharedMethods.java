package dk.aau.cs.ds302e18.app;

import dk.aau.cs.ds302e18.app.auth.AuthGroup;
import dk.aau.cs.ds302e18.app.auth.AuthGroupRepository;
import dk.aau.cs.ds302e18.app.domain.*;
import dk.aau.cs.ds302e18.app.service.AccountService;
import dk.aau.cs.ds302e18.app.service.LessonService;

import java.util.ArrayList;
import java.util.List;

public class SharedMethods {

    public List<Account> findAccountsOfType(String accountType, AccountService accountService, AuthGroupRepository authGroupRepository) {
        List<AuthGroup> authGroups = authGroupRepository.findAll();
        List<Account> accountList = accountService.getAllAccounts();
        List<Account> accountsOfSelectedType = new ArrayList<>();

        /* When an account is created it is at the same time added to authGroup. Elements in a result-set are per default
           fetched with the order they were entered in the database, so account[0] will be the same as the
           authGroup[0]. This means that we do not have to manually check which authGroups matches which accounts. */
        for (int i = 0; i < accountList.size(); i++) {
            if (authGroups.get(i).getAuthGroup().equals(accountType)) {
                accountsOfSelectedType.add(accountList.get(i));
            }
        }
        return accountsOfSelectedType;
    }


    public void setInstructorFullName(List<Course> courseList, AccountService accountService, boolean takesCourseList) {
        /*  Finds and sets the full name for every instructor in a courseList */
        List<Account> accounts = accountService.getAllAccounts();
        String firstName = "";
        String lastName = "";
        for (Course course : courseList) {
            for (Account account : accounts) {
                if (course.getInstructorUsername().equals(account.getUsername())) {
                    firstName = account.getFirstName();
                    lastName = account.getLastName();
                }
            }
            String fullName = firstName + " " + lastName;
            course.setInstructorFullName(fullName);
        }
    }

    public void setInstructorFullName(List<Lesson> lessonList, AccountService accountService) {
        /*  Finds and sets the full name for every instructor in a courseList */
        List<Account> accounts = accountService.getAllAccounts();
        String firstName = "";
        String lastName = "";
        for (Lesson lesson : lessonList) {
            for (Account account : accounts) {
                if (lesson.getLessonInstructor().equals(account.getUsername())) {
                    firstName = account.getFirstName();
                    lastName = account.getLastName();
                }
            }
            String fullName = firstName + " " + lastName;
            lesson.setInstructorFullName(fullName);
        }
    }

    public void setInstructorFullName(Lesson lesson, AccountService accountService) {
        /*  Finds and sets the full name for every instructor in a courseList */
        String firstName = accountService.getAccount(lesson.getLessonInstructor()).getFirstName();
        String lastName = accountService.getAccount(lesson.getLessonInstructor()).getLastName();
        String fullName = firstName + " " + lastName;
        lesson.setInstructorFullName(fullName);
    }

    public void setInstructorFullName(Course course, AccountService accountService) {
        /*  Finds and sets the full name for every instructor in a courseList */
        String firstName = accountService.getAccount(course.getInstructorUsername()).getFirstName();
        String lastName = accountService.getAccount(course.getInstructorUsername()).getLastName();
        String fullName = firstName + " " + lastName;
        course.setInstructorFullName(fullName);
    }

    public String saveStringListAsSingleString(ArrayList<String> studentNameList) {
        String combinedString = "";
        for (String student : studentNameList) {
            combinedString += student + ",";
        }
        return combinedString;
    }

    public ArrayList<String> saveStringsSeparatedByCommaAsArray(String string) {
        ArrayList<String> studentList = new ArrayList<>();
        String[] parts = string.split(",");
        for (String part : parts) {
            studentList.add(part);
        }
        return studentList;
    }

    /* Checks if username is in String. Not the same as contains, as they need to be equal to the username, not
     * a substring of it.  */
    public boolean isUsernameInString(String username, String string) {
        boolean usernameExistsInString = false;
        SharedMethods sharedMethods = new SharedMethods();
        ArrayList<String> studentUsernames = sharedMethods.saveStringsSeparatedByCommaAsArray(string);
        for (String student : studentUsernames) {
            if (student.equals(username)) {
                usernameExistsInString = true;
            }
        }
        return usernameExistsInString;
    }


    public void updateUsernamesAssociatedWithCourse(long courseID, String updatedString, LessonService lessonService, boolean isInstructor) {
        List<Lesson> lessons = lessonService.getAllLessons();
        /* Replaces studentUsernames in every lesson with the courseID specified, with the updatedString */
        if(!isInstructor) {
            for (Lesson lesson : lessons) {
                /* If the student that is being added to the course is in a lesson associated with that courseID, update it. */
                if (lesson.getCourseId() == courseID) {
                    /* Sets the LessonModel with the values of the current lesson before it has been changed. */
                    LessonModel updatedLesson = lesson.translateLessonToModel();
                    /* Updates the student list */
                    updatedLesson.setStudentList(updatedString);
                    lessonService.updateLesson(lesson.getId(), updatedLesson);
                }
            }
        }
        /* If the instructor is in the lesson and the lesson has not been completed, update it. If it has been
         * completed the original instructor should still be registered with the lesson so he gets his salary. */
        if (isInstructor) {
            for (Lesson lesson : lessons) {
                if ((lesson.getCourseId() == courseID) && (lesson.getLessonState() == LessonState.PENDING)) {
                    /* Sets the LessonModel with the values of the current lesson before it has been changed. */
                    LessonModel updatedLesson = lesson.translateLessonToModel();
                    /* Updates the student list */
                    updatedLesson.setLessonInstructor(updatedString);
                    lessonService.updateLesson(lesson.getId(), updatedLesson);
                }
            }
        }
    }
}

