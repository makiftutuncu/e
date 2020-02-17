package dev.akif.espringexample.people;

import org.springframework.stereotype.Component;

import dev.akif.espringexample.errors.Errors;
import dev.akif.espringexample.people.dto.PersonDTO;
import e.java.Maybe;

@Component
public class PeopleValidator {
    public static final int MAX_NAME_LENGTH = 16;

    public Maybe<Void> validatePersonDTO(PersonDTO personDTO, boolean allowNull) {
        return validateName(personDTO.getName(), allowNull)
                .andThen(() -> validateAge(personDTO.getAge(), allowNull)
                .andThen(Maybe::unit));
    }

    private Maybe<Void> validateName(String name, boolean allowNull) {
        if (allowNull && name == null) {
            return Maybe.unit();
        }

        if (!allowNull && name == null) {
            return Errors.invalidData
                         .message("Name is invalid!")
                         .data("rules", "Name cannot be null!")
                         .toMaybe();
        }

        if (name.isBlank() || name.length() > MAX_NAME_LENGTH) {
            return Errors.invalidData
                         .message("Name is invalid!")
                         .data("name", name)
                         .data("rules", "Name must be less than " + MAX_NAME_LENGTH + " characters long and it must not be empty.")
                         .toMaybe();
        }

        return Maybe.unit();
    }

    private Maybe<Void> validateAge(Integer age, boolean allowNull) {
        if (allowNull && age == null) {
            return Maybe.unit();
        }

        if (!allowNull && age == null) {
            return Errors.invalidData
                         .message("Age is invalid!")
                         .data("rules", "Age cannot be null!")
                         .toMaybe();
        }

        if (age < 0) {
            return Errors.invalidData
                         .message("Age is invalid!")
                         .data("age", age)
                         .data("rules", "Age must be a positive integer.")
                         .toMaybe();
        }

        return Maybe.unit();
    }
}
