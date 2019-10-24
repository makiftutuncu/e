package dev.akif.e

import scala.util.{Failure, Success, Try}

object Maybe {
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
