package dev.akif.ehttp4sexample.common

import e.scala.E

object Errors {
    val notFound: E = E(name = Some("not-found"), code = Some(404))
    val database: E = E(name = Some("database"), code = Some(500))
    val unexpected: E = E(name = Some("unexpected"), code = Some(500))
}
