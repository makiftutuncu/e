package e.codec

import scala.annotation.implicitNotFound

/**
 * Typeclass defining how to encode an input value to an output value
 *
 * @tparam I Type of input
 * @tparam O Type of output
 */
@implicitNotFound(
  "No implicit instance is found of type e.codec.Encoder[${I}, ${O}]. You may try following:\n\n" +
  "* Make sure an instance of correct types is in scope (missing import?)\n" +
  "* Implement an implicit instance yourself"
)
trait Encoder[-I, +O] {
  /**
   * Encodes an input
   *
   * @param input An input
   *
   * @return Encoded output
   */
  def encode(input: I): O
}

object Encoder {
  /**
   * Summons an implicit encoder
   *
   * @param encoder Implicit instance of encoder
   *
   * @tparam I Type of input
   * @tparam O Type of output
   *
   * @return Provided implicit encoder
   */
  def apply[I, O](implicit encoder: Encoder[I, O]): Encoder[I, O] = encoder
}
