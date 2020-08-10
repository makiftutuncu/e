package e.gson;

import static e.java.test.Assertions.*;
import static e.java.test.Helpers.*;

import com.google.gson.*;
import e.java.E;
import e.java.EOr;
import e.java.codec.Decoder;
import org.junit.jupiter.api.Test;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GsonTest {
    private final EGsonCodec eCodec = EGsonCodec.get();
    private final EOrGsonCodec<TestData> eOrCodec = new EOrGsonCodec<>(new Gson(), TestData.class);
    private final E error = Decoder.decodingError;

    private static final class TestData {
        public final String s;
        public final int i;

        public TestData(String s, int i) {
            this.s = s;
            this.i = i;
        }

        @Override public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof TestData)) return false;
            TestData that = (TestData) o;
            return this.i == that.i && Objects.equals(this.s, that.s);
        }

        @Override public int hashCode() {
            return Objects.hash(s, i);
        }

        @Override public String toString() {
            return new StringJoiner(",", TestData.class.getSimpleName() + "{", "}").add("\"s\":\"" + s + "\"").add("\"i\":" + i).toString();
        }
    }

    @Test void failingToDecodeAnE() {
        assertError(
            eCodec.decode(new JsonArray()),
            error.causes(E.fromMessage("Expected: JsonObject"))
        );

        assertError(
            eCodec.decode(obj(mapEntry("code", new JsonPrimitive("foo")))),
            error.causes(E.fromName("obj.code").message("Expected: Int"))
        );

        assertError(
            eCodec.decode(obj(mapEntry("name", new JsonPrimitive(42)))),
            error.causes(E.fromName("obj.name").message("Expected: String"))
        );

        assertError(
            eCodec.decode(obj(mapEntry("message", new JsonPrimitive(123)))),
            error.causes(E.fromName("obj.message").message("Expected: String"))
        );

        assertError(
            eCodec.decode(obj(mapEntry("causes", new JsonPrimitive("foo")))),
            error.causes(E.fromName("obj.causes").message("Expected: List<E>"))
        );

        assertError(
            eCodec.decode(obj(mapEntry("data", new JsonPrimitive("foo")))),
            error.causes(E.fromName("obj.data").message("Expected: Map<String, String>"))
        );

        assertError(
            eCodec.decode(obj(mapEntry("time", new JsonPrimitive("foo")))),
            error.causes(E.fromName("obj.time").message("Expected: Long"))
        );

        assertError(
            eCodec.decode(obj(mapEntry("code", new JsonPrimitive("foo")), mapEntry("name", new JsonPrimitive(42)))),
            error.causes(E.fromName("obj.code").message("Expected: Int"), E.fromName("obj.name").message("Expected: String"))
        );
    }

    @Test void decodingAnE() {
        assertValue(eCodec.decode(obj()), E.empty);

        JsonObject input1 = obj(
            mapEntry("code",    JsonNull.INSTANCE),
            mapEntry("name",    JsonNull.INSTANCE),
            mapEntry("message", JsonNull.INSTANCE),
            mapEntry("causes",  JsonNull.INSTANCE),
            mapEntry("data",    JsonNull.INSTANCE),
            mapEntry("time",    JsonNull.INSTANCE)
        );
        assertValue(eCodec.decode(input1), E.empty);

        JsonObject input2 = obj(
            mapEntry("code",    new JsonPrimitive(1)),
            mapEntry("name",    new JsonPrimitive("test-name")),
            mapEntry("message", new JsonPrimitive("Test Message")),
            mapEntry("causes",  arr(obj(mapEntry("name", new JsonPrimitive("cause-1"))), obj(mapEntry("name", new JsonPrimitive("cause-2"))))),
            mapEntry("data",    obj(mapEntry("foo", new JsonPrimitive("bar")))),
            mapEntry("time",    new JsonPrimitive(123456789L))
        );
        E e = new E(
            1,
            "test-name",
            "Test Message",
            listOf(E.fromName("cause-1"), E.fromName("cause-2")),
            mapOf(mapEntry("foo", "bar")),
            123456789L
        );
        assertValue(eCodec.decode(input2), e);
    }

    @Test void encodingAnE() {
        assertEquals(obj(), eCodec.encode(E.empty));

        JsonObject expected = obj(
            mapEntry("code",    new JsonPrimitive(1)),
            mapEntry("name",    new JsonPrimitive("test-name")),
            mapEntry("message", new JsonPrimitive("Test Message")),
            mapEntry("causes",  arr(obj(mapEntry("name", new JsonPrimitive("cause-1"))), obj(mapEntry("name", new JsonPrimitive("cause-2"))))),
            mapEntry("data",    obj(mapEntry("foo", new JsonPrimitive("bar")))),
            mapEntry("time",    new JsonPrimitive(123456789L))
        );
        E e = new E(
            1,
            "test-name",
            "Test Message",
            listOf(E.fromName("cause-1"), E.fromName("cause-2")),
            mapOf(mapEntry("foo", "bar")),
            123456789L
        );
        assertEquals(expected, eCodec.encode(e));
    }

    @Test void failingToDecodeTestDataOrE() {
        EOr<TestData> eor1 = eOrCodec.decode(arr());
        assertError(eor1, error.cause(E.fromMessage("java.lang.IllegalStateException: Expected BEGIN_OBJECT but was BEGIN_ARRAY at path $")));

        EOr<TestData> eor2 = eOrCodec.decode(obj(mapEntry("s", new JsonPrimitive(42)), mapEntry("i", new JsonPrimitive("foo"))));
        assertError(eor2, error.cause(E.fromMessage("java.lang.NumberFormatException: For input string: \"foo\"")));
    }

    @Test void decodingATestDataOrE() {
        EOr<TestData> eor = eOrCodec.decode(obj(mapEntry("s", new JsonPrimitive("foo")), mapEntry("i", new JsonPrimitive("42"))));
        assertValue(eor, new TestData("foo", 42));
    }

    @Test void encodingATestDataOrE() {
        JsonObject expected = obj(mapEntry("s", new JsonPrimitive("foo")), mapEntry("i", new JsonPrimitive(42)));

        JsonElement actual = eOrCodec.encode(EOr.from(new TestData("foo", 42)));

        assertEquals(expected, actual);
    }

    @SafeVarargs
    private final JsonObject obj(SimpleImmutableEntry<String, JsonElement>... properties) {
        JsonObject o = new JsonObject();
        Stream.of(properties).forEach(e -> o.add(e.getKey(), e.getValue()));
        return o;
    }

    private JsonArray arr(JsonElement... elements) {
        JsonArray a = new JsonArray();
        Stream.of(elements).forEach(a::add);
        return a;
    }
}
