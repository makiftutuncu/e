package dev.akif.espringexample.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

import e.java.E;

@ControllerAdvice
public class ErrorHandler {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<E> handle(NoHandlerFoundException exception) {
        logger.info("No handler found!");

        return handle(Errors.notFound.data("method", exception.getHttpMethod()).data("url", exception.getRequestURL()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<E> handle(Exception exception) {
        logger.error("Caught an unhandled exception!", exception);

        return handle(Errors.unexpected.cause(exception));
    }

    private ResponseEntity<E> handle(E e) {
        return ResponseEntity.status(e.code()).body(e);
    }
}
