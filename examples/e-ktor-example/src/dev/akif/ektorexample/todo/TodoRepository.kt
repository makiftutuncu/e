package dev.akif.ektorexample.todo

import dev.akif.ektorexample.common.Errors
import dev.akif.ektorexample.common.Repository
import dev.akif.ektorexample.common.ZDTProvider
import dev.akif.ektorexample.database.DB
import e.kotlin.*
import org.jetbrains.exposed.sql.*
import java.time.ZoneOffset

class TodoRepository(override val db: DB, val zdt: ZDTProvider) : Repository<Todo>(db) {
    fun create(userId: Long, createTodo: CreateTodo): EOr<Todo> {
        val now = zdt.now()

        return run {
            TodoTable.insertAndGetId {
                it[TodoTable.userId]  = userId
                it[title]             = createTodo.title
                it[details]           = createTodo.details
                it[time]              = now.toInstant()
            }
        }.map { id ->
            Todo.from(id.value, userId, createTodo, now)
        }
    }

    fun getAllByUserId(userId: Long): EOr<List<Todo>> =
        run {
            TodoTable
                .select { TodoTable.userId eq userId }
                .map { row -> TodoTable.toTodo(row) }
        }

    fun getById(id: Long, userId: Long): EOr<Todo?> =
        run {
            TodoTable
                .select { TodoTable.id eq id }
                .singleOrNull()
                ?.let { TodoTable.toTodo(it) }
        }.flatMap { todo ->
            if (todo != null && todo.userId != userId) {
                Errors.notFound.message("Todo $id is not found!").toEOr()
            } else {
                todo.orE()
            }
        }

    fun update(todo: Todo, updateTodo: UpdateTodo): EOr<Todo> {
        val now = zdt.now()

        return run {
            TodoTable.update({ (TodoTable.id eq todo.id) }) {
                it[title]   = updateTodo.title ?: todo.title
                it[details] = updateTodo.details
                it[time]    = now.toInstant()
            }
        }.map {
            todo.updatedWith(updateTodo).copy(time = now)
        }
    }

    fun delete(id: Long): EOr<Unit> =
        run<Unit> {
            TodoTable.deleteWhere {
                TodoTable.id eq id
            }
        }

    override fun convertTo(row: ResultRow): Todo  =
        Todo(
            row[TodoTable.id].value,
            row[TodoTable.userId],
            row[TodoTable.title],
            row[TodoTable.details],
            row[TodoTable.time].atZone(ZoneOffset.UTC)
        )
}
