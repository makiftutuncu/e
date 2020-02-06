package e.java;

import java.util.Map;
import java.util.StringJoiner;

public final class JsonStringEncoder implements Encoder<String> {
    private static JsonStringEncoder instance;

    private JsonStringEncoder() {}

    public static JsonStringEncoder get() {
        if (instance == null) {
            instance = new JsonStringEncoder();
        }

        return instance;
    }

    @Override public String encode(E e) {
        StringJoiner joiner = new StringJoiner(",", "{", "}");

        if (e.hasName())    joiner.add(String.format("\"name\":\"%s\"",    escape(e.name())));
        if (e.hasMessage()) joiner.add(String.format("\"message\":\"%s\"", escape(e.message())));
        if (e.hasCode())    joiner.add(String.format("\"code\":%d",        e.code()));;
        if (e.hasCause())   joiner.add(String.format("\"cause\":%s",       encodeCause(e)));
        if (e.hasData())    joiner.add(String.format("\"data\":%s",        encodeData(e)));

        return joiner.toString();
    }

    private String encodeData(E e) {
        StringJoiner joiner = new StringJoiner(",", "{", "}");

        for (Map.Entry<String, String> entry : e.data().entrySet()) {
            joiner.add(String.format("\"%s\":\"%s\"", escape(entry.getKey()), escape(entry.getValue())));
        }

        return joiner.toString();
    }

    private String encodeCause(E e) {
        return e.cause() == null ? "null" : String.format("\"%s\"", escape(e.cause().getMessage()));
    }

    private String escape(String s) {
        return s.replace("\"", "\\\"");
    }
}
