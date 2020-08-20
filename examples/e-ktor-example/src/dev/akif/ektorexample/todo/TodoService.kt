package dev.akif.ektorexample.todo

import dev.akif.ektorexample.common.Errors
import e.kotlin.*

class TodoService(val repository: TodoRepository) {
    fun create(userId: Long, createTodo: CreateTodo): EOr<Todo> = repository.create(userId, createTodo)

    fun getAllByUserId(userId: Long): EOr<List<Todo>> = repository.getAllByUserId(userId)

    fun getById(id: Long, userId: Long): EOr<Todo> =
        repository.getById(id, userId).flatMap { todo ->
            todo.toEOr { Errors.notFound.message("Todo $id is not found!") }
        }

    fun update(id: Long, userId: Long, updateTodo: UpdateTodo): EOr<Todo> =
        getById(id, userId).flatMap { todo ->
            repository.update(todo, updateTodo)
        }

    fun delete(id: Long, userId: Long): EOr<Unit> =
        getById(id, userId).flatMap { todo ->
            repository.delete(todo.id)
        }
}
