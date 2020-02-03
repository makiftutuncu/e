package e.zio

import _root_.zio.ZIO
import e.scala.E

object MaybeZR {
  def error[R, A](e: E): MaybeZR[R, A] = ZIO.fail(e)
  def value[R, A](a: A): MaybeZR[R, A] = ZIO.succeed(a)
}
