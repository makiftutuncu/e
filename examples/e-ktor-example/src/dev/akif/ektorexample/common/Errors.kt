package dev.akif.ektorexample.common

import e.kotlin.E
import io.ktor.http.HttpStatusCode

object Errors {
    val parseError: E = E(name = "parse-error", message = "Parse error!", code = HttpStatusCode.BadRequest.value)

    val notFound: E = E(name = "not-found", message = "Requested resource is not found!", code = HttpStatusCode.NotFound.value)

    val database: E   = E(name = "database-error",   message = "Database operation failed!",    code = HttpStatusCode.InternalServerError.value)
    val unexpected: E = E(name = "unexpected-error", message = "An unexpected error occurred!", code = HttpStatusCode.InternalServerError.value)
}
