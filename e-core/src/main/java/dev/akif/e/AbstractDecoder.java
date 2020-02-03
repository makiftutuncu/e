package dev.akif.e;

import java.util.Optional;

public interface AbstractDecoder<IN, E extends AbstractE<?, ?>> {
    DecodingResult<E> decode(IN input);

    final class DecodingResult<E> {
        private final E decodingError;
        private final E decoded;

        private DecodingResult(E decodingError, E decoded) {
            this.decodingError = decodingError;
            this.decoded = decoded;
        }

        public static <E> DecodingResult<E> fail(E decodingError) {
            return new DecodingResult<>(decodingError, null);
        }

        public static <E> DecodingResult<E> succeed(E decoded) {
            return new DecodingResult<>(null, decoded);
        }

        public Optional<E> decodingError() {
            return Optional.ofNullable(decodingError);
        }

        public Optional<E> decoded() {
            return Optional.ofNullable(decoded);
        }
    }
}
