package e

import e.scala._
import zio.{ZIO, Task, RIO, IO}

object ezio {
  /**
   * Type alias for a ZIO that fails with E
   *
   * @tparam A Type of successful value
   *
   * @see zio.ZIO
   */
  type EIO[+A] = ZIO[Any, E, A]

  /**
   * @see [[e.ezio.EIO]]
   */
  val EIO: ZIO.type = ZIO

  /**
   * Type alias for a ZIO that has and environment and fails with E
   *
   * @tparam R Type of environment
   * @tparam A Type of successful value
   *
   * @see zio.ZIO
   */
  type REIO[-R, +A]  = ZIO[R, E, A]

  /**
   * @see [[e.ezio.REIO]]
   */
  val REIO: ZIO.type = ZIO

  implicit class EExtensionsForEIO(e: E) {
    /**
     * Converts this E to a failed ZIO
     *
     * @tparam A Type of successful value
     *
     * @return A ZIO failed with this E
     */
    def toEIO[A]: EIO[A] = EIO.fail(e)

    /**
     * Converts this E to a failed ZIO, in an environment
     *
     * @tparam R Type of environment
     * @tparam A Type of successful value
     *
     * @return A ZIO failed with this E
     */
    def toREIO[R, A]: REIO[R, A] = REIO.fail(e)
  }

  implicit class ValueExtensionsForEIO[A](a: A) {
    /**
     * Converts this value to a successful ZIO
     *
     * @return A ZIO succeeding with this value
     */
    def toEIO: EIO[A] = EIO.effectTotal(a)

    /**
     * Converts this value to a successful ZIO, in an environment
     *
     * @tparam R Type of environment
     *
     * @return A ZIO succeeding with this value
     */
    def toREIO[R]: REIO[R, A] = REIO.effectTotal(a)
  }

  implicit class EOrExtensionsForEIO[A](eor: EOr[A]) {
    /**
     * Converts this EOr to a ZIO
     *
     * @return A ZIO that fails with E in this EOr or succeeds with value in this EOr
     */
    def toEIO: EIO[A] = eor.fold(e => EIO.fail(e), a => EIO.effectTotal(a))

    /**
     * Converts this EOr to a ZIO, in an environment
     *
     * @tparam R Type of environment
     *
     * @return A ZIO that fails with E in this EOr or succeeds with value in this EOr
     */
    def toREIO[R]: REIO[R, A] = eor.fold(e => REIO.fail(e), a => REIO.effectTotal(a))
  }

  implicit class TaskExtensionsForEIO[A](task: Task[A]) {
    /**
     * Converts this Task such that error in it is an E
     *
     * @param f A mapping function
     *
     * @return A ZIO that fails with an E
     *
     * @see e.E.fromThrowable
     */
    def toEIO(f: Throwable => E = E.fromThrowable): EIO[A] = task.mapError(_.toE(f))

    /**
     * Converts this Task such that error in it is an E, in an environment
     *
     * @tparam R Type of environment
     *
     * @param f A mapping function
     *
     * @return A ZIO that fails with an E
     *
     * @see e.E.fromThrowable
     */
    def toREIO[R](f: Throwable => E = E.fromThrowable): REIO[R, A] = task.mapError(_.toE(f))
  }

  implicit class IOExtensionsForEIO[EE, A](io: IO[EE, A]) {
    /**
     * Converts this IO such that error in it is an E built by given function
     *
     * @param buildE A mapping function to build an E from error of this IO
     *
     * @return A ZIO that fails with an E
     */
    def toEIO(buildE: EE => E): EIO[A] = io.mapError(buildE)

    /**
     * Converts this IO such that error in it is an E built by given function, in an environment
     *
     * @tparam R Type of environment
     *
     * @param buildE A mapping function to build an E from error of this IO
     *
     * @return A ZIO that fails with an E
     */
    def toREIO[R](buildE: EE => E): REIO[R, A] = io.mapError(buildE)
  }

  implicit class RIOExtensionsForREIO[R, A](rio: RIO[R, A]) {
    /**
     * Converts this RIO such that error in it is an E
     *
     * @param f A mapping function
     *
     * @return A ZIO that fails with an E
     *
     * @see e.E.fromThrowable
     */
    def toREIO(f: Throwable => E = E.fromThrowable): REIO[R, A] = rio.mapError(_.toE(f))
  }
}
