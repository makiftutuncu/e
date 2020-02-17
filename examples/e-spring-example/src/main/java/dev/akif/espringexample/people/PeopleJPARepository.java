package dev.akif.espringexample.people;

import org.springframework.data.repository.CrudRepository;

import dev.akif.espringexample.people.model.Person;

public interface PeopleJPARepository extends CrudRepository<Person, Long> {}
