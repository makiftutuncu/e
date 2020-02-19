package dev.akif.ehttp4sexample.common

import e.scala.E

object Errors {
  final case class EAsException(e: E) extends Exception(e.toString(), e.cause.orNull)

  val notFound: E   = E("not-found",  code = 404)
  val database: E   = E("database",   code = 500)
  val unexpected: E = E("unexpected", code = 500)
}
