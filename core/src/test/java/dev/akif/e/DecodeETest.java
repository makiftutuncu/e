package dev.akif.e;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DecodeETest {
    private final DecoderE<String> codeParsingDecoderE = s -> {
        try {
            return E.empty.code(Integer.parseInt(s));
        } catch (Exception e) {
            throw new DecodingFailure(String.format("'%s' was not a valid code", s), e);
        }
    };

    @Test void testDecodeEFail() {
        assertThrows(DecodingFailure.class, () -> codeParsingDecoderE.decodeOrThrow("a"));
    }

    @Test void testDecodeESucceed() {
        assertEquals(E.empty.code(1), codeParsingDecoderE.decodeOrThrow("1"));
    }
}
