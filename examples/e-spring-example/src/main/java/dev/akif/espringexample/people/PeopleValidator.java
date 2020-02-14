package dev.akif.espringexample.people;

import org.springframework.stereotype.Component;

import dev.akif.espringexample.app.Errors;
import e.java.Maybe;

@Component
public class PeopleValidator {
    public static final int MAX_NAME_LENGTH = 16;

    public Maybe<Void> validateName(String name, boolean allowNull) {
        if (allowNull && name == null) {
            return Maybe.unit();
        } else if (!allowNull && name == null) {
            return Maybe.failure(Errors.invalidData.message("Name is invalid!").data("rules", "Name cannot be null!"));
        } else if (name.isBlank() || name.length() > MAX_NAME_LENGTH) {
            return Maybe.failure(
                    Errors.invalidData
                          .message("Name is invalid!")
                          .data("name", name)
                          .data("rules", "Name must be less than " + MAX_NAME_LENGTH + " characters long and it must not be empty.")
            );
        } else {
            return Maybe.unit();
        }
    }

    public Maybe<Void> validateAge(Integer age, boolean allowNull) {
        if (allowNull && age == null) {
            return Maybe.unit();
        } else if (!allowNull && age == null) {
            return Maybe.failure(Errors.invalidData.message("Age is invalid!").data("rules", "Age cannot be null!"));
        } else if (age < 0) {
            return Maybe.failure(
                    Errors.invalidData
                          .message("Age is invalid!")
                          .data("age", String.valueOf(age))
                          .data("rules", "Age must be a positive integer.")
            );
        } else {
            return Maybe.unit();
        }
    }

    public Maybe<Void> validatePersonDTO(PersonDTO personDTO, boolean allowNull) {
        return validateName(personDTO.getName(), allowNull).flatMap(n ->
               validateAge(personDTO.getAge(), allowNull).flatMap(a ->
               Maybe.unit()));
    }
}
