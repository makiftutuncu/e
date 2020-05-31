package e.java.codec;

import e.java.EOr;

/**
 * Typeclass defining decoding and encoding together via {@link e.java.codec.Decoder} and {@link e.java.codec.Encoder}
 *
 * @param <S> Type of source
 * @param <T> Type of target
 */
public interface Codec<S, T> extends Decoder<T, S>, Encoder<S, T> {
    /**
     * Creates a codec based on an implicit decoder and encoder
     *
     * @param <S> Type of source
     * @param <T> Type of target
     *
     * @param decoder Implicit instance of decoder
     * @param encoder Implicit instance of encoder
     *
     * @return Created codec
     */
    static <S, T> Codec<S, T> from(Decoder<T, S> decoder, Encoder<S, T> encoder) {
        return new Codec<S, T>() {
            @Override public EOr<S> decode(T input) {
                return decoder.decode(input);
            }

            @Override public T encode(S input) {
                return encoder.encode(input);
            }
        };
    }
}
