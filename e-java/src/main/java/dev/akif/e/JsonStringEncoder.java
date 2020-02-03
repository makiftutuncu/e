package dev.akif.e;

import java.util.Map;
import java.util.StringJoiner;

public final class JsonStringEncoder extends AbstractJsonStringEncoder<Throwable, Map<String, String>> {
    private static JsonStringEncoder instance;

    private JsonStringEncoder() {}

    public static JsonStringEncoder get() {
        if (instance == null) {
            instance = new JsonStringEncoder();
        }

        return instance;
    }

    @Override protected String encodeCause(Throwable cause)
    {
        return cause == null ? "null" : String.format("\"%s\"", escape(cause.getMessage()));
    }

    @Override protected String encodeData(Map<String, String> data) {
        StringJoiner joiner = new StringJoiner(",", "{", "}");

        for (Map.Entry<String, String> entry : data.entrySet()) {
            joiner.add(String.format("\"%s\":\"%s\"", escape(entry.getKey()), escape(entry.getValue())));
        }

        return joiner.toString();
    }
}
