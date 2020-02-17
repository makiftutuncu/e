package dev.akif.ektorexample.common

import e.kotlin.E
import io.ktor.http.HttpStatusCode

object Errors {
    val parseError: E = E("parse-error", "Parse error!", HttpStatusCode.BadRequest.value)

    val notFound: E = E("not-found", "Requested resource is not found!", HttpStatusCode.NotFound.value)

    val database: E   = E("database-error",   "Database operation failed!",    HttpStatusCode.InternalServerError.value)
    val unexpected: E = E("unexpected-error", "An unexpected error occurred!", HttpStatusCode.InternalServerError.value)
}
