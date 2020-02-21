package dev.akif.eplayexample.common

import e.scala.E

object Errors {
  final case class EAsException(e: E) extends Exception(e.toString(), e.cause.orNull)

  val invalidData: E = E("invalid-data", code = 400)
  val notFound: E    = E("not-found",    code = 404)
  val database: E    = E("database",     code = 500)
  val unexpected: E  = E("unexpected",   code = 500).message("An unexpected error occurred!")
}
