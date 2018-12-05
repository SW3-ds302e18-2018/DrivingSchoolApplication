package dk.aau.cs.ds302e18.service.controllers;

import dk.aau.cs.ds302e18.service.models.Lesson;
import dk.aau.cs.ds302e18.service.models.LessonModel;
import dk.aau.cs.ds302e18.service.models.LessonNotFoundException;
import dk.aau.cs.ds302e18.service.models.LessonRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/lessons")
public class LessonServicesController {
    private final LessonRepository repository;

    public LessonServicesController(LessonRepository repository) {
        super();
        this.repository = repository;
    }

    /* Returns all the lessons in the database in a list */
    @GetMapping
    public List<Lesson> getAllLessons() {
        return new ArrayList<>(this.repository.findAll());
    }

    /* Get = responsible for retrieving information only */
    @GetMapping("/{id}")
    public Lesson getLesson(@PathVariable Long id) {
        Optional<Lesson> lesson = this.repository.findById(id);
        if (lesson.isPresent()) {
            return lesson.get();
        }
        throw new LessonNotFoundException("Lesson not found with id: " + id);
    }

    /* Post = responsible for posting new information directly after it has been created to the website, and create fitting
    links to the new information. */
    @PostMapping
    public ResponseEntity<Lesson> addLesson(@RequestBody LessonModel model) {
        /* Translates the input entered in the add lesson menu into input that can be entered in the database. */
        Lesson lesson = this.repository.save(model.translateModelToLesson());
        /* The new lesson will be placed in the current browser /id , with an id that matches the entered lessons ID. */
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(lesson.getId()).toUri();
        /* The connection to the new lesson is created. */
        return ResponseEntity.created(location).body(lesson);
    }


    /* Put = responsible for updating existing database entries*/
    @PutMapping("/{id}")
    public Lesson updateLesson(@PathVariable Long id, @RequestBody LessonModel model) {
        /* Throw an error if the selected lesson do not exist. */
        Optional<Lesson> existing = this.repository.findById(id);
        if (!existing.isPresent()) {
            throw new LessonNotFoundException("Lesson not found with id: " + id);
        }
        /* Translates input from the interface into an lesson object */
        Lesson lesson = model.translateModelToLesson();
        /* Uses the ID the lesson already had to save the lesson */
        lesson.setId(id);
        return this.repository.save(lesson);
    }

    /* Delete = responsible for deleting database entries. */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.RESET_CONTENT)
    public void deleteLesson(@PathVariable Long id) {
        Optional<Lesson> existing = this.repository.findById(id);
        if (!existing.isPresent()) {
            throw new LessonNotFoundException("Lesson not found with id: " + id);
        }
        this.repository.deleteById(id);
    }
}