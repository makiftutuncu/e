package dev.akif.e

import scala.annotation.implicitNotFound

@implicitNotFound("Cannot encode 'dev.akif.e.E' as '${A}'. Try defining an implicit instance of 'EncoderE[${A}]'.")
trait EncoderE[+A] {
  def encode(e: E): A
}

object EncoderE {
  def apply[A](implicit encoderE: EncoderE[A]): EncoderE[A] = encoderE

  def encode[A](e: E)(implicit encoderE: EncoderE[A]): A = encoderE.encode(e)
}
