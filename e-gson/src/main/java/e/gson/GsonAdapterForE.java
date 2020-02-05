package e.gson;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import e.java.Codec;
import e.java.E;

public class GsonAdapterForE implements Codec<JsonElement>, JsonSerializer<E>, JsonDeserializer<E> {
    private static GsonAdapterForE instance;

    private GsonAdapterForE() {}

    public static GsonAdapterForE get() {
        if (instance == null) {
            instance = new GsonAdapterForE();
        }

        return instance;
    }

    @Override public JsonElement encode(E e) {
        JsonObject obj = new JsonObject();

        if (e.hasName())    obj.addProperty("name",    e.name());
        if (e.hasMessage()) obj.addProperty("message", e.message());
        if (e.hasCode())    obj.addProperty("code",    e.code());
        if (e.hasCause())   obj.addProperty("cause",   e.cause().getMessage());
        if (e.hasData())    obj.add("data", encodeData(e.data()));

        return obj;
    }

    @Override public DecodingResult<E> decode(JsonElement json) {
        try {
            JsonObject obj            = json.getAsJsonObject();
            JsonPrimitive nameJson    = obj.getAsJsonPrimitive("name");
            JsonPrimitive messageJson = obj.getAsJsonPrimitive("message");
            JsonPrimitive codeJson    = obj.getAsJsonPrimitive("code");
            JsonObject dataJson       = obj.getAsJsonObject("data");

            String name               = nameJson    != null ? nameJson.getAsString()    : "";
            String message            = messageJson != null ? messageJson.getAsString() : "";
            int code                  = codeJson    != null ? codeJson.getAsInt()       : 0;
            Map<String, String> data  = dataJson    != null ? decodeData(dataJson)      : new HashMap<>();

            // Cannot know cause field because it isn't possible to construct the causing exception from just a serialized message string
            return DecodingResult.succeed(new E(name, message, code, null, data));
        } catch (Exception cause) {
            E e = new E("decoding-failure", "Cannot decode as E!").cause(cause).data("input", json.toString());

            return DecodingResult.fail(e);
        }
    }

    @Override public JsonElement serialize(E e, Type typeOfSrc, JsonSerializationContext context) {
        return encode(e);
    }

    @Override public E deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        DecodingResult<E> result = decode(json);

        if (!result.isSuccess) {
            throw new JsonParseException(result.get().toException());
        }

        return result.get();
    }

    private JsonElement encodeData(Map<String, String> data) {
        JsonObject obj = new JsonObject();

        for (Map.Entry<String, String> entry : data.entrySet()) {
            obj.addProperty(entry.getKey(), entry.getValue());
        }

        return obj;
    }

    private Map<String, String> decodeData(JsonObject obj) {
        Map<String, String> data = new HashMap<>();

        for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
            data.put(entry.getKey(), entry.getValue().getAsString());
        }

        return data;
    }
}
