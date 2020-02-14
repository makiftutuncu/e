package dev.akif.espringexample.people;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import dev.akif.espringexample.app.Errors;
import e.java.E;
import e.java.Maybe;

@Repository
public class PeopleRepository {
    private static final Map<Long, Person> database = new LinkedHashMap<>();
    private static long maxId = 0L;

    public PeopleRepository() {}

    public Maybe<List<Person>> getAll() {
        return Maybe.success(new ArrayList<>(database.values()));
    }

    public Maybe<Optional<Person>> getById(long id) {
        return Maybe.success(Optional.ofNullable(database.get(id)));
    }

    public Maybe<Person> create(PersonDTO personDTO) {
        long nextId = maxId + 1;
        Person person = personDTO.toPerson(nextId);
        database.put(nextId, person);
        maxId = nextId;
        return Maybe.success(person);
    }

    public Maybe<Person> update(long id, PersonDTO personDTO) {
        Person person = database.get(id);

        if (person == null) {
            E e = Errors.notFound.message("Cannot find person to update!").data("id", String.valueOf(id));
            return Maybe.failure(e);
        }

        Person updated = personDTO.updated(person);

        database.put(id, updated);

        return Maybe.success(updated);
    }

    public Maybe<Person> delete(long id) {
        Person person = database.get(id);

        if (person == null) {
            E e = Errors.notFound.message("Cannot find person to delete!").data("id", String.valueOf(id));
            return Maybe.failure(e);
        }

        database.remove(id);

        return Maybe.success(person);
    }
}
