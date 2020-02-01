package dev.akif.e;

public interface AbstractCodec<E extends AbstractE<?, ?>, IN, OUT> extends AbstractDecoder<IN, E>, AbstractEncoder<E, OUT> {}
