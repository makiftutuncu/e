package dev.akif.espringexample.app;

import e.java.E;

public final class Errors {
    public static final E invalidData = new E("invalid-data",     "Provided data is invalid!",          400);
    public static final E notFound    = new E("not-found",        "Requested resource does not exist!", 404);
    public static final E unexpected  = new E("unexpected-error", "An unexpected error occurred!",      500);

    private Errors() {}
}
