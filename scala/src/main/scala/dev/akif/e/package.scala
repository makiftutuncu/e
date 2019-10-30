package dev.akif

import scala.util.{Failure, Success, Try}
import scala.util.control.NonFatal

package object e {
  type Maybe[+A] = Either[E, A]

  implicit class DecoderEExtensions[A](private val decoderE: DecoderE[A]) {
    def decode(a: A): Either[DecodingFailure, E] =
      Try(decoderE.decodeOrThrow(a)) match {
        case Failure(df: DecodingFailure) => Left(df)
        case Failure(NonFatal(t))         => Left(new DecodingFailure(s"Cannot decode $a as E!", t))
        case Success(e)                   => Right(e)
      }
  }

  implicit class MaybeExtensions[A](private val maybe: Maybe[A]) {
    val isError: Boolean  = maybe.isLeft
    val hasError: Boolean = isError

    val isValue: Boolean = maybe.isRight
  }

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

    implicit class TryExtensions[A](private val `try`: Try[A]) {
      def orE(makeE: Throwable => E): Maybe[A] =
        `try` match {
          case Failure(t) => Left(makeE(t))
          case Success(a) => Right(a)
        }
    }
  }

  object syntax {
    implicit class MaybeSyntaxE(private val e: E) {
      def maybe[A]: Maybe[A] = Left(e)
    }

    implicit class MaybeSyntax[+A](private val a: A) {
      def maybe: Maybe[A] = Right(a)
    }
  }
}
