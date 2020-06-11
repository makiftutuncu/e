package dev.akif.espringexample.people;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import dev.akif.espringexample.errors.Errors;
import dev.akif.espringexample.crud.Service;
import dev.akif.espringexample.people.dto.PersonDTO;
import dev.akif.espringexample.people.dto.PersonDTOWithId;
import e.java.EOr;

@Component
public class PeopleService implements Service<PersonDTO, PersonDTOWithId> {
    private final PeopleRepository repository;
    private final PeopleValidator validator;

    @Autowired
    public PeopleService(PeopleRepository repository, PeopleValidator validator) {
        this.repository = repository;
        this.validator  = validator;
    }

    @Override public EOr<List<PersonDTOWithId>> getAll() {
        return repository.getAll().map(people ->
            people.stream().map(PersonDTOWithId::new).collect(Collectors.toList())
        );
    }

    @Override public EOr<PersonDTOWithId> getById(long id) {
        return repository.getById(id)
            .flatMap(personOpt ->
                EOr.fromOptional(personOpt, () ->
                    Errors.notFound
                          .message("Person is not found!")
                          .data("id", id)
                )
            )
            .map(PersonDTOWithId::new);
    }

    @Override public EOr<PersonDTOWithId> create(PersonDTO personDTO) {
        return validator.validatePersonDTO(personDTO, false)
                        .andThen(() -> repository.create(personDTO))
                        .map(PersonDTOWithId::new);
    }

    @Override public EOr<PersonDTOWithId> update(long id, PersonDTO personDTO) {
        return validator.validatePersonDTO(personDTO, true)
                        .andThen(() -> repository.update(id, personDTO))
                        .map(PersonDTOWithId::new);
    }

    @Override public EOr<PersonDTOWithId> delete(long id) {
        return repository.delete(id).map(PersonDTOWithId::new);
    }
}
