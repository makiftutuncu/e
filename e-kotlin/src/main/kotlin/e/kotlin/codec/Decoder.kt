package e.kotlin.codec

import e.kotlin.E
import e.kotlin.EOr

/**
 * Typeclass defining how to decode an input value to an output value, possibly failing with E
 *
 * @param I Type of input
 * @param O Type of output
 *
 * @see e.kotlin.E
 * @see e.kotlin.EOr
 */
interface Decoder<in I, out O> {
  /**
   * Decodes an input, possibly failing with E
   *
   * @param input An input
   *
   * @return Decoded output or E
   *
   * @see e.kotlin.EOr
   */
  fun decode(input: I): EOr<O>

  companion object {
    /**
     * A default E to be used when decoding fails
     */
    val decodingError: E = E.name("decoding-error").message("Failed to decode!")
  }
}
