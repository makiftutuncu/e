package e.kotlin.codec

/**
 * Typeclass defining how to encode an input value to an output value
 *
 * @param I Type of input
 * @param O Type of output
 */
interface Encoder<in I, out O> {
  /**
   * Encodes an input
   *
   * @param input An input
   *
   * @return Encoded output
   */
  fun encode(input: I): O
}
