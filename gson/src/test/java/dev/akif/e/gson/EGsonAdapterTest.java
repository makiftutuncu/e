package dev.akif.e.gson;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import dev.akif.e.DecodingFailure;
import dev.akif.e.E;

class EGsonAdapterTest {
    private final EGsonAdapter adapter = new EGsonAdapter();
    private final Gson gson            = new GsonBuilder().registerTypeAdapter(E.class, adapter).create();

    @Test void testToJsonEmptyE() {
        E e = E.empty;

        String expected = "{}";
        String actual   = gson.toJson(e);

        assertEquals(expected, actual);
    }

    @Test void testToJsonEWithCode() {
        E e = E.of(42);

        String expected = "{\"code\":42}";
        String actual   = gson.toJson(e);

        assertEquals(expected, actual);
    }

    @Test void testToJsonEWithName() {
        E e = E.of("test");

        String expected = "{\"name\":\"test\"}";
        String actual   = gson.toJson(e);

        assertEquals(expected, actual);
    }

    @Test void testToJsonEWithMessage() {
        E e = E.empty.message("Test");

        String expected = "{\"message\":\"Test\"}";
        String actual   = gson.toJson(e);

        assertEquals(expected, actual);
    }

    @Test void testToJsonEWithCause() {
        Throwable t = new Exception("test");
        E e = E.empty.cause(t);

        String expected = "{\"cause\":\"test\"}";
        String actual   = gson.toJson(e);

        assertEquals(expected, actual);
    }

    @Test void testToJsonEWithData() {
        Map<String, String> d = new HashMap<>();
        d.put("foo", "bar");
        E e = E.empty.data(d);

        String expected = "{\"data\":{\"foo\":\"bar\"}}";
        String actual   = gson.toJson(e);

        assertEquals(expected, actual);
    }

    @Test void testToJsonEWithSome() {
        E e = E.of(1, "test", "Test");

        String expected = "{\"code\":1,\"name\":\"test\",\"message\":\"Test\"}";
        String actual   = gson.toJson(e);

        assertEquals(expected, actual);
    }

    @Test void testToJsonEWithAll() {
        Map<String, String> d = new HashMap<>();
        d.put("foo", "bar");
        Throwable t = new Exception("test");
        E e = E.of(1, "test", "Test", t, d);

        String expected = "{\"code\":1,\"name\":\"test\",\"message\":\"Test\",\"cause\":\"test\",\"data\":{\"foo\":\"bar\"}}";
        String actual   = gson.toJson(e);

        assertEquals(expected, actual);
    }

    @Test void testToJsonEWithAllEscaped() {
        Map<String, String> d = new HashMap<>();
        d.put("f\"oo", "ba\"r");
        Throwable t = new Exception("te\"st");
        E e = E.of(1, "te\"st", "Te\"st", t, d);

        String expected = "{\"code\":1,\"name\":\"te\\\"st\",\"message\":\"Te\\\"st\",\"cause\":\"te\\\"st\",\"data\":{\"f\\\"oo\":\"ba\\\"r\"}}";
        String actual   = gson.toJson(e);

        assertEquals(expected, actual);
    }

    @Test void testFromJsonEFail() {
        assertThrows(DecodingFailure.class, () -> gson.fromJson("[1,2,3]", E.class));
    }

    @Test void testFromJsonEmptyE() {
        String json = "{}";

        E expected = E.empty;
        E actual   = gson.fromJson(json, E.class);

        assertEquals(expected, actual);
    }

    @Test void testFromJsonEWithCode() {
        String json = "{\"code\":42}";

        E expected = E.of(42);
        E actual   = gson.fromJson(json, E.class);

        assertEquals(expected, actual);
    }

    @Test void testFromJsonEWithName() {
        String json = "{\"name\":\"test\"}";

        E expected = E.of("test");
        E actual   = gson.fromJson(json, E.class);

        assertEquals(expected, actual);
    }

    @Test void testFromJsonEWithMessage() {
        String json = "{\"message\":\"Test\"}";

        E expected = E.empty.message("Test");
        E actual   = gson.fromJson(json, E.class);

        assertEquals(expected, actual);
    }

    @Test void testFromJsonEWithCause() {
        String json = "{\"cause\":\"test\"}";

        // Cause is ignored
        E expected = E.empty;
        E actual   = gson.fromJson(json, E.class);

        assertEquals(expected, actual);
    }

