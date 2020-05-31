package e.gson;

import com.google.gson.*;
import e.E;
import e.EOr;
import e.codec.Decoder;
import e.codec.Encoder;

import java.lang.reflect.Type;

public class EOrGsonCodec<A> implements Decoder<JsonElement, A>,
                                        Encoder<EOr<A>, JsonElement>,
                                        JsonDeserializer<EOr<A>>,
                                        JsonSerializer<EOr<A>> {
    private final Gson gson;
    private final Class<A> klass;

    public EOrGsonCodec(Gson gson, Class<A> klass) {
        this.gson = gson;
        this.klass = klass;
    }

    @Override public EOr<A> decode(JsonElement json) {
        return EOr.catching(
            () -> gson.fromJson(json, klass),
            t -> Decoder.decodingError.cause(E.fromThrowable(t))
        );
    }

    @Override public JsonElement encode(EOr<A> eor) {
        return eor.fold(
            e -> EGsonCodec.get().encode(e),
            gson::toJsonTree
        );
    }

    @Override public EOr<A> deserialize(JsonElement json, Type type, JsonDeserializationContext ctx) throws JsonParseException {
        return decode(json);
    }

    @Override public JsonElement serialize(EOr<A> eor, Type type, JsonSerializationContext ctx) {
        return encode(eor);
    }
}
