package e.java;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public class Maybe<A> {
    private static final class Failure<A> extends Maybe<A> {
        public Failure(E e) {
            super(e, null);
        }
    }

    private static final class Success<A> extends Maybe<A> {
        public Success(A value) {
            super(null, value);
        }
    }

    private final E e;
    private final A value;

    protected Maybe(E e, A value) {
        this.e     = e;
        this.value = value;
    }

    public static <A> Maybe<A> failure(E e) {
        return new Failure<>(e);
    }

    public static <A> Maybe<A> success(A value) {
        return new Success<>(value);
    }

    public static <A> Maybe<A> catching(ThrowingSupplier<A> action, Function<Throwable, E> ifFailure) {
        try {
            return new Success<>(action.get());
        } catch (Throwable t) {
            return new Failure<>(ifFailure.apply(t));
        }
    }

    public boolean isSuccess() {
        return e == null && value != null;
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
