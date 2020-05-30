package e.gson;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import e.EOr;
import e.codec.Encoder;

import java.lang.reflect.Type;

public class EOrGsonEncoder implements Encoder<EOr<Object>, JsonElement>, JsonSerializer<EOr<Object>> {
    private final Gson gson;

    public EOrGsonEncoder(Gson gson) {
        this.gson = gson;
    }

    @Override public JsonElement encode(EOr<Object> eor) {
        return eor.fold(e -> EGsonCodec.get().encode(e), gson::toJsonTree);
    }

    @Override public JsonElement serialize(EOr<Object> eor, Type type, JsonSerializationContext ctx) {
        return encode(eor);
    }
}
