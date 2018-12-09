package dk.aau.cs.ds302e18.app;

import dk.aau.cs.ds302e18.app.auth.AuthGroup;
import dk.aau.cs.ds302e18.app.auth.AuthGroupRepository;
import dk.aau.cs.ds302e18.app.domain.Account;
import dk.aau.cs.ds302e18.app.domain.Course;
import dk.aau.cs.ds302e18.app.domain.Lesson;
import dk.aau.cs.ds302e18.app.service.AccountService;

import java.util.ArrayList;
import java.util.List;

public class SharedMethods {
    private AccountService accountService;
    private AuthGroupRepository authGroupRepository;

    public SharedMethods(AccountService accountService, AuthGroupRepository authGroupRepository) {
        this.accountService = accountService;
        this.authGroupRepository = authGroupRepository;
    }

    public List<Account> findAccountsOfType(String accountType) {
        List<AuthGroup> authGroups = this.authGroupRepository.findAll();
        List<Account> accountList = this.accountService.getAllAccounts();
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

    public void setStudentFullNamesFromUsernamesString(List<Lesson> lessonList){
        /*  Every student username list in account is separated and added to an String array. Then the
         *  username is used to fetch the full name of the student belonging to the username. If there are no students
         *  do not attempt to fetch their full name. */
        for(Lesson lesson: lessonList){
            ArrayList<String> fullNames = new ArrayList<>();
            if(lesson.getStudentList() != null && !lesson.getStudentList().equals("")) {
                ArrayList<String> listOfUsernames = saveStringsSeparatedByCommaAsArray(lesson.getStudentList());
                for (String username : listOfUsernames) {
                    String firstName = accountService.getAccount(username).getFirstName();
                    String lastName = accountService.getAccount(username).getLastName();
                    String fullName = firstName + " " + lastName;
                    fullNames.add(" " + fullName);
                }
                String studentFullNames = saveStringListAsSingleString(fullNames);
            /* The way saveStringListAsSingleString formats the list to an string is with an comma at the end of each
               object. The last of the commas is removed for appearance sake. */
                String studentFullNamesWithoutEndingComma = studentFullNames.substring(0, studentFullNames.length() - 1);

                lesson.setStudentFullNames(studentFullNamesWithoutEndingComma);
            }
        }
    }

    public void setInstructorFullName(List<Course> courseList, boolean takesCourseList) {
        /*  Finds and sets the full name for every instructor in a courseList */
        for (Course course : courseList) {
            String firstName = accountService.getAccount(course.getInstructorUsername()).getFirstName();
            String lastName = accountService.getAccount(course.getInstructorUsername()).getLastName();
            String fullName = firstName + " " + lastName;
            course.setInstructorFullName(fullName);
        }
    }

    public void setInstructorFullName(List<Lesson> lessonList) {
        /*  Finds and sets the full name for every instructor in a courseList */
        for (Lesson lesson : lessonList) {
            String firstName = accountService.getAccount(lesson.getLessonInstructor()).getFirstName();
            String lastName = accountService.getAccount(lesson.getLessonInstructor()).getLastName();
            String fullName = firstName + " " + lastName;
            lesson.setInstructorFullName(fullName);
        }
    }

    public void setInstructorFullName(Lesson lesson) {
        /*  Finds and sets the full name for every instructor in a courseList */
        String firstName = accountService.getAccount(lesson.getLessonInstructor()).getFirstName();
        String lastName = accountService.getAccount(lesson.getLessonInstructor()).getLastName();
        String fullName = firstName + " " + lastName;
        lesson.setInstructorFullName(fullName);
    }

    public void setInstructorFullName(Course course) {
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

}
