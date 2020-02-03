package dev.akif.e.zio

import dev.akif.e.scala.E

object implicits {
  implicit class MaybeZSyntaxE(private val e: E) {
    def maybeZR[R, A]: MaybeZR[R, A] = MaybeZR.error(e)
    def maybeZ[A]: MaybeZ[A]         = MaybeZ.error(e)
  }

  implicit class MaybeZSyntax[+A](private val a: A) {
    def maybeZR[R]: MaybeZR[R, A] = MaybeZR.value(a)
    def maybeZ: MaybeZ[A]         = MaybeZ.value(a)
  }
}
