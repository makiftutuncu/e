package e.java;

/**
 * A helper functional interface marking a value to be supplied in an unsafe way
 *
 * @param <A> Type of value to be supplied
 */
@FunctionalInterface
public interface UnsafeSupplier<A> {
    /**
     * Gets a value in an unsafe way
     *
     * @return Value if nothing is thrown
     *
     * @throws Throwable If something goes wrong while getting the value
     */
    A get() throws Throwable;
}
