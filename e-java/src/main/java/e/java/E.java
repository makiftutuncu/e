package e.java;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import e.AbstractE;

public final class E extends AbstractE<Throwable, Map<String, String>>  {
    public E(String name, String message, int code, Throwable cause, Map<String, String> data) {
        super(
            isBlankString(name) ? "" : name,
            isBlankString(message) ? "" : message,
            code,
            cause,
            data == null ? new LinkedHashMap<>() : data
        );
    }

    public E(String name, String message, int code, Throwable cause) {
        this(name, message, code, cause, null);
    }

    public E(String name, String message, int code) {
        this(name, message, code, null, null);
    }

    public E(String name, String message) {
        this(name, message, EMPTY_CODE, null, null);
    }

    public E(String name) {
        this(name, null, EMPTY_CODE, null, null);
    }

    public E() {
        this(null, null, EMPTY_CODE, null, null);
    }

    public static E empty() {
        return new E();
    }

    @Override public E name(String name) {
        return new E(name, message(), code(), cause(), data());
    }

    @Override public E message(String message) {
        return new E(name(), message, code(), cause(), data());
    }

    @Override public E code(int code) {
        return new E(name(), message(), code, cause(), data());
    }

    @Override public E cause(Throwable cause) {
        return new E(name(), message(), code(), cause, data());
    }

    @Override public E data(Map<String, String> data) {
        return new E(name(), message(), code(), cause(), data);
    }

    public E data(String key, Object value) {
        Map<String, String> data = new LinkedHashMap<>(data());
        data.put(key, String.valueOf(value));
        return new E(name(), message(), code(), cause(), data);
    }

    @Override public Map<String, String> data() {
        return Collections.unmodifiableMap(super.data());
    }

    @Override public boolean hasCause() {
        return cause() != null;
    }

    @Override public boolean hasData() {
        return !data().isEmpty();
    }

    @Override public EException toException() {
        return new EException(this);
    }

    public <A> Maybe<A> toMaybe() {
        return Maybe.failure(this);
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractE<?, ?>)) return false;

        AbstractE<?, ?> that = (AbstractE<?, ?>) o;

        return this.name().equals(that.name()) &&
               this.message().equals(that.message()) &&
               this.code() == that.code() &&
               Objects.equals(this.cause(), that.cause()) &&
               Objects.equals(this.data(), that.data());
    }

    @Override public int hashCode() {
        return Objects.hash(name(), message(), code(), cause(), data());
    }

    @Override public String toString() {
        return JsonStringEncoder.get().encode(this);
    }
}
