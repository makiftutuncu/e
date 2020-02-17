package dev.akif.espringexample.people;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.akif.espringexample.crud.Controller;
import dev.akif.espringexample.people.dto.PersonDTO;
import dev.akif.espringexample.people.dto.PersonDTOWithId;
import e.java.Maybe;

@RestController
@RequestMapping(value = "/people", produces = MediaType.APPLICATION_JSON_VALUE)
public class PeopleController extends Controller<PersonDTO, PersonDTOWithId> {
    private final PeopleService service;

    @Autowired
    public PeopleController(PeopleService service) {
        this.service = service;
    }

    @Override public ResponseEntity<Maybe<List<PersonDTOWithId>>> getAll() {
        return respond(service.getAll());
    }

    @Override public ResponseEntity<Maybe<PersonDTOWithId>> getById(@PathVariable long id) {
        return respond(service.getById(id));
    }

    @Override public ResponseEntity<Maybe<PersonDTOWithId>> create(@RequestBody PersonDTO personDTO) {
        return respond(service.create(personDTO), HttpStatus.CREATED);
    }

    @Override public ResponseEntity<Maybe<PersonDTOWithId>> update(@PathVariable long id, @RequestBody PersonDTO personDTO) {
        return respond(service.update(id, personDTO));
    }

    @Override public ResponseEntity<Maybe<PersonDTOWithId>> delete(@PathVariable long id) {
        return respond(service.delete(id));
    }
}
