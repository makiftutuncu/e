package dev.akif.e.scala

import dev.akif.e.AbstractDecoder

import scala.util.{Failure, Success, Try}

object implicits {
  implicit class DecoderExtensions[A](private val decoder: AbstractDecoder[A, E]) {
    def decodeEither(a: A): Either[E, E] = {
      val result = decoder.decode(a)
      val e      = result.get()

      Either.cond(result.isSuccess, e, e)
    }
  }

  implicit class MaybeExtensions[A](private val maybe: Maybe[A]) {
    val isFailure: Boolean = maybe.isLeft
    val isSuccess: Boolean = maybe.isRight
  }

  implicit class MaybeSyntaxE(private val e: E) {
    def maybe[A]: Maybe[A] = Left(e)
  }

  implicit class MaybeSyntax[+A](private val a: A) {
    def maybe: Maybe[A] = Right(a)
  }

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
