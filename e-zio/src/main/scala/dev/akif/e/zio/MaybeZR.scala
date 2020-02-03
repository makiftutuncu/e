package dev.akif.e.zio

import dev.akif.e.scala.E
import zio.ZIO

object MaybeZR {
  def error[R, A](e: E): MaybeZR[R, A] = ZIO.fail(e)
  def value[R, A](a: A): MaybeZR[R, A] = ZIO.succeed(a)
}
