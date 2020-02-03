package e.zio

import _root_.zio.ZIO
import e.scala.E

object MaybeZ {
  def error[A](e: E): MaybeZ[A] = ZIO.fail(e)
  def value[A](a: A): MaybeZ[A] = ZIO.succeed(a)
}
