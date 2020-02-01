package dev.akif.e.scala

import scala.util.{Failure, Success, Try}

object implicits {
  implicit class OptionExtensions[A](private val option: Option[A]) {
    def orE(e: => E): Maybe[A] =
      option match {
        case None    => Left(e)
        case Some(a) => Right(a)
      }
  }

  implicit class EitherExtensions[L, R](private val either: Either[L, R]) {
    def orE(makeE: L => E): Maybe[R] =
      either match {
        case Left(l)  => Left(makeE(l))
        case Right(r) => Right(r)
      }
  }

  implicit class TryExtensions[A](private val t: Try[A]) {
    def orE(makeE: Throwable => E): Maybe[A] =
      t match {
        case Failure(t) => Left(makeE(t))
        case Success(a) => Right(a)
      }
  }
}
