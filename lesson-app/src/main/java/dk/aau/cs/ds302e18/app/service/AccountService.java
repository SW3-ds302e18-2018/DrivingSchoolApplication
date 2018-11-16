package dk.aau.cs.ds302e18.app.service;

import dk.aau.cs.ds302e18.app.Account;
import dk.aau.cs.ds302e18.app.domain.Course;
import dk.aau.cs.ds302e18.app.domain.CourseModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/* Class responsible for reading lesson related data from the 8100 server. */
@Service
public class AccountService
{
    private static final String REQUESTS = "/account";
    private static final String SLASH = "/";

    @Value("${ds.service.url}")
    private String accountServiceUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    /* Retrieves an list of store from the 8100 server and returns it as list of lessons in the format specified in
       the Lesson class. */
    public List<Account> getAllAccounts()
    {
        String url = accountServiceUrl + REQUESTS;
        HttpEntity<String> request = new HttpEntity<>(null, null);
        return this.restTemplate.exchange(url, HttpMethod.GET, request, new ParameterizedTypeReference<List<Account>>() { }).getBody();
    }

    public Course addCourse(CourseModel courseModel)
    {
        String url = accountServiceUrl + REQUESTS;
        HttpEntity<CourseModel> request = new HttpEntity<>(courseModel, null);
        return this.restTemplate.exchange(url, HttpMethod.POST, request, Course.class).getBody();
    }


    public Account getAccount(long id) {
        String url = accountServiceUrl + REQUESTS + SLASH + id;
        HttpEntity<String> request = new HttpEntity<>(null, null);
        return this.restTemplate.exchange(url, HttpMethod.GET, request, Account.class).getBody();
    }


    public Course acceptCourseRequest(long id, CourseModel courseModel) {
        System.out.println(courseModel);
        String url = accountServiceUrl + REQUESTS + SLASH + id;
        HttpEntity<CourseModel> request = new HttpEntity<>(courseModel, null);
        return this.restTemplate.exchange(url, HttpMethod.PUT, request, Course.class).getBody();
    }

    public Course deleteCourse(CourseModel courseModel){
        String url = accountServiceUrl + REQUESTS + SLASH + "deleteCourse";
        HttpEntity<CourseModel> request = new HttpEntity<>(courseModel, null);
        return this.restTemplate.exchange(url, HttpMethod.DELETE, request, Course.class).getBody();
    }
}
