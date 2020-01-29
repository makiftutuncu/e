package dev.akif.e;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DefaultEncoderETest {
    /*
    private final DefaultEncoderE encoder = DefaultEncoderE.get();

    @Test void testEmptyE() {
        E e = E.empty;

        String expected = "{}";
        String actual   = encoder.encode(e);

        assertEquals(expected, actual);
    }

    @Test void testEWithCode() {
        E e = E.of(42);

        String expected = "{\"code\":42}";
        String actual   = encoder.encode(e);

        assertEquals(expected, actual);
    }

    @Test void testEWithName() {
        E e = E.of("test");

        String expected = "{\"name\":\"test\"}";
        String actual   = encoder.encode(e);

        assertEquals(expected, actual);
    }

    @Test void testEWithMessage() {
        E e = E.empty.message("Test");

        String expected = "{\"message\":\"Test\"}";
        String actual   = encoder.encode(e);

        assertEquals(expected, actual);
    }

    @Test void testEWithCause() {
        Throwable t = new Exception("test");
        E e = E.empty.cause(t);

        String expected = "{\"cause\":\"test\"}";
        String actual   = encoder.encode(e);

        assertEquals(expected, actual);
    }

    @Test void testEWithData() {
        E e = E.empty.data("foo", "bar");

        String expected = "{\"data\":{\"foo\":\"bar\"}}";
        String actual   = encoder.encode(e);

        assertEquals(expected, actual);
    }

    @Test void testEWithSome() {
        E e = E.of(1, "test", "Test");

        String expected = "{\"code\":1,\"name\":\"test\",\"message\":\"Test\"}";
        String actual   = encoder.encode(e);

        assertEquals(expected, actual);
    }

    @Test void testEWithAll() {
        Map<String, String> d = new HashMap<>();
        d.put("foo", "bar");
        Throwable t = new Exception("test");
        E e = E.of(1, "test", "Test", t, d);

        String expected = "{\"code\":1,\"name\":\"test\",\"message\":\"Test\",\"cause\":\"test\",\"data\":{\"foo\":\"bar\"}}";
        String actual   = encoder.encode(e);

        assertEquals(expected, actual);
    }

    @Test void testEWithAllEscaped() {
        Map<String, String> d = new HashMap<>();
        d.put("f\"oo", "ba\"r");
        Throwable t = new Exception("te\"st");
        E e = E.of(1, "te\"st", "Te\"st", t, d);

        String expected = "{\"code\":1,\"name\":\"te\\\"st\",\"message\":\"Te\\\"st\",\"cause\":\"te\\\"st\",\"data\":{\"f\\\"oo\":\"ba\\\"r\"}}";
        String actual   = encoder.encode(e);

        assertEquals(expected, actual);
    }
    */
}
