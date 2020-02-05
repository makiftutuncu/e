package e.java;

@FunctionalInterface
public interface ThrowingSupplier<A> {
    A get() throws Throwable;
}
