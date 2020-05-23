package e

import _root_.zio.{ZIO, Task, RIO, IO}

object zio {
  type EIO[+A]      = ZIO[Any, E, A]
  val EIO: ZIO.type = ZIO

  type REIO[-R, +A]  = ZIO[R, E, A]
  val REIO: ZIO.type = ZIO

  implicit class EExtensions(e: E) {
    def toEIO[A]: EIO[A] = EIO.fail(e)
  }

  implicit class ValueExtensions[A](a: A) {
    def toEIO: EIO[A] = EIO.effectTotal(a)
  }

  implicit class EOrExtensions[A](eor: A or E) {
    def toEIO: EIO[A] = eor.fold(e => EIO.fail[E](e), a => EIO.effectTotal[A](a))
  }

  implicit class TaskExtensions[A](task: Task[A]) {
    def toEIO: EIO[A] = task.mapError(_.toE)
  }

  implicit class RIOExtensions[R, A](rio: RIO[R, A]) {
    def toREIO: REIO[R, A] = rio.mapError(_.toE)
  }

  implicit class IOExtensions[EE, A](io: IO[EE, A]) {
    def toEIO(buildE: EE => E): EIO[A] = io.mapError(buildE)
  }
}
