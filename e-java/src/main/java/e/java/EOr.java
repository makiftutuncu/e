package e.java;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * A container that can either be a Failure containing an E or Success containing a value
 *
 * @param <A> Type of the value this EOr can contain
 *
 * @see e.java.E
 */
public interface EOr<A> {
    /**
     * Whether or not this contains an E
     */
    default boolean isFailure() {
        return this instanceof Failure;
    }

    /**
     * Whether or not this contains a value
     */
    default boolean isSuccess() {
        return this instanceof Success;
    }

    /**
     * E in this as an Optional
     */
    default Optional<E> error() {
        if (isSuccess()) { return Optional.empty(); }

        E e = ((Failure<?>) this).e;

        return Optional.of(e);
    }

    /**
     * Value in this as an Optional
     */
    default Optional<A> value() {
        if (isFailure()) { return Optional.empty(); }

        A a = ((Success<A>) this).a;

        return Optional.of(a);
    }

    /**
     * Converts value in this, if it exists, using given mapping function to make a new EOr
     *
     * @param <B> Type of the new value
     *
     * @param f Mapping function
     *
     * @return A new EOr containing either the new value or E in this one
     */
    default <B> EOr<B> map(Function<A, B> f) {
        if (isFailure()) {
            E e = ((Failure<?>) this).e;

            return new Failure<>(e);
        }

        A a = ((Success<A>) this).a;
        B b = f.apply(a);

        return new Success<>(b);
    }

    /**
     * Computes a new EOr using value in this, if it exists, with given flat mapping function
     *
     * @param <B> Type of the new value
     *
     * @param f Flat mapping function
     *
     * @return Computed EOr or a new EOr containing E in this one
     */
    default <B> EOr<B> flatMap(Function<A, EOr<B>> f) {
        if (isFailure()) {
            E e = ((Failure<?>) this).e;

            return new Failure<>(e);
        }

        A a = ((Success<A>) this).a;

        return f.apply(a);
    }

    /**
     * Converts E in this, if it exists, using given mapping function to make a new EOr
     *
     * @param f E mapping function
     *
     * @return This EOr or a new EOr containing computed E if this one has E
     */
    default EOr<A> mapError(Function<E, E> f) {
        if (isSuccess()) { return this; }

        E thisE = ((Failure<?>) this).e;
        E thatE = f.apply(thisE);

        return new Failure<>(thatE);
    }

    /**
     * Computes a new EOr using E in this, if it exists, with given flat mapping function
     *
     * @param f E flat mapping function
     *
     * @return This EOr or a computed EOr if this one has E
     */
    default EOr<A> flatMapError(Function<E, EOr<A>> f) {
        if (isSuccess()) { return this; }

        E e = ((Failure<?>) this).e;

        return f.apply(e);
    }

    /**
     * Folds this into a single value, handling both E and value conversions with given functions
     *
     * @param <B> Type of the desired result
     *
     * @param ifFailure Conversion function for E
     * @param ifSuccess Conversion function for value
     *
     * @return Converted result
     */
    default <B> B fold(Function<E, B> ifFailure, Function<A, B> ifSuccess) {
        if (isFailure()) {
            E e = ((Failure<?>) this).e;

            return ifFailure.apply(e);
        }

        A a = ((Success<A>) this).a;

        return ifSuccess.apply(a);
    }

    /**
     * Gets the value in this or falls back to given default value
     *
     * @param alternative Default value to use in case this has E
     *
     * @return Value in this or given default value
     */
    default A getOrElse(Supplier<A> alternative) {
        if (isSuccess()) { return ((Success<A>) this).a; }

        return alternative.get();
    }

    /**
     * Provides an alternative EOr if this one has E, ignoring the E
     *
     * @param alternative Alternative EOr in case this one has E
     *
     * @return This EOr or alternative if this one has E
     */
    default EOr<A> orElse(Supplier<EOr<A>> alternative) {
        if (isSuccess()) { return this; }

        return alternative.get();
    }

    /**
     * Provides a next EOr if this one has a value, ignoring the value
     *
     * @param <B> Type of value in next EOr
     *
     * @param next Next EOr in case this one has a value
     *
     * @return Next EOr or a new EOr containing E in this one
     */
    default <B> EOr<B> andThen(Supplier<EOr<B>> next) {
        if (isFailure()) {
            E e = ((Failure<?>) this).e;

            return new Failure<>(e);
        }

        return next.get();
    }