    @Test void testFromJsonEWithData() {
        String json = "{\"data\":{\"foo\":\"bar\"}}";
        Map<String, String> d = new HashMap<>();
        d.put("foo", "bar");

        E expected = E.empty.data(d);
        E actual   = gson.fromJson(json, E.class);

        assertEquals(expected, actual);
    }

    @Test void testFromJsonEWithSome() {
        String json = "{\"code\":1,\"name\":\"test\",\"message\":\"Test\"}";

        E expected = E.of(1, "test", "Test");
        E actual   = gson.fromJson(json, E.class);

        assertEquals(expected, actual);
    }

    @Test void testFromJsonEWithAll() {
        String json = "{\"code\":1,\"name\":\"test\",\"message\":\"Test\",\"cause\":\"test\",\"data\":{\"foo\":\"bar\"}}";
        Map<String, String> d = new HashMap<>();
        d.put("foo", "bar");

        // Cause is ignored
        E expected = E.of(1, "test", "Test", d);
        E actual   = gson.fromJson(json, E.class);

        assertEquals(expected, actual);
    }

    @Test void testFromJsonEWithAllEscaped() {
        String json = "{\"code\":1,\"name\":\"te\\\"st\",\"message\":\"Te\\\"st\",\"data\":{\"f\\\"oo\":\"ba\\\"r\"}}";
        Map<String, String> d = new HashMap<>();
        d.put("f\"oo", "ba\"r");

        E expected = E.of(1, "te\"st", "Te\"st", d);
        E actual   = gson.fromJson(json, E.class);

        assertEquals(expected, actual);
    }

    @Test void testEncodeEmptyE() {
        E e = E.empty;

        JsonElement expected = new JsonObject();
        JsonElement actual   = adapter.encode(e);

        assertEquals(expected, actual);
    }

    @Test void testEncodeEWithCode() {
        E e = E.of(42);

        JsonObject expected = new JsonObject();
        expected.add("code", new JsonPrimitive(42));

        JsonElement actual = adapter.encode(e);

        assertEquals(expected, actual);
    }

    @Test void testEncodeEWithName() {
        E e = E.of("test");

        JsonObject expected = new JsonObject();
        expected.add("name", new JsonPrimitive("test"));

        JsonElement actual = adapter.encode(e);

        assertEquals(expected, actual);
    }

    @Test void testEncodeEWithMessage() {
        E e = E.empty.message("Test");

        JsonObject expected = new JsonObject();
        expected.add("message", new JsonPrimitive("Test"));

        JsonElement actual = adapter.encode(e);

        assertEquals(expected, actual);
    }

    @Test void testEncodeEWithCause() {
        Throwable t = new Exception("test");
        E e = E.empty.cause(t);

        JsonObject expected = new JsonObject();
        expected.add("cause", new JsonPrimitive("test"));

        JsonElement actual = adapter.encode(e);

        assertEquals(expected, actual);
    }

    @Test void testEncodeEWithData() {
        Map<String, String> d = new HashMap<>();
        d.put("foo", "bar");
        E e = E.empty.data(d);

        JsonObject expected = new JsonObject();
        JsonObject data = new JsonObject();
        data.add("foo", new JsonPrimitive("bar"));
        expected.add("data", data);

        JsonElement actual = adapter.encode(e);

        assertEquals(expected, actual);
    }

    @Test void testEncodeEWithSome() {
        E e = E.of(1, "test", "Test");

        JsonObject expected = new JsonObject();
        expected.add("code", new JsonPrimitive(1));
        expected.add("name", new JsonPrimitive("test"));
        expected.add("message", new JsonPrimitive("Test"));

        JsonElement actual = adapter.encode(e);

        assertEquals(expected, actual);
    }

    @Test void testEncodeEWithAll() {
        Map<String, String> d = new HashMap<>();
        d.put("foo", "bar");
        Throwable t = new Exception("test");
        E e = E.of(1, "test", "Test", t, d);

        JsonObject expected = new JsonObject();
        expected.add("code", new JsonPrimitive(1));
        expected.add("name", new JsonPrimitive("test"));
        expected.add("message", new JsonPrimitive("Test"));
        expected.add("cause", new JsonPrimitive("test"));
        JsonObject data = new JsonObject();
        data.add("foo", new JsonPrimitive("bar"));
        expected.add("data", data);

        JsonElement actual = adapter.encode(e);

        assertEquals(expected, actual);
    }

