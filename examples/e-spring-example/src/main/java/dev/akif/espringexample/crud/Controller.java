package dev.akif.espringexample.crud;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import e.java.Maybe;

public abstract class Controller<DTO, DTOWithId> {
    @GetMapping
    public abstract ResponseEntity<Maybe<List<DTOWithId>>> getAll();

    @GetMapping(value = "/{id}")
    public abstract ResponseEntity<Maybe<DTOWithId>> getById(@PathVariable long id);

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public abstract ResponseEntity<Maybe<DTOWithId>> create(@RequestBody DTO dto);

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public abstract ResponseEntity<Maybe<DTOWithId>> update(@PathVariable long id, @RequestBody DTO dto);

    @DeleteMapping(value = "/{id}")
    public abstract ResponseEntity<Maybe<DTOWithId>> delete(@PathVariable long id);

    protected <A> ResponseEntity<Maybe<A>> respond(Maybe<A> maybe, HttpStatus status) {
        return maybe.fold(
            e     -> ResponseEntity.status(e.code()).body(Maybe.failure(e)),
            value -> ResponseEntity.status(status).body(Maybe.success(value))
        );
    }

    protected <A> ResponseEntity<Maybe<A>> respond(Maybe<A> maybe) {
        return respond(maybe, HttpStatus.OK);
    }
}
