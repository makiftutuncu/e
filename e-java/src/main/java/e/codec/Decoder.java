package e.codec;

import e.E;
import e.EOr;

/**
 * Typeclass defining how to decode an input value to an output value, possibly failing with E
 *
 * @param <I> Type of input
 * @param <O> Type of output
 *
 * @see e.E
 * @see e.EOr
 */
public interface Decoder<I, O> {
    /**
     * Decodes an input, possibly failing with E
     *
     * @param input An input
     *
     * @return Decoded output or E
     *
     * @see e.EOr
     */
    EOr<O> decode(I input);

    /**
     * A default E to be used when decoding fails
     */
    E decodingError = E.fromName("decoding-error").message("Failed to decode!");
}
