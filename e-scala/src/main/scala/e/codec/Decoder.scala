package e.codec

import e.{E, or}

import scala.annotation.implicitNotFound

/**
 * Typeclass defining how to decode an input value to an output value, possibly failing with E
 *
 * @tparam I Type of input
 * @tparam O Type of output
 *
 * @see [[e.E]]
 * @see [[e.EOr]]
 */
@implicitNotFound(
  "No implicit instance is found of type e.codec.Decoder[${I}, ${O}]. You may try following:\n\n" +
  "* Make sure an instance of correct types is in scope (missing import?)\n" +
  "* Implement an implicit instance yourself"
)
trait Decoder[-I, +O] {
  /**
   * Decodes an input, possibly failing with E
   *
   * @param input An input
   *
   * @return Decoded output or E
   *
   * @see [[e.EOr]]
   */
  def decode(input: I): O or E
}

object Decoder {
  /**
   * A default E to be used when decoding fails
   */
  val decodingError: E = E.name("decoding-error").message("Failed to decode!")

  /**
   * Summons an implicit decoder
   *
   * @param decoder Implicit instance of decoder
   *
   * @tparam I Type of input
   * @tparam O Type of output
   *
   * @return Provided implicit decoder
   */
  def apply[I, O](implicit decoder: Decoder[I, O]): Decoder[I, O] = decoder
}
