package dev.akif.e;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EncodeETest {
    private final EncoderE<String> fieldCountingEncoderE = e -> String.format(
        "%d,%d,%d,%d,%d",
        e.hasCode()    ? 1 : 0,
        e.hasName()    ? 1 : 0,
        e.hasMessage() ? 1 : 0,
        e.hasCause()   ? 1 : 0,
        e.hasData()    ? 1 : 0
    );

    @Test void testEncodeE() {
        Map<String, String> d = new HashMap<>();
        d.put("foo", "bar");
        E e = E.of(1, "test", "Test", null, d);

        String expected = "1,1,1,0,1";
        String actual   = fieldCountingEncoderE.encode(e);

        assertEquals(expected, actual);
    }
}
