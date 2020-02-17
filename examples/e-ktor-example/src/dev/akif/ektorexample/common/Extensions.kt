package dev.akif.ektorexample.common

import e.kotlin.Maybe
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.features.StatusPages
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.response.respondText
import org.slf4j.Logger
import java.time.ZonedDateTime

fun String?.asId(): Maybe<Long> =
    Maybe.catching({ (this ?: "").toLong() }) { t ->
        Errors.parseError.message("Invalid id!").cause(t)
    }

suspend fun <T> ApplicationCall.respondMaybe(maybe: Maybe<T>, status: HttpStatusCode = HttpStatusCode.OK) {
    val e     = maybe.e
    val value = maybe.value

    when {
        e != null     -> respondText(e.toString(), ContentType.Application.Json, HttpStatusCode.fromValue(e.code()))
        value != null -> respond(status, value as Any)
    }
}

fun StatusPages.Configuration.registerErrorHandler(logger: Logger) {
    exception<Exception> { cause ->
        val error = Errors.unexpected.cause(cause)
        logger.error("Request failed! $error", cause)
        call.respondMaybe(error.toMaybe<String>())
    }
}

fun ZonedDateTime.asString(): String = this.format(ZDT.formatter)

fun String.asZDT(): ZonedDateTime = ZonedDateTime.parse(this, ZDT.formatter)
