package e.java;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class JsonStringEncoderTest {
    private Throwable cause;
    private Map<String, String> data;

    private static JsonStringEncoder encoder = JsonStringEncoder.get();

    @BeforeEach void reset() {
        cause = new Exception("Test Exception");
        data = new LinkedHashMap<>();
        data.put("test", "data");
    }

    @Test void testEncodingEWithAllFields() {
        E e = new E(1, "test-name", "Test Message", cause, data);

        String expected = "{\"code\":1,\"name\":\"test-name\",\"message\":\"Test Message\",\"cause\":\"Test Exception\",\"data\":{\"test\":\"data\"}}";
        String actual   = encoder.encode(e);

        assertEquals(expected, actual);
    }

    @Test void testEncodingEWithSomeFields() {
        E e = new E().name("test-name").message("Test Message");

        String expected = "{\"name\":\"test-name\",\"message\":\"Test Message\"}";
        String actual   = encoder.encode(e);

        assertEquals(expected, actual);
    }

    @Test void testEncodingEWithQuotes() {
        data.put("test", "da\"ta");
        data.put("f\"oo", "bar");
        E e = new E().name("test-name").data(data);

        String expected = "{\"name\":\"test-name\",\"data\":{\"test\":\"da\\\"ta\",\"f\\\"oo\":\"bar\"}}";
        String actual   = encoder.encode(e);

        assertEquals(expected, actual);
    }

    @Test void testEncodingEWithNoFields() {
        E e = new E();

        String expected = "{}";
        String actual   = encoder.encode(e);

        assertEquals(expected, actual);
    }
}
