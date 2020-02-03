package e;

public interface AbstractCodec<E extends AbstractE<?, ?>, A> extends AbstractDecoder<A, E>, AbstractEncoder<E, A> {}
