package dev.akif.e.zio

import dev.akif.e.scala.E
import zio.ZIO

object MaybeZ {
  def error[A](e: E): MaybeZ[A] = ZIO.fail(e)
  def value[A](a: A): MaybeZ[A] = ZIO.succeed(a)
}
