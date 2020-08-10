package dev.akif.ektorexample

import com.google.gson.GsonBuilder
import dev.akif.ektorexample.common.ZDT
import dev.akif.ektorexample.common.ZDTProvider
import dev.akif.ektorexample.common.registerErrorHandler
import dev.akif.ektorexample.database.DB
import dev.akif.ektorexample.todo.TodoRepository
import dev.akif.ektorexample.todo.TodoService
import dev.akif.ektorexample.todo.todo
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.features.StatusPages
import io.ktor.gson.GsonConverter
import io.ktor.http.ContentType
import io.ktor.response.respondText
import io.ktor.routing.Routing
import io.ktor.routing.get
import org.slf4j.LoggerFactory
import java.time.ZonedDateTime

val gson =
    GsonBuilder()
        .setPrettyPrinting()
        .serializeNulls()
        .registerTypeAdapter(ZonedDateTime::class.java, ZDT.gsonAdapter)
        .create()

fun Application.modules(db: DB,
                        zdt: ZDTProvider,
                        todoRepository: TodoRepository,
                        todoService: TodoService) {
    val logger = LoggerFactory.getLogger("todo")

    db.init()

    install(StatusPages) {
        registerErrorHandler(logger)
    }

    install(ContentNegotiation) {
        register(ContentType.Application.Json, GsonConverter(gson))
    }

    install(Routing) {
        get("/ping") {
            call.respondText("pong", ContentType.Text.Html)
        }

        todo(todoService)
    }
}

fun Application.defaultModules() {
    val db             = DB("file:./db/todo.db;DB_CLOSE_ON_EXIT=FALSE;AUTO_SERVER=TRUE")
    val zdt            = ZDT
    val todoRepository = TodoRepository(db, zdt)
    val todoService    = TodoService(todoRepository)

    modules(db, zdt, todoRepository, todoService)
}

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)
