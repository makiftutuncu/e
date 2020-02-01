package dev.akif.e;

public abstract class AbstractE<Cause, Data> {
    public static final int EMPTY_CODE = 0;

    private final int _code;
    private final String _name;
    private final String _message;
    private final Cause _cause;
    private final Data _data;

    protected AbstractE(int code, String name, String message, Cause cause, Data data) {
        this._code    = code;
        this._name    = name;
        this._message = message;
        this._cause   = cause;
        this._data    = data;
    }

    public int code() {
        return _code;
    }

    public String name() {
        return _name;
    }

    public String message() {
        return _message;
    }

    public Cause cause() {
        return _cause;
    }

    public Data data() {
        return _data;
    }

    public boolean hasCode() {
        return code() != EMPTY_CODE;
    }

    public boolean hasName() {
        return name() != null && !name().trim().isEmpty();
    }

    public boolean hasMessage() {
        return message() != null && !message().trim().isEmpty();
    }

    public abstract boolean hasCause();

    public abstract boolean hasData();

    public abstract Exception toException();
}
