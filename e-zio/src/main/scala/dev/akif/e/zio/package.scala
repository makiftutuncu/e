package dev.akif.e

import _root_.zio.ZIO
import dev.akif.e.scala.E

package object zio {
  type MaybeZR[-R, +A] = ZIO[R, E, A]
  type MaybeZ[+A]      = ZIO[Any, E, A]
}
