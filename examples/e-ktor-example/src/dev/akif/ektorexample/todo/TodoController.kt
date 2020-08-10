package dev.akif.ektorexample.todo

import dev.akif.ektorexample.common.asId
import dev.akif.ektorexample.common.respond
import e.kotlin.*
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

            call.respond(controller.create(userId, createTodo), HttpStatusCode.Created)
        }

        get("/{userId}") {
            val userId = call.parameters["userId"]

            call.respond(controller.getAllByUserId(userId))
        }

        get("/{userId}/{id}") {
            val id     = call.parameters["id"]
            val userId = call.parameters["userId"]

            call.respond(controller.getById(id, userId))
        }

        put("/{userId}/{id}") {
            val id         = call.parameters["id"]
            val userId     = call.parameters["userId"]
            val updateTodo = call.receive<UpdateTodo>()

            call.respond(controller.update(id, userId, updateTodo))
        }

        delete("/{userId}/{id}") {
            val id     = call.parameters["id"]
            val userId = call.parameters["userId"]

            call.respond(controller.delete(id, userId))
        }
    }
}

class TodoController(val service: TodoService) {
    fun create(userIdStr: String?, createTodo: CreateTodo): EOr<Todo> =
        userIdStr.asId().flatMap { userId ->
            service.create(userId, createTodo)
        }

    fun getAllByUserId(userIdStr: String?): EOr<List<Todo>> =
        userIdStr.asId().flatMap { userId ->
            service.getAllByUserId(userId)
        }

    fun getById(idStr: String?, userIdStr: String?): EOr<Todo> =
        idStr.asId().flatMap { id ->
            userIdStr.asId().flatMap { userId ->
                service.getById(id, userId)
            }
        }

    fun update(idStr: String?, userIdStr: String?, updateTodo: UpdateTodo): EOr<Todo> =
        idStr.asId().flatMap { id ->
            userIdStr.asId().flatMap { userId ->
                service.update(id, userId, updateTodo)
            }
        }

    fun delete(idStr: String?, userIdStr: String?): EOr<Unit> =
        idStr.asId().flatMap { id ->
            userIdStr.asId().flatMap { userId ->
                service.delete(id, userId)
            }
        }
}
