package dev.akif.e;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

public final class E extends Exception implements Serializable {
    public final int code;
    public final String name;
    public final String message;
    public final Throwable cause;
    public final Map<String, String> data;

    public static final E empty = new E(0, "", "", null, Collections.emptyMap());

    public E(int code, String name, String message, Throwable cause, Map<String, String> data) {
        super(message == null ? "" : message, cause);
        this.code = code;
        this.name = name == null ? "" : name;
        this.message = getMessage();
        this.cause = cause;
        this.data = data == null ? Collections.emptyMap() : data;
    }

    public E(int code, String name, String message) {
        this(code, name, message, null, Collections.emptyMap());
    }

    public E(int code, String name) {
        this(code, name, "", null, Collections.emptyMap());
    }

    public E code(int code) {
        return new E(code, this.name, this.message, this.cause, this.data);
    }

    public E name(String name) {
        return new E(this.code, name, this.message, this.cause, this.data);
    }

    public E message(String message) {
        return new E(this.code, this.name, message, this.cause, this.data);
    }

    public E cause(Throwable cause) {
        return new E(this.code, this.name, this.message, cause, this.data);
    }

    public E data(Map<String, String> data) {
        return new E(this.code, this.name, this.message, this.cause, data);
    }

    public boolean hasCode() {
        return code != 0;
    }

    public boolean hasName() {
        return !name.isEmpty();
    }

    public boolean hasMessage() {
        return !message.isEmpty();
    }

    public boolean hasCause() {
        return cause != null;
    }

    public boolean hasData() {
        return !data.isEmpty();
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        E that = (E) o;
        return this.code == that.code &&
            Objects.equals(this.name, that.name) &&
            Objects.equals(this.message, that.message) &&
            Objects.equals(this.cause, that.cause) &&
            Objects.equals(this.data, that.data);
    }

    @Override public int hashCode() {
        return Objects.hash(code, name, message, cause, data);
    }

    @Override public String toString() {
        return DefaultEncoderE.encode(this);
    }
}
