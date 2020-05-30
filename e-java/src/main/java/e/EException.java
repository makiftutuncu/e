package e;

/**
 * A RuntimeException wrapping E to be used where errors are represented as E but an Exception is needed
 *
 * @see e.E
 * @see java.lang.RuntimeException
 */
public class EException extends RuntimeException {
    public final E e;

    public EException(E e) {
        if (e == null) throw new IllegalArgumentException("E cannot be null!");
        this.e = e;
    }

    @Override public String getMessage() {
        return e.toString();
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EException)) return false;
        EException that = (EException) o;
        return this.e.equals(that.e);
    }

    @Override public int hashCode() {
        return e.hashCode();
    }

    @Override public String toString() {
        return e.toString();
    }
}
