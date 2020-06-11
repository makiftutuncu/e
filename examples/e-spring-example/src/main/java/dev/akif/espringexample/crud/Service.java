package dev.akif.espringexample.crud;

import java.util.List;

import e.java.EOr;

public interface Service<DTO, DTOWithId> {
    EOr<List<DTOWithId>> getAll();

    EOr<DTOWithId> getById(long id);

    EOr<DTOWithId> create(DTO dto);

    EOr<DTOWithId> update(long id, DTO dto);

    EOr<DTOWithId> delete(long id);
}
