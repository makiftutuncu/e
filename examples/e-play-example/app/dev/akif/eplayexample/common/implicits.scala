package dev.akif.eplayexample.common

import e.scala.Maybe
import e.zio.{MaybeZ, MaybeZR}

object implicits {
  implicit class MaybeZRExtensions[+A](private val maybe: Maybe[A]) {
    def toMaybeZ: MaybeZ[A]         = maybe.fold(MaybeZ.error,  MaybeZ.value)
    def toMaybeZR[R]: MaybeZR[R, A] = maybe.fold(MaybeZR.error, MaybeZR.value)
  }
}
