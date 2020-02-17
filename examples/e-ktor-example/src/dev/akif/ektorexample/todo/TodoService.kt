package dev.akif.ektorexample.todo

import dev.akif.ektorexample.common.Errors
import e.kotlin.Maybe
import e.kotlin.toMaybe

class TodoService(val repository: TodoRepository) {
    fun create(userId: Long, createTodo: CreateTodo): Maybe<Todo> = repository.create(userId, createTodo)

    fun getAllByUserId(userId: Long): Maybe<List<Todo>> = repository.getAllByUserId(userId)

    fun getById(id: Long, userId: Long): Maybe<Todo> =
        repository.getById(id, userId).flatMap { todo ->
            todo.toMaybe(Errors.notFound.message("Todo $id is not found!"))
        }

    fun update(id: Long, userId: Long, updateTodo: UpdateTodo): Maybe<Todo> =
        getById(id, userId).flatMap { todo ->
            repository.update(todo, updateTodo)
        }

    fun delete(id: Long, userId: Long): Maybe<Unit> =
        getById(id, userId).flatMap { todo ->
            repository.delete(todo.id)
        }
}
