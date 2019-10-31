package dev.akif.e;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ETest {
    @Test void testEmptyE() {
        E empty = E.empty;

        assertEquals(0, empty.code);
        assertEquals("", empty.name);
        assertEquals("", empty.message);
        assertNull(empty.cause);
        assertEquals(new HashMap<>(), empty.data);
    }

    @Test void testEWithCode() {
        E e = E.of(42);

        assertEquals(42, e.code);
        assertEquals("", e.name);
        assertEquals("", e.message);
        assertNull(e.cause);
        assertEquals(new HashMap<>(), e.data);
    }

    @Test void testEWithName() {
        E e = E.of("test");

        assertEquals(0, e.code);
        assertEquals("test", e.name);
        assertEquals("", e.message);
        assertNull(e.cause);
        assertEquals(new HashMap<>(), e.data);
    }

    @Test void testEWithMessage() {
        E e = E.empty.message("Test");

        assertEquals(0, e.code);
        assertEquals("", e.name);
        assertEquals("Test", e.message);
        assertNull(e.cause);
        assertEquals(new HashMap<>(), e.data);
    }

    @Test void testEWithCause() {
        Throwable t = new Exception("test");
        E e = E.empty.cause(t);

        assertEquals(0, e.code);
        assertEquals("", e.name);
        assertEquals("", e.message);
        assertEquals(t, e.cause);
        assertEquals(new HashMap<>(), e.data);
    }

    @Test void testEWithData() {
        Map<String, String> d = new HashMap<>();
        d.put("foo", "bar");
        E e = E.empty.data(d);

        assertEquals(0, e.code);
        assertEquals("", e.name);
        assertEquals("", e.message);
        assertNull(e.cause);
        assertEquals(d, e.data);
    }

    @Test void testEWithAll() {
        Map<String, String> d = new HashMap<>();
        d.put("foo", "bar");
        Throwable t = new Exception("test");
        E e = E.of(1, "test", "Test", t, d);

        assertEquals(1, e.code);
        assertEquals("test", e.name);
        assertEquals("Test", e.message);
        assertEquals(t, e.cause);
        assertEquals(d, e.data);
    }

    @Test void testEToString() {
        Map<String, String> d = new HashMap<>();
        d.put("foo", "bar");
        Throwable t = new Exception("test");
        E e = E.of(1, "test", "Test", t, d);

        String expected = DefaultEncoderE.get().encode(e);
        String actual   = e.toString();

        assertEquals(expected, actual);
    }
}
