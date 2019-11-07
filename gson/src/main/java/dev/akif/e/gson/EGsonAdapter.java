package dev.akif.e.gson;

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

import dev.akif.e.DecoderE;
import dev.akif.e.DecodingFailure;
import dev.akif.e.E;
import dev.akif.e.EncoderE;

public class EGsonAdapter implements EncoderE<JsonElement>,
                                     DecoderE<JsonElement>,
                                     JsonSerializer<E>,
                                     JsonDeserializer<E> {
    @Override public JsonElement encode(E e) {
        JsonObject obj = new JsonObject();
        if (e.hasCode())    obj.addProperty("code",    e.code);
        if (e.hasName())    obj.addProperty("name",    e.name);
        if (e.hasMessage()) obj.addProperty("message", e.message);
        if (e.hasCause())   obj.addProperty("cause",   e.cause.getMessage());
        if (e.hasData())    obj.add("data", encodeData(e.data));
        return obj;
    }

    @Override public E decodeOrThrow(JsonElement json) {
        try {
            JsonObject obj            = json.getAsJsonObject();
            JsonPrimitive codeJson    = obj.getAsJsonPrimitive("code");
            JsonPrimitive nameJson    = obj.getAsJsonPrimitive("name");
            JsonPrimitive messageJson = obj.getAsJsonPrimitive("message");
            JsonObject dataJson       = obj.getAsJsonObject("data");
            int code                  = codeJson    != null ? codeJson.getAsInt()       : 0;
            String name               = nameJson    != null ? nameJson.getAsString()    : "";
            String message            = messageJson != null ? messageJson.getAsString() : "";
            Map<String, String> data  = dataJson    != null ? decodeData(dataJson)      : new HashMap<>();
            return E.of(code, name, message, data);
        } catch (Exception e) {
            throw new DecodingFailure("", e);
        }
    }

    @Override public JsonElement serialize(E e, Type typeOfSrc, JsonSerializationContext context) {
        return encode(e);
    }

    @Override public E deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        return decodeOrThrow(json);
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
            data.put(entry.getKey(),entry.getValue().getAsString());
        }
        return data;
    }
}
