package e.java.codec;

import e.java.E;
import e.java.EOr;

/**
 * Typeclass defining how to decode an input value to an output value, possibly failing with E
 *
 * @param <I> Type of input
 * @param <O> Type of output
 *
 * @see e.java.E
 * @see e.java.EOr
 */
public interface Decoder<I, O> {
    /**
     * Decodes an input, possibly failing with E
     *
     * @param input An input
     *
     * @return Decoded output or E
     *
     * @see e.java.EOr
     */
    EOr<O> decode(I input);

    /**
     * A default E to be used when decoding fails
     */
    E decodingError = E.fromName("decoding-error").message("Failed to decode!");
}
