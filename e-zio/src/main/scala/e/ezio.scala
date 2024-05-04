package e

import e.scala.*
import zio.{ZIO, Task, RIO, IO}

object ezio:
    /** Type alias for a ZIO that fails with E
      *
      * @tparam A
      *   Type of successful value
      * @see
      *   zio.ZIO
      */
    type EIO[+A] = ZIO[Any, E, A]

    /** @see
      *   [[e.ezio.EIO]]
      */
    val EIO: ZIO.type = ZIO

    /** Type alias for a ZIO that has and environment and fails with E
      *
      * @tparam R
      *   Type of environment
      * @tparam A
      *   Type of successful value
      * @see
      *   zio.ZIO
      */
    type REIO[-R, +A] = ZIO[R, E, A]

    /** @see
      *   [[e.ezio.REIO]]
      */
    val REIO: ZIO.type = ZIO

    extension (e: E)
        /** Converts this E to a failed ZIO
          *
          * @tparam A
          *   Type of successful value
          * @return
          *   A ZIO failed with this E
          */
        inline def toEIO[A]: EIO[A] = EIO.fail(e)

        /** Converts this E to a failed ZIO, in an environment
          *
          * @tparam R
          *   Type of environment
          * @tparam A
          *   Type of successful value
          * @return
          *   A ZIO failed with this E
          */
        inline def toREIO[R, A]: REIO[R, A] = REIO.fail(e)

    extension [A](a: A)
        /** Converts this value to a successful ZIO
          *
          * @return
          *   A ZIO succeeding with this value
          */
        inline def toEIO: EIO[A] = EIO.succeed(a)

        /** Converts this value to a successful ZIO, in an environment
          *
          * @tparam R
          *   Type of environment
          * @return
          *   A ZIO succeeding with this value
          */
        inline def toREIO[R]: REIO[R, A] = REIO.succeed(a)

    extension [A](eor: EOr[A])
        /** Converts this EOr to a ZIO
          *
          * @return
          *   A ZIO that fails with E in this EOr or succeeds with value in this EOr
          */
        inline def toEIO: EIO[A] = eor.fold(e => EIO.fail(e), a => EIO.succeed(a))

        /** Converts this EOr to a ZIO, in an environment
          *
          * @tparam R
          *   Type of environment
          * @return
          *   A ZIO that fails with E in this EOr or succeeds with value in this EOr
          */
        inline def toREIO[R]: REIO[R, A] = eor.fold(e => REIO.fail(e), a => REIO.succeed(a))

    extension [EE, A](io: IO[EE, A])
        /** Converts this IO such that error in it is an E built by given function
          *
          * @param buildE
          *   A mapping function to build an E from error of this IO
          * @return
          *   A ZIO that fails with an E
          */
        inline def toEIO(buildE: EE => E): EIO[A] = io.mapError(buildE)

        /** Converts this IO such that error in it is an E built by given function, in an environment
          *
          * @tparam R
          *   Type of environment
          * @param buildE
          *   A mapping function to build an E from error of this IO
          * @return
          *   A ZIO that fails with an E
          */
        inline def toREIO[R](buildE: EE => E): REIO[R, A] = io.mapError(buildE)
