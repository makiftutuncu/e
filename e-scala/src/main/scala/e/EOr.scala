package e

import scala.util.{Try, Failure => TryFailure, Success => TrySuccess}

/**
 * A container that can either be a Failure containing an E or Success containing a value,
 * semantically a combination of Either and Try but specialized for E
 *
 * @tparam A Type of the value this EOr can contain
 *
 * @see [[e.E]]
 * @see [[scala.util.Either]]
 * @see [[scala.util.Try]]
 */
sealed trait EOr[+A] { self =>
  import EOr.{Failure, Success}

  /**
   * Whether or not this contains an E
   */
  val isFailure: Boolean = self.isInstanceOf[Failure]

  /**
   * Whether or not this contains a value
   */
  val isSuccess: Boolean = self.isInstanceOf[Success[A]]

  /**
   * E in this as an Option
   */
  val error: Option[E] =
    self match {
      case Failure(e) => Some(e)
      case _          => None
    }

  /**
   * Value in this as an Option
   */
  val value: Option[A] =
    self match {
      case Success(a) => Some(a)
      case _          => None
    }

  /**
   * Converts value in this, if it exists, using given mapping function to make a new EOr
   *
   * @param f Mapping function
   *
   * @tparam B Type of the new value
   *
   * @return A new EOr containing either the new value or E in this one
   */
  def map[B](f: A => B): EOr[B] =
    self match {
      case Failure(e)     => e.toEOr[B]
      case Success(value) => f(value).orE
    }

  /**
   * Computes a new EOr using value in this, if it exists, with given flat mapping function
   *
   * @param f Flat mapping function
   *
   * @tparam B Type of the new value
   *
   * @return Computed EOr or a new EOr containing E in this one
   */
  def flatMap[B](f: A => EOr[B]): EOr[B] =
    self match {
      case Failure(e)     => e.toEOr[B]
      case Success(value) => f(value)
    }

  /**
   * Converts E in this, if it exists, using given mapping function to make a new EOr
   *
   * @param f E mapping function
   *
   * @return This EOr or a new EOr containing computed E if this one has E
   */
  def mapError(f: E => E): EOr[A] =
    self match {
      case Failure(e) => f(e).toEOr[A]
      case Success(_) => self
    }

  /**
   * Computes a new EOr using E in this, if it exists, with given flat mapping function
   *
   * @param f E flat mapping function
   *
   * @tparam AA Type of the new value
   *
   * @return This EOr or a computed EOr if this one has E
   */
  def flatMapError[AA >: A](f: E => EOr[AA]): EOr[AA] =
    self match {
      case Failure(e) => f(e)
      case Success(_) => self
    }

  /**
   * Folds this into a single value, handling both E and value conversions with given functions
   *
   * @param ifFailure Conversion function for E
   * @param ifSuccess Conversion function for value
   *
   * @tparam B Type of the desired result
   *
   * @return Converted result
   */
  def fold[B](ifFailure: E => B, ifSuccess: A => B): B =
    self match {
      case Failure(e)     => ifFailure(e)
      case Success(value) => ifSuccess(value)
    }

  /**
   * Gets the value in this or falls back to given default value
   *
   * @param default Default value to use in case this has E
   *
   * @tparam AA Type of default value
   *
   * @return Value in this or given default value
   */
  def getOrElse[AA >: A](default: => AA): AA =
    self match {
      case Failure(_)     => default
      case Success(value) => value
    }

  /**
   * Provides an alternative EOr if this one has E, ignoring the E
   *
   * @param alternative Alternative EOr in case this one has E
   *
   * @tparam AA Type of value in alternative EOr
   *
   * @return This EOr or alternative if this one has E
   */
  def orElse[AA >: A](alternative: => EOr[AA]): EOr[AA] =
    self match {
      case Failure(_) => alternative
      case Success(_) => self
    }

  /**
   * Provides a next EOr if this one has a value, ignoring the value
   *
   * @param next Next EOr in case this one has a value
   *
   * @tparam B Type of value in next EOr
   *
   * @return Next EOr or a new EOr containing E in this one
   */
  def andThen[B](next: => EOr[B]): EOr[B] =
    self match {
      case Failure(e) => e.toEOr[B]
      case Success(_) => next
    }

  /**
   * Performs a side-effect using value in this, if it exists
   *
   * @param f Side-effecting function
   *
   * @tparam U Type of result of the side-effect
   */
  def foreach[U](f: A => U): Unit =
    self match {
      case Failure(_)     => ()
      case Success(value) => f(value)
    }

