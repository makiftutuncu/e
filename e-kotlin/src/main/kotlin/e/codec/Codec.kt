package e.codec

import e.EOr

/**
 * Typeclass defining decoding and encoding together via [e.codec.Decoder] and [e.codec.Encoder]
 *
 * @param S Type of source
 * @param T Type of target
 */
interface Codec<S, T>: Decoder<T, S>, Encoder<S, T> {
  companion object {
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
    fun <S, T> from(decoder: Decoder<T, S>, encoder: Encoder<S, T>): Codec<S, T> =
      object: Codec<S, T> {
        override fun decode(input: T): EOr<S> = decoder.decode(input)
        override fun encode(input: S): T      = encoder.encode(input)
      }
  }
}
