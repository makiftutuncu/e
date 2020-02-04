package e.gson;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;

import e.AbstractDecoder.DecodingResult;
import e.java.E;

public class GsonAdapterTest {
    private final GsonAdapterForE adapter = GsonAdapterForE.get();
    private final Gson gson = new GsonBuilder().registerTypeAdapter(E.class, adapter).create();

    @Test void testDecodingAndFailing() {
        JsonArray json = new JsonArray();
        json.add(1);
        json.add(2);

        E expected = E.empty()
                      .name("decoding-failure")
                      .message("Cannot decode as E!")
                      .data("input", json.toString());

        DecodingResult<E> result = adapter.decode(json);
        E decoded                = result.get();

        assertFalse(result.isSuccess);
        assertEquals(expected, decoded.cause(null));
        assertEquals("Not a JSON Object: [1,2]", decoded.cause().getMessage());
    }

    @Test void testDecodingAndSucceeding() {
        JsonObject data = new JsonObject();
        data.add("test", new JsonPrimitive("data"));

        JsonObject json = new JsonObject();
        json.add("code", new JsonPrimitive(1));
        json.add("name", new JsonPrimitive("test-name"));
        json.add("message", new JsonPrimitive("Test Message"));
        json.add("data", data);

        E expected = new E(1, "test-name", "Test Message").data("test", "data");

        DecodingResult<E> result = adapter.decode(json);

        assertTrue(result.isSuccess);
        assertEquals(expected, result.get());
    }

    @Test void testEncoding() {
        E e = new E(1, "test-name", "Test Message", new Exception("Test Exception")).data("test", "data");

        JsonObject data = new JsonObject();
        data.add("test", new JsonPrimitive("data"));

        JsonObject expected = new JsonObject();
        expected.add("code", new JsonPrimitive(1));
        expected.add("name", new JsonPrimitive("test-name"));
        expected.add("message", new JsonPrimitive("Test Message"));
        expected.add("cause", new JsonPrimitive("Test Exception"));
        expected.add("data", data);

        JsonElement actual = adapter.encode(e);

        assertEquals(expected, actual);
    }

    @Test void testDeserializingAndFailing() {
        JsonArray json = new JsonArray();
        json.add(1);
        json.add(2);

        assertThrows(JsonParseException.class, () -> gson.fromJson(json, E.class));
    }

    @Test void testDeserializingAndSucceeding() {
        JsonObject data = new JsonObject();
        data.add("test", new JsonPrimitive("data"));

        JsonObject json = new JsonObject();
        json.add("code", new JsonPrimitive(1));
        json.add("name", new JsonPrimitive("test-name"));
        json.add("message", new JsonPrimitive("Test Message"));
        json.add("data", data);

        E expected = new E(1, "test-name", "Test Message").data("test", "data");
        E actual   = gson.fromJson(json, E.class);

        assertEquals(expected, actual);
    }

    @Test void testSerializing() {
        E e = new E(1, "test-name", "Test Message", new Exception("Test Exception")).data("test", "data");

        JsonObject data = new JsonObject();
        data.add("test", new JsonPrimitive("data"));

        JsonObject expected = new JsonObject();
        expected.add("code", new JsonPrimitive(1));
        expected.add("name", new JsonPrimitive("test-name"));
        expected.add("message", new JsonPrimitive("Test Message"));
        expected.add("cause", new JsonPrimitive("Test Exception"));
        expected.add("data", data);

        JsonElement actual = gson.toJsonTree(e);

        assertEquals(expected, actual);
    }
}