  /**
   * Filters this EOr by value in it, if it exists, using given function
   *
   * @param condition     Filtering function
   * @param filteredError E conversion function
   *
   * @return This EOr of a new EOr containing an E computed by given conversion function
   */
  def filter(condition: A => Boolean,
             filteredError: A => E = { a => EOr.filteredError.data("value", a) }): EOr[A] =
    self match {
      case Failure(_)     => self
      case Success(value) => if (condition(value)) value.orE else filteredError(value).toEOr[A]
    }

  /**
   * Filters this EOr by value in it, if it exists, using given function
   *
   * @param condition Filtering function
   *
   * @return This EOr of a new EOr containing filtered error
   *
   * @see [[e.EOr#filteredError]]
   */
  def withFilter(condition: A => Boolean): EOr[A] = filter(condition)

  /**
   * Handles E in this EOr, if it exists, by given partial function to compute a successful EOr
   *
   * @param f Partial function to handle E
   *
   * @tparam AA Type of value returned by partial function
   *
   * @return A new EOr containing handled value if partial function was defined for E in this EOr or this EOr as is
   */
  def handle[AA >: A](f: PartialFunction[E, AA]): EOr[AA] =
    self match {
      case Failure(e) if f.isDefinedAt(e) => f(e).orE
      case _                              => self
    }

  /**
   * Handles E in this EOr, if it exists, by given partial function to compute a new EOr
   *
   * @param f Partial function to compute new EOr
   *
   * @tparam AA Type of value in EOr returned by partial function
   *
   * @return Computed new EOr if partial function was defined for E in this EOr or this EOr as is
   */
  def handleWith[AA >: A](f: PartialFunction[E, EOr[AA]]): EOr[AA] =
    self match {
      case Failure(e) if f.isDefinedAt(e) => f(e)
      case _                              => self
    }
}

object EOr {
  /**
   * A failed EOr
   *
   * @param e An error
   */
  final case class Failure(e: E) extends EOr[Nothing] {
    override def toString: String = e.toString
  }

  /**
   * A successful EOr
   *
   * @param a A value
   *
   * @tparam A Type of the value this EOr can contain
   */
  final case class Success[+A](a: A) extends EOr[A] {
    override def toString: String = a.toString
  }

  /**
   * A successful EOr of type Unit
   */
  val unit: EOr[Unit] = Success(())

  /**
   * A default E to be used when condition does not hold while filtering an EOr
   *
   * @see [[e.EOr#withFilter]]
   */
  val filteredError: E = E(name = Some("filtered"), message = Some("Condition does not hold!"))

  /**
   * Constructs a failed EOr containing given E
   *
   * @param e An E
   *
   * @tparam A Type of value of resulting EOr
   *
   * @return A new failed EOr containing given E
   */
  def apply[A](e: E): EOr[A] = Failure(e)

  /**
   * Constructs a successful EOr containing given value
   *
   * @param a A value
   *
   * @tparam A Type of value of resulting EOr
   *
   * @return A new failed EOr containing given value
   */
  def apply[A](a: A): EOr[A] = Success(a)

  /**
   * Constructs an EOr from an [[scala.Option]]
   *
   * @param option An Option
   * @param ifNone An error to use in case Option is None
   *
   * @tparam A Type of value of Option
   *
   * @return An EOr containing either value in Option or given E
   */
  def fromOption[A](option: Option[A])(ifNone: => E): EOr[A] =
    option match {
      case None    => apply(ifNone)
      case Some(a) => apply(a)
    }

  /**
   * Constructs an EOr from an [[scala.util.Either]]
   *
   * @param either An Either
   * @param ifLeft An E conversion function in case Either is Left
   *
   * @tparam L Type of Left value of Either
   * @tparam R Type of Right value of Either
   *
   * @return An EOr containing either Right value in Either or an E computed by given function
   */
  def fromEither[L, R](either: Either[L, R])(ifLeft: L => E): EOr[R] =
    either match {
      case Left(l)  => apply(ifLeft(l))
      case Right(r) => apply(r)
    }

  /**
   * Constructs an EOr from a [[scala.util.Try]]
   *
   * @param try       A Try
   * @param ifFailure An E conversion function in case Try is Failure
   *
   * @tparam A Type of value of Try
   *
   * @return An EOr containing either value in Try or an E computed by given function
   */
  def fromTry[A](`try`: Try[A])(ifFailure: Throwable => E): EOr[A] =
    `try` match {
      case TryFailure(EException(e)) => apply(e)
      case TryFailure(t)             => apply(ifFailure(t))
      case TrySuccess(a)             => apply(a)
    }
}
