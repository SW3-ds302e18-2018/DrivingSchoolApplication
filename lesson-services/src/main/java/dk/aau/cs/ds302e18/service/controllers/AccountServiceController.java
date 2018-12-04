package dk.aau.cs.ds302e18.service.controllers;

import dk.aau.cs.ds302e18.service.models.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/account")
public class AccountServiceController
{
    private final AccountRespository accountRespository;

    public AccountServiceController(AccountRespository accountRespository){
        super();
        this.accountRespository = accountRespository;
    }

    /* Returns all the lessons in the database in a list */
    @GetMapping
    public List<Account> getAllAccounts(){
        return new ArrayList<>(this.accountRespository.findAll());
    }

    /* Get = responsible for retrieving information only */
    @GetMapping("/{id}")
    public Account getAccount(@PathVariable Long id){
        Optional<Account> account = this.accountRespository.findById(id);
        if(account.isPresent()){
            return account.get();
        }
        throw new LessonNotFoundException("Lesson not found with id: " + id);
    }


    /* Post = responsible for posting new information directly after it has been created to the website, and create fitting
    links to the new information. */
    @PostMapping
    public ResponseEntity<Account> addAccount(@RequestBody AccountModel model){
        /* Translates the input entered in the add account menu into input that can be entered in the database. */
        Account account = this.accountRespository.save(model.translateModelToAccount());
        /* The new account will be placed in the current browser /id , with an id that matches the entered lessons ID. */
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(account.getId()).toUri();
        /* The connection to the new account is created. */
        return ResponseEntity.created(location).body(account);
    }


    /* Put = responsible for updating existing database entries*/
    @PutMapping("/{id}")
    public Account updateLesson(@PathVariable Long id, @RequestBody AccountModel model){
        /* Throw an error if the selected lesson do not exist. */
        Optional<Account> existing = this.accountRespository.findById(id);
        if(!existing.isPresent()){
            throw new LessonNotFoundException("Lesson not found with id: " + id);
        }
        /* Translates input from the interface into an lesson object */
        Account account = model.translateModelToAccount();
        /* Uses the ID the lesson already had to save the lesson */
        account.setId(id);
        return this.accountRespository.save(account);
    }

    /* NOT IMPLEMENTED: Delete = responsible for deleting database entries. */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.RESET_CONTENT)
    public void deleteLesson(@PathVariable Long id){
        Optional<Account> existing = this.accountRespository.findById(id);
        if(!existing.isPresent()){
            throw new LessonNotFoundException("Lesson not found with id: " + id);
        }
        this.accountRespository.deleteById(id);
    }
}
