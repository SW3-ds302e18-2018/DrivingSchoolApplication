package dk.aau.cs.ds302e18.service.controllers;

import dk.aau.cs.ds302e18.service.models.Course;
import dk.aau.cs.ds302e18.service.models.CourseModel;
import dk.aau.cs.ds302e18.service.models.CourseNotFoundException;
import dk.aau.cs.ds302e18.service.models.CourseRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/course")
public class CourseServicesController
{

    private final CourseRepository courseRepository;

    public CourseServicesController(CourseRepository courseRepository){
        super();
        this.courseRepository = courseRepository;
    }

    /* Returns all the courses in the database in a list */
    @GetMapping
    public List<Course> getAllCourses(){
        return new ArrayList<>(this.courseRepository.findAll());
    }

    /* Get = responsible for retrieving information only */
    @GetMapping("/{id}")
    public Course getCourse(@PathVariable Long id){
        Optional<Course> course = this.courseRepository.findById(id);
        if(course.isPresent()){
            return course.get();
        }
        throw new CourseNotFoundException("Course not found with id: " + id);
    }

    /* Get = responsible for retrieving information only */
    @GetMapping("/getLastCourse")
    public Course getLastCourseOrderedByID(){
        Optional<Course> course = Optional.ofNullable(this.courseRepository.findFirstByOrderByCourseTableIDDesc());
        if(course.isPresent()){
            return course.get();
        }
        throw new CourseNotFoundException("Course not find last the last course.");
    }

    /* Post = responsible for posting new information directly after it has been created to the website, and create fitting
    links to the new information. */
    @PostMapping(value = "/addCourse")
    public ResponseEntity<Course> addCourse(@RequestBody CourseModel courseModel){
        Course course = new Course();
        course.setInstructorUsername(courseModel.getInstructorUsername());
        course.setCourseType(courseModel.getCourseType());
        if(courseModel.getStudentUsernames() == null) {
            course.setStudentUsernames("empty");
        }else {
            course.setStudentUsernames(courseModel.getStudentUsernames());
        }
        if(courseModel.getCourseStartDate() == null){
            /* Default date when no first lesson has been created is set to 2000. A date "should" be set immediately after when the initial
             * theory lessons are created in addCourseLessons, but in case an error has occurred the default date is set, to prevent
             * crashes when accessing the rest of the program caused by accessing an null database. An instructor can then chose
             * to delete the faulty course through the websites delete course functionality. */
            Date defaultDate = new Date(101,0,0);
            course.setCourseStartDate(defaultDate);
        } else {
            course.setCourseStartDate(courseModel.getCourseStartDate());
        }
        course = this.courseRepository.save(course);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(course.getCourseTableID()).toUri();
        return ResponseEntity.created(location).body(course);
    }




    /* Put = responsible for updating existing database entries*/
    @PutMapping("/{id}")
    public Course updateCourse(@PathVariable Long id, @RequestBody CourseModel courseModel){
        /* Throw an error if the selected course do not exist. */
        Optional<Course> existing = this.courseRepository.findById(id);
        if(!existing.isPresent()){
            throw new CourseNotFoundException("Course not found with id: " + id);
        }
        /* Translates input from the interface into an course object */
        Course course = courseModel.translateModelToCourse();
        /* Uses the ID the course already had to save the course */
        course.setCourseTableID(id);
        return this.courseRepository.save(course);
    }

    /* NOT IMPLEMENTED: Delete = responsible for deleting database entries. */
    @DeleteMapping("/delete/{id}")
    @ResponseStatus(HttpStatus.RESET_CONTENT)
    public void deleteCourse(@PathVariable Long id){
        Optional<Course> existing = this.courseRepository.findById(id);
        if(!existing.isPresent()){
            throw new CourseNotFoundException("Course not found with id: " + id);
        }
        this.courseRepository.deleteById(id);
    }
}