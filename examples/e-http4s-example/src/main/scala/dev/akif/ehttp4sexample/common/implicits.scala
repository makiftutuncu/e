package dev.akif.ehttp4sexample.common

import cats.effect.IO
import dev.akif.ehttp4sexample.common.Errors.EAsException
import e.scala.{E, Maybe}

object implicits {
  implicit class EIOExtensions(private val e: E) {
    def toIO[A]: IO[A] = IO.raiseError(EAsException(e))
  }

  implicit class MaybeIOExtensions[A](private val maybe: Maybe[A]) {
    def toIO: IO[A] = maybe.fold[IO[A]](e => e.toIO[A], a => IO.pure(a))
  }
}
