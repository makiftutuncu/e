package dev.akif.espringexample.errors;

import org.springframework.http.HttpStatus;

import e.java.E;

public final class Errors {
    public static final E invalidData = E.fromName("invalid-data").message("Provided data is invalid!").code(HttpStatus.BAD_REQUEST.value());
    public static final E notFound    = E.fromName("not-found").message("Requested resource does not exist!").code(HttpStatus.NOT_FOUND.value());
    public static final E database    = E.fromName("database-error").message("A database error occurred!").code(HttpStatus.INTERNAL_SERVER_ERROR.value());
    public static final E unexpected  = E.fromName("unexpected-error").message("An unexpected error occurred!").code(HttpStatus.INTERNAL_SERVER_ERROR.value());

    private Errors() {}
}
