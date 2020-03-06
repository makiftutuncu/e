package e.java;

public class EException extends Exception {
    public final E e;

    public EException(E e) {
        super(e.toString(), e.cause());
        this.e = e;
    }
}