    /**
     * Performs a side-effect using value in this, if it exists
     *
     * @param <U> Type of result of the side-effect
     *
     * @param f Side-effecting function
     */
    default <U> void forEach(Function<A, U> f) {
        if (isFailure()) { return; }

        A a = ((Success<A>) this).a;
        f.apply(a);
    }

    /**
     * Filters this EOr by value in it, if it exists, using given function
     *
     * @param condition     Filtering function
     * @param filteredError E conversion function
     *
     * @return This EOr of a new EOr containing an E computed by given conversion function
     */
    default EOr<A> filter(Function<A, Boolean> condition, Function<A, E> filteredError) {
        if (isFailure()) { return this; }

        A a = ((Success<A>) this).a;

        if (condition.apply(a)) { return this; }

        return filteredError.apply(a).toEOr();
    }

    /**
     * Filters this EOr by value in it, if it exists
     *
     * @param condition Filtering function
     *
     * @return This EOr of a new EOr containing an E
     *
     * @see e.java.EOr#filteredError
     */
    default EOr<A> filter(Function<A, Boolean> condition) {
        return filter(condition, a -> EOr.filteredError.data("value", a));
    }

    /**
     * A failed EOr
     *
     * @param <A> Type of the value this EOr can contain
     */
    final class Failure<A> implements EOr<A> {
        private final E e;

        public Failure(E e) {
            if (e == null) { throw new IllegalArgumentException("E cannot be null!"); }
            this.e = e;
        }

        /** E in this EOr */
        public E e() {
            return e;
        }

        @Override public boolean equals(Object o) {
            if (this == o) { return true; }
            if (!(o instanceof Failure)) { return false; }

            Failure<?> that = (Failure<?>) o;

            return this.e.equals(that.e);
        }

        @Override public int hashCode() {
            return Objects.hash(e);
        }

        @Override public String toString() {
            return e.toString();
        }
    }

    /**
     * A successful EOr
     *
     * @param <A> Type of the value this EOr can contain
     */
    final class Success<A> implements EOr<A> {
        private final A a;

        public Success(A a) {
            this.a = a;
        }

        /** Value in this EOr */
        public A a() {
            return a;
        }

        @Override public boolean equals(Object o) {
            if (this == o) { return true; }
            if (!(o instanceof Success)) { return false; }

            Success<?> that = (Success<?>) o;

            return Objects.equals(this.a, that.a);
        }

        @Override public int hashCode() {
            return Objects.hash(a);
        }

        @Override public String toString() {
            return String.valueOf(a);
        }
    }

    /**
     * A successful EOr of type Unit
     */
    EOr<Void> unit = new Success<>(null);

    /**
     * A default E to be used when condition does not hold while filtering an EOr
     *
     * @see e.java.EOr#filter
     */
    E filteredError = E.fromName("filtered").message("Condition does not hold!");

    /**
     * Constructs a failed EOr containing given E
     *
     * @param <A> Type of value of resulting EOr
     *
     * @param e An E
     *
     * @return A new failed EOr containing given E
     */
    static <A> EOr<A> from(E e) {
        return new Failure<>(e);
    }

    /**
     * Constructs a successful EOr containing given value
     *
     * @param <A> Type of value of resulting EOr
     *
     * @param a A value
     *
     * @return A new failed EOr containing given value
     */
    static <A> EOr<A> from(A a) {
        return new Success<>(a);
    }

    /**
     * Constructs an EOr from a nullable value
     *
     * @param <A> Type of value
     *
     * @param a      A nullable value
     * @param ifNull An error to use in case value is null
     *
     * @return An EOr containing either value or given E
     */
    static <A> EOr<A> fromNullable(A a, Supplier<E> ifNull) {
        return a == null ? new Failure<>(ifNull.get()) : new Success<>(a);
    }

    /**
     * Constructs an EOr from a computation that can throw
     *
     * @param <A> Type of value
     *
     * @param f         A computation that can throw
     * @param ifFailure An E conversion function
     *
     * @return An EOr containing either computed value or an E computed by given function
     */
    static <A> EOr<A> catching(UnsafeSupplier<A> f, Function<Throwable, E> ifFailure) {
        try {
            return new Success<>(f.get());
        } catch (EException ee) {
            return new Failure<>(ee.e);
        } catch (Throwable t) {
            return new Failure<>(ifFailure.apply(t));
        }
    }
}
