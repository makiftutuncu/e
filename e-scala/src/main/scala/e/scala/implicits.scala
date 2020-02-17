package e.scala

import e.AbstractDecoder

import scala.language.implicitConversions
import scala.util.Try

object implicits {
  implicit class DecoderExtensions[A](private val decoder: AbstractDecoder[A, E]) {
    def decodeEither(a: A): Either[E, E] = {
      val result = decoder.decode(a)
      val e      = result.get()

      Either.cond(result.isSuccess, e, e)
    }
  }

  implicit def eitherToMaybe[A](either: Either[E, A]): Maybe[A] = Maybe.fromEither[E, A](either, identity)

  implicit class MaybeSyntax[A](private val a: A) {
    @inline def toMaybe: Maybe[A] = Maybe.success(a)
  }

  implicit class OptionExtensions[A](private val option: Option[A]) {
    @inline def toMaybe(e: => E): Maybe[A] = Maybe.fromOption(option, e)
  }

  implicit class EitherExtensions[L, R](private val either: Either[L, R]) {
    @inline def toMaybe(makeE: L => E): Maybe[R] = Maybe.fromEither(either, makeE)
  }

  implicit class TryExtensions[A](private val t: Try[A]) {
    @inline def toMaybe(makeE: Throwable => E): Maybe[A] = Maybe.fromTry(t, makeE)
  }
}
