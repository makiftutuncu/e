package dev.akif.e;

public interface AbstractDecoder<IN, E extends AbstractE<?, ?>> {
    DecodingResult<E> decode(IN input);

    final class DecodingResult<E> {
        public final boolean isSuccess;
        private final E e;

        private DecodingResult(boolean isSuccess, E e) {
            this.isSuccess = isSuccess;
            this.e = e;
        }

        public static <E> DecodingResult<E> fail(E decodingError) {
            if (decodingError == null) {
                throw new IllegalArgumentException("Decoding failure cannot be null!");
            }

            return new DecodingResult<>(false, decodingError);
        }

        public static <E> DecodingResult<E> succeed(E decoded) {
            if (decoded == null) {
                throw new IllegalArgumentException("Decoded E cannot be null!");
            }

            return new DecodingResult<>(true, decoded);
        }

        public E get() {
            return e;
        }
    }
}
