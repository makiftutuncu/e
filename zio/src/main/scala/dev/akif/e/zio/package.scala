package dev.akif.e

import _root_.zio.ZIO

package object zio {
  type MaybeZR[-R, +A] = ZIO[R, E, A]
  type MaybeZ[+A]      = ZIO[Any, E, A]

  object MaybeZR {
    def error[R, A](e: E): MaybeZR[R, A] = ZIO.fail(e)
    def value[R, A](a: A): MaybeZR[R, A] = ZIO.succeed(a)
  }

  object MaybeZ {
    def error[A](e: E): MaybeZ[A] = ZIO.fail(e)
    def value[A](a: A): MaybeZ[A] = ZIO.succeed(a)
  }

  object syntax {
    implicit class MaybeZRSyntaxE(private val e: E) {
      def maybeZR[R, A]: MaybeZR[R, A] = MaybeZR.error(e)
    }

    implicit class MaybeZSyntaxE(private val e: E) {
      def maybeZ[A]: MaybeZ[A] = MaybeZ.error(e)
    }

    implicit class MaybeZSyntax[+A](private val a: A) {
      def maybeZR[R]: MaybeZR[R, A] = MaybeZR.value(a)
      def maybeZ: MaybeZ[A]         = MaybeZ.value(a)
    }
  }
}
