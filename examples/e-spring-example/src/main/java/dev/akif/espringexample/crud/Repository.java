package dev.akif.espringexample.crud;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import e.java.EOr;

public interface Repository<Model, DTO> {
    EOr<List<Model>> getAll();

    EOr<Optional<Model>> getById(long id);

    EOr<Model> create(DTO dto);

    EOr<Model> update(long id, DTO dto);

    EOr<Model> delete(long id);

    default <A> List<A> toList(Iterable<A> iterator) {
        List<A> list = new ArrayList<>();
        iterator.forEach(list::add);
        return list;
    }
}
