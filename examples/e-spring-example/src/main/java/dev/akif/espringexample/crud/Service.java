package dev.akif.espringexample.crud;

import java.util.List;

import e.java.Maybe;

public interface Service<DTO, DTOWithId> {
    Maybe<List<DTOWithId>> getAll();

    Maybe<DTOWithId> getById(long id);

    Maybe<DTOWithId> create(DTO dto);

    Maybe<DTOWithId> update(long id, DTO dto);

    Maybe<DTOWithId> delete(long id);
}
