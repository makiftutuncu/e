package dev.akif.e;

public interface AbstractEncoder<E extends AbstractE<?, ?>, OUT> {
    OUT encode(E e);
}
