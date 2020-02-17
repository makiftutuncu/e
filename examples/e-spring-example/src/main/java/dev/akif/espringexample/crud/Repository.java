package dev.akif.espringexample.crud;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import e.java.Maybe;

public interface Repository<Model, DTO> {
    Maybe<List<Model>> getAll();

    Maybe<Optional<Model>> getById(long id);

    Maybe<Model> create(DTO dto);

    Maybe<Model> update(long id, DTO dto);

    Maybe<Model> delete(long id);

    default <A> List<A> toList(Iterable<A> iterator) {
        List<A> list = new ArrayList<>();
        iterator.forEach(list::add);
        return list;
    }
}
