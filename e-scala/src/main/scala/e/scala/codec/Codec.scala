package e.scala.codec

import e.scala.EOr

import scala.annotation.implicitNotFound

/**
 * Typeclass defining decoding and encoding together via [[e.scala.codec.Decoder]] and [[e.scala.codec.Encoder]]
 *
 * @tparam S Type of source
 * @tparam T Type of target
 */
@implicitNotFound(
  "No implicit instance is found of type e.scala.codec.Codec[${S}, ${T}]. You may try following:\n\n" +
  "* Make sure an instance of correct types is in scope (missing import?)\n" +
  "* Implement an implicit instance yourself\n" +
  "* Ensure both e.scala.codec.Decoder[${T}, ${S}] and e.scala.codec.Encoder[${S}, ${T}] are in scope and use `Codec.of[${S}, ${T}]`"
)
trait Codec[S, T] extends Decoder[T, S] with Encoder[S, T]

object Codec {
  /**
   * Summons an implicit codec
   *
   * @param codec Implicit instance of codec
   *
   * @tparam S Type of source
   * @tparam T Type of target
   *
   * @return Provided implicit codec
   */
  def apply[S, T](implicit codec: Codec[S, T]): Codec[S, T] = codec

  /**
   * Creates a codec based on an implicit decoder and encoder
   *
   * @param decoder Implicit instance of decoder
   * @param encoder Implicit instance of encoder
   *
   * @tparam S Type of source
   * @tparam T Type of target
   *
   * @return Created codec
   */
  def of[S, T](implicit decoder: Decoder[T, S], encoder: Encoder[S, T]): Codec[S, T] =
    new Codec[S, T] {
      override def decode(input: T): EOr[S] = decoder.decode(input)
      override def encode(input: S): T      = encoder.encode(input)
    }
}
