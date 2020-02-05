package e;

public abstract class AbstractE<Cause, Data> {
    public static final int EMPTY_CODE = 0;

    private final String _name;
    private final String _message;
    private final int _code;
    private final Cause _cause;
    private final Data _data;

    protected AbstractE(String name, String message, int code, Cause cause, Data data) {
        this._name    = name;
        this._message = message;
        this._code    = code;
        this._cause   = cause;
        this._data    = data;
    }

    public String name() {
        return _name;
    }

    public String message() {
        return _message;
    }

    public int code() {
        return _code;
    }

    public Cause cause() {
        return _cause;
    }

    public Data data() {
        return _data;
    }

    public abstract AbstractE<Cause, Data> name(String name);

    public abstract AbstractE<Cause, Data> message(String message);

    public abstract AbstractE<Cause, Data> code(int code);

    public abstract AbstractE<Cause, Data> cause(Cause cause);

    public abstract AbstractE<Cause, Data> data(Data data);

    public boolean hasName() {
        return !isBlankString(name());
    }

    public boolean hasMessage() {
        return !isBlankString(message());
    }

    public boolean hasCode() {
        return code() != EMPTY_CODE;
    }

    public abstract boolean hasCause();

    public abstract boolean hasData();

    public abstract Exception toException();

    protected static boolean isBlankString(String s) {
        return s == null || s.trim().isEmpty();
    }
}
