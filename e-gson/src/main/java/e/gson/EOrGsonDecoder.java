package e.gson;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import e.E;
import e.EOr;
import e.codec.Decoder;

import java.lang.reflect.Type;

public class EOrGsonDecoder<A> implements Decoder<JsonElement, A>, JsonDeserializer<EOr<A>> {
    private final Gson gson;

    public EOrGsonDecoder(Gson gson) {
        this.gson = gson;
    }

    @Override public EOr<A> decode(JsonElement json) {
        return EOr.<A>catching(
            () -> gson.fromJson(json, (new TypeToken<A>() {}).getType()),
            t -> Decoder.decodingError.cause(E.fromThrowable(t))
        ).fold(
            aDecodingError -> EGsonCodec.get().decode(json).fold(eDecodingError -> aDecodingError.toEOr(), E::toEOr),
            EOr::from
        );
    }

    @Override public EOr<A> deserialize(JsonElement json, Type type, JsonDeserializationContext ctx) throws JsonParseException {
        return decode(json);
    }
}
