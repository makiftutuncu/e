package dev.akif.ektorexample.common

import e.gson.EGsonCodec
import e.kotlin.*
import e.java.E as JavaE
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.features.StatusPages
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.response.respondText
import org.slf4j.Logger
import java.time.ZonedDateTime

fun String?.asId(): EOr<Long> =
    EOr.catching({ (this ?: "").toLong() }) { t ->
        Errors.parseError.message("Invalid id!").cause(t.toE())
    }

suspend fun <A> ApplicationCall.respond(eor: EOr<A>, status: HttpStatusCode = HttpStatusCode.OK) {
    when (eor) {
        is EOr.Companion.Failure -> {
            val e           = eor.error
            val json        = EGsonCodec.get().encode(e.toJavaE()).toString()
            val errorStatus = e.code?.let { HttpStatusCode.fromValue(it) } ?: HttpStatusCode.InternalServerError

            respondText(json, ContentType.Application.Json, errorStatus)
        }

        is EOr.Companion.Success -> respond(status, eor.value as Any)
    }
}

fun StatusPages.Configuration.registerErrorHandler(logger: Logger) {
    exception<Exception> { cause ->
        val error = Errors.unexpected.cause(cause.toE())
        logger.error("Request failed! $error", cause)
        call.respond(error.toEOr<String>())
    }
}

fun ZonedDateTime.asString(): String = this.format(ZDT.formatter)

fun String.asZDT(): ZonedDateTime = ZonedDateTime.parse(this, ZDT.formatter)

private fun E.toJavaE(): JavaE {
    if (causes.isEmpty()) {
        return JavaE(code, name, message, emptyList(), data, time)
    }

    return JavaE(code, name, message, causes.map { it.toJavaE() }, data, time)
}
