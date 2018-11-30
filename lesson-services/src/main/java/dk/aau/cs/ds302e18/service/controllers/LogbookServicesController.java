package dk.aau.cs.ds302e18.service.controllers;

import dk.aau.cs.ds302e18.service.models.Logbook;
import dk.aau.cs.ds302e18.service.models.LogbookModel;
import dk.aau.cs.ds302e18.service.models.LogbookNotFoundException;
import dk.aau.cs.ds302e18.service.models.LogbookRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/logbook")
public class LogbookServicesController {

    private final LogbookRepository logbookRepository;

    public LogbookServicesController(LogbookRepository logbookRepository) {
        super();
        this.logbookRepository = logbookRepository;
    }

    //Returns all logbooks in the repository
    @GetMapping
    public List<Logbook> GetAllLogbooks() {
        return new ArrayList<>(this.logbookRepository.findAll());
    }

    //Returns a single logbook in the repository by id
    @GetMapping("/{id}")
    public Logbook GetLogbook(@PathVariable long id) {
        Optional<Logbook> optionalLogbook = this.logbookRepository.findById(id);
        if(optionalLogbook.isPresent()) {
            return optionalLogbook.get();
        }
        throw new LogbookNotFoundException("Logbook: " + id + " not found.");
    }

    //Adds a logbook to the repository
    @PostMapping
    public ResponseEntity<Logbook> addLogbook(@RequestBody LogbookModel model) {
        Logbook logbook = this.logbookRepository.save(model.translateModelToLogbook());
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(logbook.getId()).toUri();
        return ResponseEntity.created(location).body(logbook);
    }

    //Deletes a logbook in the repository by id
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Void> deleteLogbook(@PathVariable long id) {
        Optional<Logbook> optionalLogbook = this.logbookRepository.findById(id);
        if(optionalLogbook.isPresent()){
            this.logbookRepository.deleteById(id);
            return new ResponseEntity<Void>(HttpStatus.OK);
        }
        throw new LogbookNotFoundException("Logbook: " + id + " not found.");
    }
}
