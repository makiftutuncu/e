package e

import scala.reflect.ClassTag
import scala.util.{Try, Failure => TryFailure, Success => TrySuccess}

/**
 * A container that can either contain an E or a value, semantically same as Either but specialized for E
 *
 * @tparam A Type of the value this EOr can contain
 *
 * @see [[e.E]]
 * @see [[scala.util.Either]]
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
  def map[B](f: A => B): B or E =
    self match {
      case Failure(e)     => e.as[B]
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
  def flatMap[B](f: A => B or E): B or E =
    self match {
      case Failure(e)     => e.as[B]
      case Success(value) => f(value)
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
  def orElse[AA >: A](alternative: => AA or E): AA or E =
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
  def andThen[B](next: => B or E): B or E =
    self match {
      case Failure(e) => e.as[B]
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
             filteredError: A => E = { a => EOr.filteredError.data("value" -> a) }): A or E =
    self match {
      case Failure(_)     => self
      case Success(value) => if (condition(value)) value.orE else filteredError(value).as[A]
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
  def withFilter(condition: A => Boolean): A or E = filter(condition)

  /**
   * Handles E in this EOr, if it exists, by given partial function to compute a successful EOr
   *
   * @param f Partial function to handle E
   *
   * @tparam AA Type of value returned by partial function
   *
   * @return A new EOr containing handled value if partial function was defined for E in this EOr or this EOr as is
   */
  def handle[AA >: A](f: PartialFunction[E, AA]): AA or E =
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
  def handleWith[AA >: A](f: PartialFunction[E, AA or E]): AA or E =
    self match {
      case Failure(e) if f.isDefinedAt(e) => f(e)
      case _                              => self
    }

  override def equals(other: Any): Boolean =
    other match {
      case that: EOr[_] => this.error == that.error && this.value == that.value
      case _            => false
    }

  override def hashCode(): Int =
    self match {
      case Failure(e) => e.hashCode
      case Success(a) => a.hashCode
    }

  override def toString: String =
    self match {
      case Failure(e) => e.toString
      case Success(a) => a.toString
    }
}

object EOr {
  private final case class Failure(e: E) extends (Nothing or E)

  private final case class Success[+A](a: A) extends (A or E)

  /**
   * A successful EOr of type Unit
   */
  val unit: Unit or E = Success(())

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
  def apply[A](e: E): A or E = Failure(e)

  /**
   * Constructs a successful EOr containing given value
   *
   * @param a A value
   *
   * @tparam A Type of value of resulting EOr
   *
   * @return A new failed EOr containing given value
   */
  def apply[A](a: A): A or E = Success(a)

  /**
   * Deconstructs given EOr for pattern matching against its E
   *
   * @param eor An EOr
   *
   * @return Some E or None if given EOr has value
   */
  def unapply(eor: Nothing or E): Option[E] = eor.error

  /**
   * Deconstructs given EOr for pattern matching against its value
   *
   * @param eor An EOr
   *
   * @return Some value or None if given EOr has E
   */
  def unapply[A: ClassTag](eor: A or E): Option[A] = eor.value

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
  def fromOption[A](option: Option[A], ifNone: => E): A or E =
    option match {
      case None    => Failure(ifNone)
      case Some(a) => Success(a)
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
  def fromEither[L, R](either: Either[L, R], ifLeft: L => E): R or E =
    either match {
      case Left(l)  => Failure(ifLeft(l))
      case Right(r) => Success(r)
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
  def fromTry[A](`try`: Try[A], ifFailure: Throwable => E): A or E =
    `try` match {
      case TryFailure(EException(e)) => Failure(e)
      case TryFailure(t)             => Failure(ifFailure(t))
      case TrySuccess(a)             => Success(a)
    }
}
