package dev.akif.e

import scala.annotation.implicitNotFound

@implicitNotFound("Cannot decode value of type '${A}' as 'dev.akif.e.E'. Try defining an implicit instance of 'DecoderE[${A}]'.")
trait DecoderE[-A] {
  def decode(a: A): Either[DecodingFailure, E]
}

object DecoderE {
  def apply[A](implicit decoderE: DecoderE[A]): DecoderE[A] = decoderE

  def decode[A](a: A)(implicit decoderE: DecoderE[A]): Either[DecodingFailure, E] = decoderE.decode(a)
}
