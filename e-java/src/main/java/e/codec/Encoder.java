package e.codec;

/**
 * Typeclass defining how to encode an input value to an output value
 *
 * @param <I> Type of input
 * @param <O> Type of output
 */
public interface Encoder<I, O> {
    /**
     * Encodes an input
     *
     * @param input An input
     *
     * @return Encoded output
     */
    O encode(I input);
}
