package dev.akif.ektorexample.todo

import dev.akif.ektorexample.common.asId
import dev.akif.ektorexample.common.respondMaybe
import e.kotlin.Maybe
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.routing.*

fun Route.todo(service: TodoService) {
    val controller = TodoController(service)

    route("/todo") {
        post("/{userId}") {
            val userId     = call.parameters["userId"]
            val createTodo = call.receive<CreateTodo>()

            call.respondMaybe(controller.create(userId, createTodo), HttpStatusCode.Created)
        }

        get("/{userId}") {
            val userId = call.parameters["userId"]

            call.respondMaybe(controller.getAllByUserId(userId))
        }

        get("/{userId}/{id}") {
            val id     = call.parameters["id"]
            val userId = call.parameters["userId"]

            call.respondMaybe(controller.getById(id, userId))
        }

        put("/{userId}/{id}") {
            val id         = call.parameters["id"]
            val userId     = call.parameters["userId"]
            val updateTodo = call.receive<UpdateTodo>()

            call.respondMaybe(controller.update(id, userId, updateTodo))
        }

        delete("/{userId}/{id}") {
            val id     = call.parameters["id"]
            val userId = call.parameters["userId"]

            call.respondMaybe(controller.delete(id, userId))
        }
    }
}

class TodoController(val service: TodoService) {
    fun create(userIdStr: String?, createTodo: CreateTodo): Maybe<Todo> =
        userIdStr.asId().flatMap { userId ->
            service.create(userId, createTodo)
        }

    fun getAllByUserId(userIdStr: String?): Maybe<List<Todo>> =
        userIdStr.asId().flatMap { userId ->
            service.getAllByUserId(userId)
        }

    fun getById(idStr: String?, userIdStr: String?): Maybe<Todo> =
        idStr.asId().flatMap { id ->
            userIdStr.asId().flatMap { userId ->
                service.getById(id, userId)
            }
        }

    fun update(idStr: String?, userIdStr: String?, updateTodo: UpdateTodo): Maybe<Todo> =
        idStr.asId().flatMap { id ->
            userIdStr.asId().flatMap { userId ->
                service.update(id, userId, updateTodo)
            }
        }

    fun delete(idStr: String?, userIdStr: String?): Maybe<Unit> =
        idStr.asId().flatMap { id ->
            userIdStr.asId().flatMap { userId ->
                service.delete(id, userId)
            }
        }
}
