package e.gson;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.internal.bind.TreeTypeAdapter;

import e.java.E;
import e.java.Maybe;

public class GsonSerializerForMaybeTest {
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapterFactory(TreeTypeAdapter.newTypeHierarchyFactory(Maybe.class, GsonSerializerForMaybe.get()))
            .create();

    @Test void testSerializingFailure() {
        E e = new E("test-name", "Test Message", 1, new Exception("Test Exception")).data("test", "data");
        Maybe<String> maybe = Maybe.failure(e);

        JsonObject data = new JsonObject();
        data.add("test", new JsonPrimitive("data"));

        JsonObject expected = new JsonObject();
        expected.add("name", new JsonPrimitive("test-name"));
        expected.add("message", new JsonPrimitive("Test Message"));
        expected.add("code", new JsonPrimitive(1));
        expected.add("cause", new JsonPrimitive("Test Exception"));
        expected.add("data", data);

        JsonElement actual = gson.toJsonTree(maybe);

        assertEquals(expected, actual);
    }

    @Test void testSerializingSuccess() {
        Map<String, String> map = new HashMap<>();
        map.put("foo", "bar");
        Maybe<Map<String, String>> maybe = Maybe.success(map);

        JsonObject expected = new JsonObject();
        expected.add("foo", new JsonPrimitive("bar"));

        JsonElement actual = gson.toJsonTree(maybe);

        assertEquals(expected, actual);
    }
}
