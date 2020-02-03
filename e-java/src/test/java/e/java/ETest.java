package e.java;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.LinkedHashMap;
import java.util.Map;

public class ETest {
    private Throwable cause;
    private Map<String, String> data;

    @BeforeEach void reset() {
        cause = new Exception("Test Exception");
        data = new LinkedHashMap<>();
        data.put("test", "data");
    }

    @Test void testConstructEWithAllFields() {
        E e = new E(1, "test-name", "Test Message", cause, data);

        assertTrue(e.hasCode());
        assertEquals(1, e.code());

        assertTrue(e.hasName());
        assertEquals("test-name", e.name());

        assertTrue(e.hasMessage());
        assertEquals("Test Message", e.message());

        assertTrue(e.hasCause());
        assertEquals("Test Exception", e.cause().getMessage());

        assertTrue(e.hasData());
        assertEquals(1, e.data().size());
        assertEquals("data", e.data().get("test"));
    }

    @Test void testConstructEWithNothing() {
        E e = new E();

        assertFalse(e.hasCode());
        assertEquals(E.EMPTY_CODE, e.code());

        assertFalse(e.hasName());
        assertEquals("", e.name());

        assertFalse(e.hasMessage());
        assertEquals("", e.message());

        assertFalse(e.hasCause());
        assertNull(e.cause());

        assertFalse(e.hasData());
        assertEquals(new LinkedHashMap<>(), e.data());
    }

    @Test void testConstructEAsEmpty() {
        E actual   = E.empty();
        E expected = new E();

        assertEquals(expected, actual);
    }

    @Test void testEquals() {
        E e = new E(1, "test-name", "Test Message", cause, data);
        Map<String, String> otherData = new LinkedHashMap<>();
        otherData.put("foo", "bar");

        assertEquals(e, new E(1, "test-name", "Test Message", cause, data));
        assertNotEquals(e, new E(2, "test-name", "Test Message", cause, data));
        assertNotEquals(e, new E(1, "foo", "Test Message", cause, data));
        assertNotEquals(e, new E(1, "test-name", "bar", cause, data));
        assertNotEquals(e, new E(1, "test-name", "Test Message", new Exception("baz"), data));
        assertNotEquals(e, new E(1, "test-name", "Test Message", cause, otherData));
    }

    @Test void testHashCode() {
        E e = new E(1, "test-name", "Test Message", cause, data);
        Map<String, String> otherData = new LinkedHashMap<>();
        otherData.put("foo", "bar");

        assertNotEquals(e.hashCode(), new E(2, "test-name", "Test Message", cause, data).hashCode());
        assertNotEquals(e.hashCode(), new E(1, "foo", "Test Message", cause, data).hashCode());
        assertNotEquals(e.hashCode(), new E(1, "test-name", "bar", cause, data).hashCode());
        assertNotEquals(e.hashCode(), new E(1, "test-name", "Test Message", new Exception("baz"), data).hashCode());
        assertNotEquals(e.hashCode(), new E(1, "test-name", "Test Message", cause, otherData).hashCode());
    }
}
