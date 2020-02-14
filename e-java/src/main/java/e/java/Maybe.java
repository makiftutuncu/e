package e.java;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public class Maybe<A> {
    private static final class Failure<A> extends Maybe<A> {
        public Failure(E e) {
            super(e);
        }
    }

    private static final class Success<A> extends Maybe<A> {
        public Success(A value) {
            super(value);
        }
    }

    private final E e;
    private final A value;

    private Maybe(E e, A value) {
        this.e     = e;
        this.value = value;
    }

    protected Maybe(E e) {
        if (e == null) throw new IllegalArgumentException("E cannot be null!");
        this.e     = e;
        this.value = null;
    }

    protected Maybe(A value) {
        if (value == null) throw new IllegalArgumentException("Value cannot be null!");
        this.e     = null;
        this.value = value;
    }

    public static <A> Maybe<A> failure(E e) {
        return new Failure<>(e);
    }

    public static <A> Maybe<A> success(A value) {
        return new Success<>(value);
    }

    public static Maybe<Void> unit() {
        return new Maybe<>(null, null);
    }

    public static <A> Maybe<A> catching(ThrowingSupplier<A> action, Function<Throwable, E> ifFailure) {
        try {
            return new Success<>(action.get());
        } catch (Throwable t) {
            return new Failure<>(ifFailure.apply(t));
        }
    }

    public static <A> Maybe<A> catchingMaybe(ThrowingSupplier<Maybe<A>> action, Function<Throwable, E> ifFailure) {
        try {
            return action.get();
        } catch (Throwable t) {
            return new Failure<>(ifFailure.apply(t));
        }
    }

    public static <A> Maybe<A> nullable(A value, Supplier<E> ifNull) {
        return value == null ? new Failure<>(ifNull.get()) : new Success<>(value);
    }

    public boolean isSuccess() {
        return e == null;
    }

    public Optional<E> eOptional() {
        return Optional.ofNullable(e);
    }

    public Optional<A> valueOptional() {
        return Optional.ofNullable(value);
    }

    public <B> Maybe<B> map(Function<A, B> f) {
        return flatMap(value -> new Success<>(f.apply(value)));
    }

    public <B> Maybe<B> flatMap(Function<A, Maybe<B>> f) {
        return !isSuccess() ? new Failure<>(e) : f.apply(value);
    }

    public <B> B fold(Function<E, B> ifFailure, Function<A, B> ifSuccess) {
        return !isSuccess() ? ifFailure.apply(e) : ifSuccess.apply(value);
    }

    public A getOrElse(A defaultValue) {
        return fold(e -> defaultValue, value -> value);
    }

    public Maybe<A> orElse(Maybe<A> alternative) {
        return !isSuccess() ? alternative : this;
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Maybe)) return false;

        Maybe<?> that = (Maybe<?>) o;

        return Objects.equals(this.e, that.e) && Objects.equals(this.value, that.value);
    }

    @Override public int hashCode() {
        return Objects.hash(e, value);
    }

    @Override public String toString() {
        return isSuccess() ? value.toString() : e.toString();
    }
}
