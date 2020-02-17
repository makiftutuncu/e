package dev.akif.ektorexample.todo

import java.time.ZonedDateTime

data class Todo(val id: Long = -1L,
                val userId: Long,
                val title: String,
                val details: String?,
                val time: ZonedDateTime) {
    fun updatedWith(updateTodo: UpdateTodo): Todo = copy(title = updateTodo.title ?: title, details = updateTodo.details)

    companion object {
        fun from(id: Long, userId: Long, createTodo: CreateTodo, time: ZonedDateTime): Todo = Todo(id, userId, createTodo.title, createTodo.details, time)
    }
}

data class CreateTodo(val title: String,
                      val details: String? = null)

data class UpdateTodo(val title: String? = null,
                      val details: String? = null)
