package e;

import java.util.StringJoiner;

public abstract class AbstractJsonStringEncoder<Cause, Data> implements AbstractEncoder<AbstractE<Cause, Data>, String> {
    protected abstract String encodeCause(Cause cause);
    protected abstract String encodeData(Data data);

    @Override public String encode(AbstractE<Cause, Data> e) {
        StringJoiner joiner = new StringJoiner(",", "{", "}");

        if (e.hasName())    joiner.add(String.format("\"name\":\"%s\"",    escape(e.name())));
        if (e.hasMessage()) joiner.add(String.format("\"message\":\"%s\"", escape(e.message())));
        if (e.hasCode())    joiner.add(String.format("\"code\":%d",        e.code()));;
        if (e.hasCause())   joiner.add(String.format("\"cause\":%s",       encodeCause(e.cause())));
        if (e.hasData())    joiner.add(String.format("\"data\":%s",        encodeData(e.data())));

        return joiner.toString();
    }

    protected String escape(String s) {
        return s.replace("\"", "\\\"");
    }
}
