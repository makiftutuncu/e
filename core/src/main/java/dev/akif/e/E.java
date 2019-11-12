package dev.akif.e;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class E extends Exception implements Serializable {
    public final int code;
    public final String name;
    public final String message;
    public final Throwable cause;
    public final Map<String, String> data;

    private E(int code, String name, String message, Throwable cause, Map<String, String> data) {
        super(message == null ? "" : message, cause);
        this.code = code;
        this.name = name == null ? "" : name;
        this.message = getMessage();
        this.cause = cause;
        this.data = data == null ? Collections.emptyMap() : data;
    }

    public static final E empty = E.of(0, "", "", null, Collections.emptyMap());

    public static E of(int code) {
        return new E(code, "", "", null, null);
    }

    public static E of(int code, String name) {
        return new E(code, name, "", null, null);
    }

    public static E of(int code, String name, String message) {
        return new E(code, name, message, null, null);
    }

    public static E of(int code, String name, String message, Throwable cause) {
        return new E(code, name, message, cause, null);
    }

    public static E of(int code, String name, String message, Map<String, String> data) {
        return new E(code, name, message, null, data);
    }

    public static E of(int code, String name, String message, Throwable cause, Map<String, String> data) {
        return new E(code, name, message, cause, data);
    }

    public static E of(String name) {
        return new E(0, name, "", null, null);
    }

    public static E of(String name, String message) {
        return new E(0, name, message, null, null);
    }

    public static E of(String name, String message, Throwable cause) {
        return new E(0, name, message, cause, null);
    }

    public static E of(String name, String message, Map<String, String> data) {
        return new E(0, name, message, null, data);
    }

    public static E of(String name, String message, Throwable cause, Map<String, String> data) {
        return new E(0, name, message, cause, data);
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

    public E data(String key, String value) {
        Map<String, String> newData = new HashMap<>(this.data);
        newData.put(key, value);
        return new E(this.code, this.name, this.message, this.cause, newData);
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

    @Override public synchronized Throwable fillInStackTrace() {
        if (!hasCause()) {
            return this;
        }

        return super.fillInStackTrace();
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
        return DefaultEncoderE.get().encode(this);
    }
}
