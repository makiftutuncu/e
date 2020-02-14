package dev.akif.espringexample.people;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dev.akif.espringexample.app.Errors;
import e.java.E;
import e.java.Maybe;

@Service
public class PeopleService {
    private PeopleRepository repository;
    private PeopleValidator validator;

    @Autowired
    public PeopleService(PeopleRepository repository, PeopleValidator validator) {
        this.repository = repository;
        this.validator  = validator;
    }

    public Maybe<List<PersonDTOWithId>> getAll() {
        return repository.getAll().map(people ->
            people.stream()
                  .map(PersonDTOWithId::new)
                  .collect(Collectors.toList())
        );
    }

    public Maybe<PersonDTOWithId> getById(long id) {
        return repository.getById(id).flatMap(personOpt -> {
            if (personOpt.isEmpty()) {
                E e = Errors.notFound.message("Person is not found!").data("id", String.valueOf(id));
                return Maybe.failure(e);
            }

            return Maybe.success(new PersonDTOWithId(personOpt.get()));
        });
    }

    public Maybe<PersonDTOWithId> create(PersonDTO personDTO) {
        return validator.validatePersonDTO(personDTO, false)
                        .flatMap(v -> repository.create(personDTO))
                        .map(PersonDTOWithId::new);
    }

    public Maybe<PersonDTOWithId> update(long id, PersonDTO personDTO) {
        return validator.validatePersonDTO(personDTO, true)
                        .flatMap(v -> repository.update(id, personDTO))
                        .map(PersonDTOWithId::new);
    }

    public Maybe<PersonDTOWithId> delete(long id) {
        return repository.delete(id).map(PersonDTOWithId::new);
    }
}
