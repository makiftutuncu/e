package e.gson;

import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

import com.google.gson.*;

import e.java.E;
import e.java.EOr;
import e.java.codec.Codec;
import e.java.codec.Decoder;

public class EGsonCodec implements Codec<E, JsonElement>,
                                   JsonSerializer<E>,
                                   JsonDeserializer<E> {
    private static EGsonCodec instance;

    private EGsonCodec() {}

    public static EGsonCodec get() {
        if (instance == null) {
            instance = new EGsonCodec();
        }

        return instance;
    }

    @Override public JsonElement encode(E e) {
        JsonObject obj = new JsonObject();

        e.code().ifPresent(code -> obj.addProperty("code", code));

        e.name().ifPresent(name -> obj.addProperty("name", name));

        e.message().ifPresent(message -> obj.addProperty("message", message));

        if (e.hasCause()) {
            obj.add(
                "causes",
                e.causes()
                 .stream()
                 .map(cause -> { JsonArray cs = new JsonArray(); cs.add(encode(cause)); return cs; })
                 .reduce(new JsonArray(), (a1, a2) -> { a1.addAll(a2); return a1; })
            );
        }

        if (e.hasData()) {
            obj.add(
                "data",
                e.data()
                 .entrySet()
                 .stream()
                 .map(entry -> { JsonObject m = new JsonObject(); m.addProperty(entry.getKey(), entry.getValue()); return m; })
                 .reduce(new JsonObject(), (o1, o2) -> { o2.entrySet().forEach(entry -> o1.add(entry.getKey(), entry.getValue())); return o1; })
            );
        }

        e.time().ifPresent(time -> obj.addProperty("time", time));

        return obj;
    }

    @Override public EOr<E> decode(JsonElement json) {
        // Gets modified by lambdas below
        // To make this reference effectively final, I made it an array of single item
        final E[] decodedE = new E[] { E.empty };

        List<E> decodingFailures = new LinkedList<>();

        if (!json.isJsonObject()) {
            decodingFailures.add(E.fromMessage("Expected: JsonObject"));
        } else {
            JsonObject obj = json.getAsJsonObject();

            decodeCode(obj).fold(
                decodingFailure -> { decodingFailures.add(decodingFailure); return null; },
                maybeCode       -> { maybeCode.ifPresent(code -> decodedE[0] = decodedE[0].code(code)); return null; }
            );

            decodeName(obj).fold(
                decodingFailure -> { decodingFailures.add(decodingFailure); return null; },
                maybeName       -> { maybeName.ifPresent(name -> decodedE[0] = decodedE[0].name(name)); return null; }
            );

            decodeMessage(obj).fold(
                decodingFailure -> { decodingFailures.add(decodingFailure); return null; },
                maybeMessage    -> { maybeMessage.ifPresent(message -> decodedE[0] = decodedE[0].message(message)); return null; }
            );

            decodeCauses(obj).fold(
                decodingFailure -> { decodingFailures.add(decodingFailure); return null; },
                causes          -> { decodedE[0] = decodedE[0].causes(causes); return null; }
            );

            decodeData(obj).fold(
                decodingFailure -> { decodingFailures.add(decodingFailure); return null; },
                data            -> { decodedE[0] = decodedE[0].data(data); return null; }
            );

            decodeTime(obj).fold(
                decodingFailure -> { decodingFailures.add(decodingFailure); return null; },
                maybeTime       -> { maybeTime.ifPresent(time -> decodedE[0] = decodedE[0].time(time)); return null; }
            );
        }

        return !decodingFailures.isEmpty() ?
               new EOr.Failure<>(Decoder.decodingError.causes(decodingFailures)) :
               new EOr.Success<>(decodedE[0]);
    }

    @Override public JsonElement serialize(E e, Type typeOfSrc, JsonSerializationContext context) {
        return encode(e);
    }

    @Override public E deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        EOr<E> result = decode(json);

        return result.fold(
            failure -> { throw new JsonParseException(failure.toException()); },
            e -> e
        );
    }

    private EOr<Optional<Integer>> decodeCode(JsonObject obj) {
        return (!obj.has("code") || obj.get("code").isJsonNull()) ?
               EOr.from(Optional.empty()) :
               primitive("code", "Int", obj, JsonPrimitive::isNumber, JsonPrimitive::getAsInt).map(Optional::of);
    }

    private EOr<Optional<String>> decodeName(JsonObject obj) {
        return (!obj.has("name") || obj.get("name").isJsonNull()) ?
               EOr.from(Optional.empty()) :
               primitive("name", "String", obj, JsonPrimitive::isString, JsonPrimitive::getAsString).map(Optional::of);
    }

    private EOr<Optional<String>> decodeMessage(JsonObject obj) {
        return (!obj.has("message") || obj.get("message").isJsonNull()) ?
               EOr.from(Optional.empty()) :
               primitive("message", "String", obj, JsonPrimitive::isString, JsonPrimitive::getAsString).map(Optional::of);
    }

    private EOr<List<E>> decodeCauses(JsonObject obj) {
        if (!obj.has("causes") || obj.get("causes").isJsonNull()) {
            return EOr.from(new LinkedList<>());
        }

        JsonElement causesJson = obj.get("causes");
        E expected = expected("causes", "List<E>");

        if (!causesJson.isJsonArray()) {
            return expected.toEOr();
        }

        List<E> causeDecodingFailures = new LinkedList<>();
        List<E> decodedCauses = new LinkedList<>();

        causesJson.getAsJsonArray().iterator().forEachRemaining(j -> decode(j).fold(
            decodingFailure -> { causeDecodingFailures.add(decodingFailure); return null; },
            cause           -> { decodedCauses.add(cause); return null; }
        ));

        if (!causeDecodingFailures.isEmpty()) {
            return expected.causes(causeDecodingFailures).toEOr();
        }

        return EOr.from(decodedCauses);
    }

    private EOr<Map<String, String>> decodeData(JsonObject obj) {
        if (!obj.has("data") || obj.get("data").isJsonNull()) {
            return EOr.from(new LinkedHashMap<>());
        }

        JsonElement dataJson = obj.get("data");
        E expected = expected("data", "Map<String, String>");

        if (!dataJson.isJsonObject()) {
            return expected.toEOr();
        }

        List<E> dataDecodingFailures = new LinkedList<>();
        Map<String, String> decodedData = new LinkedHashMap<>();

        dataJson.getAsJsonObject().entrySet().iterator().forEachRemaining(j -> {
            String key = j.getKey();
            JsonElement value = j.getValue();

            if (!value.isJsonPrimitive() || !value.getAsJsonPrimitive().isString()) {
                dataDecodingFailures.add(expected("data." + key, "String"));
            } else {
                decodedData.put(key, value.getAsJsonPrimitive().getAsString());
            }
        });

        if (!dataDecodingFailures.isEmpty()) {
            return expected.causes(dataDecodingFailures).toEOr();
        }

        return EOr.from(decodedData);
    }

    private EOr<Optional<Long>> decodeTime(JsonObject obj) {
        return (!obj.has("time") || obj.get("time").isJsonNull()) ?
               EOr.from(Optional.empty()) :
               primitive("time", "Long", obj, JsonPrimitive::isNumber, JsonPrimitive::getAsLong).map(Optional::of);
    }

    private E expected(String key, String type) {
        return E.fromName("obj." + key).message("Expected: " + type);
    }

    private <A> EOr<A> primitive(String key,
                                 String type,
                                 JsonObject obj,
                                 Predicate<JsonPrimitive> predicate,
                                 Function<JsonPrimitive, A> getter) {
        E expected = expected(key, type);

        return EOr.catching(() -> obj.get(key).getAsJsonPrimitive(), t  -> expected)
                  .filter(predicate::test, j -> expected)
                  .map(getter);
    }
}
