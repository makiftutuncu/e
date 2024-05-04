package e.scala

import _root_.scala.util.Try

/** Type alias for EOr, allowing a more pleasing syntax as following:
  *
  * {{{
  *   E or String // instead of EOr[String]
  * }}}
  *
  * @tparam A
  *   Type of the value EOr can contain
  * @see
  *   [[e.scala.E]]
  * @see
  *   [[e.scala.EOr]]
  */
infix type or[+_ <: E, +A] = EOr[A]

extension [A](a: => A)
    /** Converts this value to a successful EOr[A]
      *
      * @return
      *   An EOr[A] containing this value
      * @see
      *   [[e.scala.EOr]]
      */
    inline def toEOr: EOr[A] =
        EOr(a)

    /** Constructs an EOr from evaluating this value and by converting caught Exception using given function
      *
      * @param ifFailure
      *   An E conversion function in case evaluating this value throws
      * @return
      *   An EOr containing either evaluated value or an E computed by given function
      */
    inline def catching(ifFailure: Throwable => E = E.fromThrowable): EOr[A] =
        EOr.fromTry(Try(a))(ifFailure)

extension [A](option: Option[A])
    /** Constructs an EOr from this [[_root_.scala.Option]]
      *
      * @param ifNone
      *   An E to use in case this Option is None
      * @return
      *   An EOr containing either value in this Option or given E
      */
    inline def toEOr(ifNone: => E): EOr[A] =
        EOr.fromOption(option)(ifNone)

extension [L, R](either: Either[L, R])
    /** Constructs an EOr from this [[_root_.scala.util.Either]]
      *
      * @param ifLeft
      *   An E conversion function in case this Either is Left
      * @return
      *   An EOr containing either Right value in this Either or an E computed by given function
      */
    inline def toEOr(ifLeft: L => E): EOr[R] =
        EOr.fromEither(either)(ifLeft)

extension [A](t: Try[A])
    /** Constructs an EOr from this [[_root_.scala.util.Try]]
      *
      * @param ifFailure
      *   An E conversion function in case this Try is Failure
      * @return
      *   An EOr containing either value in this Try or an E computed by given function
      */
    inline def toEOr(ifFailure: Throwable => E = E.fromThrowable): EOr[A] =
        EOr.fromTry(t)(ifFailure)

extension (throwable: Throwable)
    /** Constructs an E from this [[_root_.scala.Throwable]]
      *
      * @param f
      *   A mapping function
      * @return
      *   A new E containing message of this Throwable
      */
    inline def toE(f: Throwable => E = E.fromThrowable): E =
        f(throwable)
