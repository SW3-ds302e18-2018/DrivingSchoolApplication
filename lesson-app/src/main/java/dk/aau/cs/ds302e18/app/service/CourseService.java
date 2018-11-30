package dk.aau.cs.ds302e18.app.service;

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
public class CourseService
{
    private static final String REQUESTS = "/course";
    private static final String SLASH = "/";

    @Value("${ds.service.url}")
    private String courseServiceUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    /* Retrieves an list of store from the 8100 server and returns it as list of lessons in the format specified in
       the Lesson class. */
    public List<Course> getAllCourseRequests()
    {
        String url = courseServiceUrl + REQUESTS;
        HttpEntity<String> request = new HttpEntity<>(null, null);
        return this.restTemplate.exchange(url, HttpMethod.GET, request, new ParameterizedTypeReference<List<Course>>() { }).getBody();
    }

    public Course addCourse(CourseModel courseModel)
    {
        String url = courseServiceUrl + REQUESTS + SLASH + "addCourse";
        HttpEntity<CourseModel> request = new HttpEntity<>(courseModel, null);
        return this.restTemplate.exchange(url, HttpMethod.POST, request, Course.class).getBody();
    }



    public Course getCourse(long id) {
        String url = courseServiceUrl + REQUESTS + SLASH + id;
        HttpEntity<String> request = new HttpEntity<>(null, null);
        return this.restTemplate.exchange(url, HttpMethod.GET, request, Course.class).getBody();
    }

    public Course getLastCourseOrderedByID() {
        String url = courseServiceUrl + REQUESTS + SLASH + "getLastCourse";
        HttpEntity<String> request = new HttpEntity<>(null, null);
        return this.restTemplate.exchange(url, HttpMethod.GET, request, Course.class).getBody();
    }

    public void deleteCourse(long id){
        String url = courseServiceUrl + REQUESTS + SLASH + "delete" + SLASH + id;
        HttpEntity<String> request = new HttpEntity<>(null, null);
        restTemplate.exchange(url, HttpMethod.DELETE, request, Course.class).getBody();
    }


    public Course updateCourse(long id, CourseModel storeModel) {
        System.out.println(storeModel);
        String url = courseServiceUrl + REQUESTS + SLASH + id;
        HttpEntity<CourseModel> request = new HttpEntity<>(storeModel, null);
        return this.restTemplate.exchange(url, HttpMethod.PUT, request, Course.class).getBody();
    }
}
