package dev.akif.e;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public final class E extends AbstractE<Throwable, Map<String, String>> {
    private E(int code, String name, String message, Throwable cause, Map<String, String> data) {
        super(
            code,
            name == null || !name.trim().isEmpty() ? "" : name,
            message == null || !message.trim().isEmpty() ? "" : message,
            cause,
            data == null ? new LinkedHashMap<>() : data
        );
    }

    public static E of(int code, String name, String message, Throwable cause, Map<String, String> data) {
        return new E(code, name, message, cause, data);
    }

    public static E of(int code, String name, String message, Throwable cause) {
        return new E(code, name, message, cause, null);
    }

    public static E of(int code, String name, String message) {
        return new E(code, name, message, null, null);
    }

    public static E of(int code, String name) {
        return new E(code, name, null, null, null);
    }

    public static E of(int code) {
        return new E(code, null, null, null, null);
    }

    public static E empty() {
        return new E(EMPTY_CODE, null, null, null, null);
    }

    public E code(int code) {
        return new E(code, name(), message(), cause(), data());
    }

    public E name(String name) {
        return new E(code(), name, message(), cause(), data());
    }

    public E message(String message) {
        return new E(code(), name(), message, cause(), data());
    }

    public E cause(Throwable cause) {
        return new E(code(), name(), message(), cause, data());
    }

    public E data(Map<String, String> data) {
        return new E(code(), name(), message(), cause(), data);
    }

    public E data(String key, String value) {
        Map<String, String> data = new LinkedHashMap<>(data());
        data.put(key, value);
        return new E(code(), name(), message(), cause(), data);
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

    @Override public Exception toException() {
        return new Exception(message(), cause());
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbstractE<?, ?>)) return false;

        AbstractE<?, ?> that = (AbstractE<?, ?>) o;

        return this.code() == that.code() &&
                this.name().equals(that.name()) &&
                this.message().equals(that.message()) &&
                Objects.equals(this.cause(), that.cause()) &&
                Objects.equals(this.data(), that.data());
    }

    @Override public int hashCode() {
        return Objects.hash(code(), name(), message(), cause(), data());
    }

    @Override public String toString() {
        return JsonStringEncoder.get().toString();
    }
}
