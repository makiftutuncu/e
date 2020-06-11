package e.kotlin.codec

import e.kotlin.EOr

/**
 * Typeclass defining decoding and encoding together via [e.kotlin.codec.Decoder] and [e.kotlin.codec.Encoder]
 *
 * @param S Type of source
 * @param T Type of target
 */
interface Codec<S, T>: Decoder<T, S>, Encoder<S, T> {
    companion object {
        /**
         * Creates a codec based on an implicit decoder and encoder
         *
         * @param S Type of source
         * @param T Type of target
         *
         * @param decoder Implicit instance of decoder
         * @param encoder Implicit instance of encoder
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
