package dk.aau.cs.ds302e18.service.controllers;

import dk.aau.cs.ds302e18.service.models.Store;
import dk.aau.cs.ds302e18.service.models.StoreModel;
import dk.aau.cs.ds302e18.service.models.StoreNotFoundException;
import dk.aau.cs.ds302e18.service.models.StoreRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/storeadmin")
public class StoreServicesController {
    private final StoreRepository storeRepository;

    public StoreServicesController(StoreRepository storeRepository) {
        super();
        this.storeRepository = storeRepository;
    }

    @GetMapping
    public List<Store> getAllStoreRequests() {
        return new ArrayList<>(this.storeRepository.findAll());
    }

    /* Get = responsible for retrieving information only */
    @GetMapping("/{id}")
    public Store getStore(@PathVariable Long id) {
        Optional<Store> store = this.storeRepository.findById(id);
        if (store.isPresent()) {
            return store.get();
        }
        throw new StoreNotFoundException("Store not found with id: " + id);
    }

    /* Post = responsible for posting new information directly after it has been created to the website, and create fitting
    links to the new information. */
    @PostMapping
    public ResponseEntity<Store> addStore(@RequestBody StoreModel model) {
        /* Translates the input entered in the add store menu into input that can be entered in the database. */
        Store store = this.storeRepository.save(model.translateModelToStore());
        /* The new store will be placed in the current browser /id , with an id that matches the entered storeadmin ID. */
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(store.getId()).toUri();
        /* The connection to the new store is created. */
        return ResponseEntity.created(location).body(store);
    }

    /* Put = responsible for updating existing database entries*/
    @PutMapping("/{id}")
    public Store updateStore(@PathVariable Long id, @RequestBody StoreModel model) {
        /* Throw an error if the selected store do not exist. */
        Optional<Store> existing = this.storeRepository.findById(id);
        if (!existing.isPresent()) {
            throw new StoreNotFoundException("Store not found with id: " + id);
        }
        /* Translates input from the interface into an store object */
        Store store = model.translateModelToStore();
        /* Uses the ID the store already had to save the store */
        store.setId(id);
        return this.storeRepository.save(store);
    }

    /* Delete = responsible for deleting database entries. */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.RESET_CONTENT)
    public void deleteStore(@PathVariable Long id) {
        Optional<Store> existing = this.storeRepository.findById(id);
        if (!existing.isPresent()) {
            throw new StoreNotFoundException("Store not found with id: " + id);
        }
        this.storeRepository.deleteById(id);
    }
}
