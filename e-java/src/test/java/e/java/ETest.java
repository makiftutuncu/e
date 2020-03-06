package e.java;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ETest {
    private Throwable cause;
    private Map<String, String> data;

    @BeforeEach void reset() {
        cause = new Exception("Test Exception");
        data = new LinkedHashMap<>();
        data.put("test", "data");
    }

    @Test void testConstructingEWithAllFields() {
        E e = new E("test-name", "Test Message", 1, cause, data);

        assertTrue(e.hasName());
        assertEquals("test-name", e.name());

        assertTrue(e.hasMessage());
        assertEquals("Test Message", e.message());

        assertTrue(e.hasCode());
        assertEquals(1, e.code());

        assertTrue(e.hasCause());
        assertEquals("Test Exception", e.cause().getMessage());

        assertTrue(e.hasData());
        assertEquals(1, e.data().size());
        assertEquals("data", e.data().get("test"));
    }

    @Test void testConstructingEWithNoFields() {
        E e = new E();

        assertFalse(e.hasName());
        assertEquals("", e.name());

        assertFalse(e.hasMessage());
        assertEquals("", e.message());

        assertFalse(e.hasCode());
        assertEquals(E.EMPTY_CODE, e.code());

        assertFalse(e.hasCause());
        assertNull(e.cause());

        assertFalse(e.hasData());
        assertEquals(new LinkedHashMap<>(), e.data());
    }

    @Test void testConstructingEAsEmpty() {
        E actual   = E.empty();
        E expected = new E();

        assertEquals(expected, actual);
    }

    @Test void testConvertingToException() {
        E e1 = new E("test-name", "Test Message", 1);

        Exception expected1 = new Exception(e1.toString());
        Exception actual1   = e1.toException();

        assertEquals(expected1.getMessage(), actual1.getMessage());
        assertNull(actual1.getCause());

        E e2 = e1.cause(cause);

        Exception expected2 = new Exception(e2.toString(), cause);
        Exception actual2   = e2.toException();

        assertEquals(expected2.getMessage(), actual2.getMessage());
        assertEquals(expected2.getCause(),   actual2.getCause());
    }

    @Test void testConvertingToMaybe() {
        E e = new E("test-name", "Test Message", 1);

        Maybe<String> expected = Maybe.failure(e);
        Maybe<String> actual   = e.toMaybe();

        assertEquals(expected, actual);
    }

    @Test void testEquality() {
        E e = new E("test-name", "Test Message", 1, cause, data);
        Map<String, String> otherData = new LinkedHashMap<>();
        otherData.put("foo", "bar");

        assertEquals(e,    new E("test-name", "Test Message", 1, cause,                data));
        assertNotEquals(e, new E("test-name", "Test Message", 2, cause,                data));
        assertNotEquals(e, new E("foo",       "Test Message", 1, cause,                data));
        assertNotEquals(e, new E("test-name", "bar",          1, cause,                data));
        assertNotEquals(e, new E("test-name", "Test Message", 1, new Exception("baz"), data));
        assertNotEquals(e, new E("test-name", "Test Message", 1, cause,                otherData));
    }

    @Test void testHashCodeGeneration() {
        E e = new E("test-name", "Test Message", 1, cause, data);
        Map<String, String> otherData = new LinkedHashMap<>();
        otherData.put("foo", "bar");

        assertNotEquals(e.hashCode(), new E("test-name", "Test Message", 2, cause,                data).hashCode());
        assertNotEquals(e.hashCode(), new E("foo",       "Test Message", 1, cause,                data).hashCode());
        assertNotEquals(e.hashCode(), new E("test-name", "bar",          1, cause,                data).hashCode());
        assertNotEquals(e.hashCode(), new E("test-name", "Test Message", 1, new Exception("baz"), data).hashCode());
        assertNotEquals(e.hashCode(), new E("test-name", "Test Message", 1, cause,                otherData).hashCode());
    }
}
