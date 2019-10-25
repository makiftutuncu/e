package dev.akif.e;

import java.util.Objects;
import java.util.StringJoiner;

public class DecodingFailure extends Exception {
    public final String message;

    public DecodingFailure(String message) {
        super(message);
        this.message = message == null ? "" : message;
    }

    public DecodingFailure(String message, Throwable cause) {
        super(message, cause);
        this.message = message == null ? "" : message;
    }

    @Override public synchronized Throwable fillInStackTrace() {
        if (getCause() == null) {
            return this;
        }

        return super.fillInStackTrace();
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DecodingFailure that = (DecodingFailure) o;
        return message.equals(that.message);
    }

    @Override public int hashCode() {
        return Objects.hash(message);
    }

    @Override public String toString() {
        return message;
    }
}
