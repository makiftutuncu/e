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

import dev.akif.e.E;
import dev.akif.e.codec.Codec;
import dev.akif.e.codec.DecodingError;

public class EGsonAdapter implements Codec<JsonElement, JsonElement>,
                                     JsonDeserializer<E>,
                                     JsonSerializer<E> {
    @Override public E decodeOrThrow(JsonElement json) throws DecodingError {
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
            return E.of(code, name, message, null, data);
        } catch (Exception e) {
            // TODO: Write a message
            throw new DecodingError("", e);
        }
    }

    @Override public JsonElement encode(E e) {
        JsonObject obj = new JsonObject();
        obj.addProperty("code", e.getCode());
        if (e.hasName())    obj.addProperty("name",    e.getName());
        if (e.hasMessage()) obj.addProperty("message", e.getMessage());
        if (e.hasData())    obj.add("data", encodeData(e.getData()));
        return obj;
    }

    @Override public JsonElement serialize(E e, Type typeOfSrc, JsonSerializationContext context) {
        return encode(e);
    }

    @Override public E deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        try
        {
            return decodeOrThrow(json);
        } catch (DecodingError e) {
            throw new JsonParseException(e);
        }
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