    @Test void testEncodeEWithAllEscaped() {
        Map<String, String> d = new HashMap<>();
        d.put("f\"oo", "ba\"r");
        Throwable t = new Exception("te\"st");
        E e = E.of(1, "te\"st", "Te\"st", t, d);

        JsonObject expected = new JsonObject();
        expected.add("code", new JsonPrimitive(1));
        expected.add("name", new JsonPrimitive("te\"st"));
        expected.add("message", new JsonPrimitive("Te\"st"));
        expected.add("cause", new JsonPrimitive("te\"st"));
        JsonObject data = new JsonObject();
        data.add("f\"oo", new JsonPrimitive("ba\"r"));
        expected.add("data", data);

        JsonElement actual = adapter.encode(e);

        assertEquals(expected, actual);
    }

    @Test void testDecodeEFail() {
        JsonArray json = new JsonArray();
        json.add("foo");
        assertThrows(DecodingFailure.class, () -> adapter.decodeOrThrow(json));
    }

    @Test void testDecodeEmptyE() {
        JsonObject json = new JsonObject();

        E expected = E.empty;
        E actual   = adapter.decodeOrThrow(json);

        assertEquals(expected, actual);
    }

    @Test void testDecodeEWithCode() {
        JsonObject json = new JsonObject();
        json.add("code", new JsonPrimitive(1));

        E expected = E.of(1);
        E actual   = adapter.decodeOrThrow(json);

        assertEquals(expected, actual);
    }

    @Test void testDecodeEWithName() {
        JsonObject json = new JsonObject();
        json.add("name", new JsonPrimitive("test"));

        E expected = E.of("test");
        E actual   = adapter.decodeOrThrow(json);

        assertEquals(expected, actual);
    }

    @Test void testDecodeEWithMessage() {
        JsonObject json = new JsonObject();
        json.add("message", new JsonPrimitive("Test"));

        E expected = E.empty.message("Test");
        E actual   = adapter.decodeOrThrow(json);

        assertEquals(expected, actual);
    }

    @Test void testDecodeEWithCause() {
        JsonObject json = new JsonObject();
        json.add("cause", new JsonPrimitive("test"));

        // Cause is ignored
        E expected = E.empty;
        E actual   = adapter.decodeOrThrow(json);

        assertEquals(expected, actual);
    }

    @Test void testDecodeEWithData() {
        JsonObject json = new JsonObject();
        JsonObject data = new JsonObject();
        data.add("foo", new JsonPrimitive("bar"));
        json.add("data", data);
        Map<String, String> d = new HashMap<>();
        d.put("foo", "bar");

        E expected = E.empty.data(d);
        E actual   = adapter.decodeOrThrow(json);

        assertEquals(expected, actual);
    }

    @Test void testDecodeEWithSome() {
        JsonObject json = new JsonObject();
        json.add("code", new JsonPrimitive(1));
        json.add("name", new JsonPrimitive("test"));
        json.add("message", new JsonPrimitive("Test"));

        E expected = E.of(1, "test", "Test");
        E actual   = adapter.decodeOrThrow(json);

        assertEquals(expected, actual);
    }

    @Test void testDecodeEWithAll() {
        JsonObject json = new JsonObject();
        json.add("code", new JsonPrimitive(1));
        json.add("name", new JsonPrimitive("test"));
        json.add("message", new JsonPrimitive("Test"));
        json.add("cause", new JsonPrimitive("test"));
        JsonObject data = new JsonObject();
        data.add("foo", new JsonPrimitive("bar"));
        json.add("data", data);
        Map<String, String> d = new HashMap<>();
        d.put("foo", "bar");

        // Cause is ignored
        E expected = E.of(1, "test", "Test", d);
        E actual   = adapter.decodeOrThrow(json);

        assertEquals(expected, actual);
    }

    @Test void testDecodeEWithAllEscaped() {
        JsonObject json = new JsonObject();
        json.add("code", new JsonPrimitive(1));
        json.add("name", new JsonPrimitive("te\"st"));
        json.add("message", new JsonPrimitive("Te\"st"));
        json.add("cause", new JsonPrimitive("te\"st"));
        JsonObject data = new JsonObject();
        data.add("f\"oo", new JsonPrimitive("ba\"r"));
        json.add("data", data);
        Map<String, String> d = new HashMap<>();
        d.put("f\"oo", "ba\"r");

        E expected = E.of(1, "te\"st", "Te\"st", d);
        E actual   = adapter.decodeOrThrow(json);

        assertEquals(expected, actual);
    }
}
