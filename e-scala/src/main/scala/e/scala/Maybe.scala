package e.scala

import e.scala.implicits._
import scala.util.{Try, Failure => TryFailure, Success => TrySuccess}

sealed abstract class Maybe[+A](private val either: Either[E, A]) { self =>
  val isSuccess: Boolean = either.isRight

  val eOpt: Option[E] = either.left.toOption

  val valueOpt: Option[A] = either.toOption

  def map[B](f: A => B): Maybe[B] =
    self match {
      case Maybe.Failure(e)     => e.toMaybe
      case Maybe.Success(value) => f(value).toMaybe
    }

  def flatMap[B](f: A => Maybe[B]): Maybe[B] =
    self match {
      case Maybe.Failure(e)     => e.toMaybe
      case Maybe.Success(value) => f(value)
    }

  def fold[B](ifFailure: E => B, ifSuccess: A => B): B =
    either match {
      case Left(e)      => ifFailure(e)
      case Right(value) => ifSuccess(value)
    }

  def getOrElse[AA >: A](default: => AA): AA =
    either match {
      case Left(_)      => default
      case Right(value) => value
    }

  def orElse[AA >: A](alternative: => Maybe[AA]): Maybe[AA] = if (!isSuccess) alternative else self

  def andThen[B](next: => Maybe[B]): Maybe[B] =
    self match {
      case Maybe.Failure(e) => e.toMaybe
      case Maybe.Success(_) => next
    }

  def foreach[U](f: A => U): Unit =
    self match {
      case Maybe.Success(value) => f(value)
    }

  def filter(predicate: A => Boolean, ifPredicateFails: A => E): Maybe[A] =
    self match {
      case Maybe.Failure(e)                          => e.toMaybe
      case Maybe.Success(value) if !predicate(value) => ifPredicateFails(value).toMaybe
      case Maybe.Success(value) if predicate(value)  => value.toMaybe
    }

  def filter(predicate: A => Boolean): Maybe[A] =
    self match {
      case Maybe.Failure(e)                          => e.toMaybe
      case Maybe.Success(value) if !predicate(value) => E("predicate-failed", "Value did not satisfy predicate!").data("value" -> value).toMaybe
      case Maybe.Success(value) if predicate(value)  => value.toMaybe
    }

  def handleErrorWith[AA >: A](f: E => Maybe[AA]): Maybe[AA] =
    self match {
      case Maybe.Failure(e)     => f(e)
      case Maybe.Success(value) => value.toMaybe
    }

  def handleError[AA >: A](f: E => AA): Maybe[AA] =
    self match {
      case Maybe.Failure(e)     => f(e).toMaybe
      case Maybe.Success(value) => value.toMaybe
    }

  override def equals(other: Any): Boolean =
    other match {
      case that: Maybe[_] => this.either == that.either
      case _              => false
    }

  override def hashCode(): Int = either.hashCode()

  override def toString: String = either.fold(_.toString, _.toString)
}

object Maybe {
  final case class Failure(e: E) extends Maybe[Nothing](Left(e))

  final case class Success[+A](value: A) extends Maybe[A](Right(value))

  def failure[A](e: E): Maybe[A] = Failure(e)

  def success[A](value: A): Maybe[A] = Success(value)

  def unit: Maybe[Unit] = Success(())

  def fromOption[A](option: Option[A], ifEmpty: => E): Maybe[A] =
    option match {
      case None    => Failure(ifEmpty)
      case Some(a) => Success(a)
    }

  def fromEither[L, R](either: Either[L, R], ifFailure: L => E): Maybe[R] =
    either match {
      case Left(l)  => Failure(ifFailure(l))
      case Right(r) => Success(r)
    }

  def fromTry[A](t: Try[A], ifFailure: Throwable => E): Maybe[A] =
    t match {
      case TryFailure(t) => Failure(ifFailure(t))
      case TrySuccess(a) => Success(a)
    }

  def catching[A](ifFailure: Throwable => E)(f: => A): Maybe[A] =
    Try(f) match {
      case TryFailure(t) => Failure(ifFailure(t))
      case TrySuccess(a) => Success(a)
    }

  def catchingMaybe[A](ifFailure: Throwable => E)(f: => Maybe[A]): Maybe[A] =
    Try(f) match {
      case TryFailure(t)     => Failure(ifFailure(t))
      case TrySuccess(maybe) => maybe
    }
}
