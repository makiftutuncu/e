package dev.akif.espringexample.people;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import e.java.Maybe;

@RestController
@RequestMapping(value = "/people", produces = MediaType.APPLICATION_JSON_VALUE)
public class PeopleController {
    private PeopleService service;

    @Autowired
    public PeopleController(PeopleService service) {
        this.service = service;
    }

    @GetMapping
    ResponseEntity<Maybe<List<PersonDTOWithId>>> getAll() {
        return respond(service.getAll());
    }

    @GetMapping(value = "/{id}")
    ResponseEntity<Maybe<PersonDTOWithId>> getById(@PathVariable long id) {
        return respond(service.getById(id));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Maybe<PersonDTOWithId>> create(@RequestBody PersonDTO personDTO) {
        return respond(service.create(personDTO), HttpStatus.CREATED);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Maybe<PersonDTOWithId>> update(@PathVariable long id, @RequestBody PersonDTO personDTO) {
        return respond(service.update(id, personDTO));
    }

    @DeleteMapping(value = "/{id}")
    ResponseEntity<Maybe<PersonDTOWithId>> delete(@PathVariable long id) {
        return respond(service.delete(id));
    }

    private <A> ResponseEntity<Maybe<A>> respond(Maybe<A> maybe, HttpStatus status) {
        return maybe.fold(
            e     -> ResponseEntity.status(e.code()).body(Maybe.failure(e)),
            value -> ResponseEntity.status(status).body(Maybe.success(value))
        );
    }

    private <A> ResponseEntity<Maybe<A>> respond(Maybe<A> maybe) {
        return respond(maybe, HttpStatus.OK);
    }
}
