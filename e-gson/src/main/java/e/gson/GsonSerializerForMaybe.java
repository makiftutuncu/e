package e.gson;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import e.java.Maybe;

public class GsonSerializerForMaybe implements JsonSerializer<Maybe<?>> {
    private final Gson gson = new Gson();

    private static GsonSerializerForMaybe instance;

    private GsonSerializerForMaybe() {}

    public static GsonSerializerForMaybe get() {
        if (instance == null) {
            instance = new GsonSerializerForMaybe();
        }

        return instance;
    }

    @Override public JsonElement serialize(Maybe<?> maybe, Type typeOfSrc, JsonSerializationContext ctx) {
        return maybe.fold(e -> GsonAdapterForE.get().encode(e), gson::toJsonTree);
    }
}
