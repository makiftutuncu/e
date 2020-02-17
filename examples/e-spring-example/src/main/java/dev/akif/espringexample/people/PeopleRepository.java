package dev.akif.espringexample.people;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dev.akif.espringexample.errors.Errors;
import dev.akif.espringexample.crud.Repository;
import dev.akif.espringexample.people.dto.PersonDTO;
import dev.akif.espringexample.people.model.Person;
import e.java.Maybe;

@Component
public class PeopleRepository implements Repository<Person, PersonDTO> {
    private final PeopleJPARepository jpa;

    @Autowired
    public PeopleRepository(PeopleJPARepository jpa) {
        this.jpa = jpa;
    }

    @Override public Maybe<List<Person>> getAll() {
        return Maybe.catching(
            () -> toList(jpa.findAll()),
            t  -> Errors.database.message("Cannot get all people!").cause(t)
        );
    }

    @Override public Maybe<Optional<Person>> getById(long id) {
        return Maybe.catching(
            () -> jpa.findById(id),
            t  -> Errors.database.message("Cannot get person by id!").cause(t).data("id", id)
        );
    }

    @Override public Maybe<Person> create(PersonDTO personDTO) {
        return Maybe.catching(
            () -> {
                Person person = personDTO.toPerson();
                jpa.save(person);
                return person;
            },
            t -> Errors.database
                       .message("Cannot create person!")
                       .cause(t)
                       .data("name", personDTO.getName())
                       .data("age",  personDTO.getAge())
        );
    }

    @Override public Maybe<Person> update(long id, PersonDTO personDTO) {
        return Maybe.catchingMaybe(
            () -> Maybe.nullable(
                jpa.findById(id).orElse(null),
                () -> Errors.notFound.message("Cannot find person to update!").data("id", id)
            ).map(person ->
                jpa.save(personDTO.updated(person))
            ),
            t -> Errors.database
                       .message("Cannot update person!")
                       .cause(t)
                       .data("name", personDTO.getName())
                       .data("age",  personDTO.getAge())
        );
    }

    @Override public Maybe<Person> delete(long id) {
        return Maybe.catchingMaybe(
            () -> Maybe.nullable(
                jpa.findById(id).orElse(null),
                () -> Errors.notFound.message("Cannot find person to delete!").data("id", id)
            ).map(person -> {
                jpa.delete(person);
                return person;
            }),
            t -> Errors.database
                       .message("Cannot delete person!")
                       .cause(t)
                       .data("id", id)
        );
    }
}
