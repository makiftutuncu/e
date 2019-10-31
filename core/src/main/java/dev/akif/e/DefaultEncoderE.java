package dev.akif.e;

import java.util.Map;
import java.util.StringJoiner;

public final class DefaultEncoderE implements EncoderE<String> {
    private static DefaultEncoderE instance;

    private DefaultEncoderE() {}

    public static DefaultEncoderE get() {
        if (instance ==  null) {
            instance = new DefaultEncoderE();
        }

        return instance;
    }

    @Override public String encode(E e) {
        StringJoiner joiner = new StringJoiner(",", "{", "}");

        if (e.hasCode())    { joiner.add(String.format("\"code\":%d",        e.code)); }
        if (e.hasName())    { joiner.add(String.format("\"name\":\"%s\"",    escape(e.name))); }
        if (e.hasMessage()) { joiner.add(String.format("\"message\":\"%s\"", escape(e.message))); }
        if (e.hasCause())   { joiner.add(String.format("\"cause\":\"%s\"",   escape(e.cause.getMessage()))); }
        if (e.hasData())    { joiner.add(String.format("\"data\":%s",        makeDataString(e.data))); }

        return joiner.toString();
    }

    String escape(String s) {
        return s.replaceAll("\"", "\\\\\"");
    }

    String makeDataString(Map<String, String> data) {
        StringJoiner joiner = new StringJoiner(",", "{", "}");
        data.forEach((key, value) -> joiner.add(String.format("\"%s\":\"%s\"", escape(key), escape(value))));
        return joiner.toString();
    }
}
