package dev.akif.eplayexample.common

import e.scala._

object Errors {
    val invalidData: E = E.name("invalid-data").code(400)
    val notFound: E = E.name("not-found").code(404)
    val database: E = E.name("database").code(500)
    val unexpected: E = E.name("unexpected").code(500).message("An unexpected error occurred!")
}
