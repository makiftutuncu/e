package e.codec

import e.E
import e.EOr

/**
 * Typeclass defining how to decode an input value to an output value, possibly failing with E
 *
 * @param I Type of input
 * @param O Type of output
 *
 * @see e.E
 * @see e.EOr
 */
interface Decoder<in I, out O> {
  /**
   * Decodes an input, possibly failing with E
   *
   * @param input An input
   *
   * @return Decoded output or E
   *
   * @see e.EOr
   */
  fun decode(input: I): EOr<O>

  companion object {
    /**
     * A default E to be used when decoding fails
     */
    val decodingError: E = E.name("decoding-error").message("Failed to decode!")
  }
}
