package dev.akif.ehttp4sexample.common

import cats.effect.IO
import e.scala.{E, EOr}

object implicits {
  implicit class EIOExtensions(private val e: E) {
    def toIO[A]: IO[A] = IO.raiseError(e.toException)
  }

  implicit class EOrIOExtensions[A](private val eOr: EOr[A]) {
    def toIO: IO[A] = eOr.fold[IO[A]](e => e.toIO[A], a => IO.pure(a))
  }
}
