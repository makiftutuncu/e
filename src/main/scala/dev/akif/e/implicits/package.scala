package dev.akif.e

import scala.util.{Failure, Success, Try}

package object implicits {
  implicit class OptionExtensions[A](private val option: Option[A]) {
    def orE(e: => E): Either[E, A] =
      option match {
        case None    => Left(e)
        case Some(a) => Right(a)
      }
  }

  implicit class EitherExtensions[L, R](private val either: Either[L, R]) {
    def orE(makeE: L => E): Either[E, R] =
      either match {
        case Left(l)  => Left(makeE(l))
        case Right(r) => Right(r)
      }
  }

  implicit class TryExtensions[A](private val `try`: Try[A]) {
    def orE(makeE: Throwable => E): Either[E, A] =
      `try` match {
        case Failure(t) => Left(makeE(t))
        case Success(a) => Right(a)
      }
  }
}
