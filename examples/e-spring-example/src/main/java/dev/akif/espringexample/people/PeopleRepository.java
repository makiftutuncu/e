package dev.akif.espringexample.people;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dev.akif.espringexample.errors.Errors;
import dev.akif.espringexample.crud.Repository;
import dev.akif.espringexample.people.dto.PersonDTO;
import dev.akif.espringexample.people.model.Person;
import e.java.E;
import e.java.EOr;

@Component
public class PeopleRepository implements Repository<Person, PersonDTO> {
    private final PeopleJPARepository jpa;

    @Autowired
    public PeopleRepository(PeopleJPARepository jpa) {
        this.jpa = jpa;
    }

    @Override public EOr<List<Person>> getAll() {
        return EOr.catching(
            () -> toList(jpa.findAll()),
            t  -> Errors.database.message("Cannot get all people!").cause(E.fromThrowable(t))
        );
    }

    @Override public EOr<Optional<Person>> getById(long id) {
        return EOr.catching(
            () -> jpa.findById(id),
            t  -> Errors.database.message("Cannot get person by id!").cause(E.fromThrowable(t)).data("id", id)
        );
    }

    @Override public EOr<Person> create(PersonDTO personDTO) {
        return EOr.catching(
            () -> jpa.save(personDTO.toPerson()),
            t  -> Errors.database
                        .message("Cannot create person!")
                        .cause(E.fromThrowable(t))
                        .data("name", personDTO.getName())
                        .data("age",  personDTO.getAge())
        );
    }

    @Override public EOr<Person> update(long id, PersonDTO personDTO) {
        return EOr.catching(
            () -> jpa.findById(id),
            t -> Errors.database
                       .message("Cannot update person!")
                       .cause(E.fromThrowable(t))
                       .data("name", personDTO.getName())
                       .data("age",  personDTO.getAge())
        ).flatMap(person ->
            EOr.fromNullable(
                person.orElse(null),
                () -> Errors.notFound.message("Cannot find person to update!").data("id", id)
            )
        );
    }

    @Override public EOr<Person> delete(long id) {
        return EOr.catching(
            () -> jpa.findById(id),
            t -> Errors.database
                       .message("Cannot delete person!")
                       .cause(E.fromThrowable(t))
                       .data("id", id)
        ).flatMap(person ->
            EOr.fromNullable(
                person.orElse(null),
                () -> Errors.notFound.message("Cannot find person to delete!").data("id", id)
            )
        ).map(person -> {
            jpa.delete(person);
            return person;
        });
    }
}
