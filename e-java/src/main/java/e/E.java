package e;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/** A generic and immutable error, containing helpful information */
public final class E {
    /** A numeric code identifying this error */
    private final Integer code;

    /** A name identifying this error, usually enum-like */
    private final String name;

    /** A message about this error, usually human-readable */
    private final String message;

    /** Underlying cause(s) of this error, if any */
    private final List<E> causes;

    /** Arbitrary data related to this error as a key-value map */
    private final Map<String, String> data;

    /** Time when this error occurred as milliseconds, see {@link System#currentTimeMillis} */
    private final Long time;

    /**
     * Creates an E with all its properties
     *
     * @param code    {@link e.E#code}
     * @param name    {@link e.E#name}
     * @param message {@link e.E#message}
     * @param causes  {@link e.E#causes}
     * @param data    {@link e.E#data}
     * @param time    {@link e.E#time}
     */
    public E(Integer code, String name, String message, List<E> causes, Map<String, String> data, Long time) {
        this.code = code;
        this.name = name;
        this.message = message;
        this.causes = causes == null ? new LinkedList<>() : causes;
        this.data = data == null ? new LinkedHashMap<>() : data;
        this.time = time;
    }

    /** @return {@link e.E#code} or empty {@link java.util.Optional} if code is not set */
    public Optional<Integer> code() {
        return Optional.ofNullable(code);
    }

    /** @return {@link e.E#name} or empty {@link java.util.Optional} if name is not set */
    public Optional<String> name() {
        return Optional.ofNullable(name);
    }

    /** @return {@link e.E#message} or empty {@link java.util.Optional} if message is not set */
    public Optional<String> message() {
        return Optional.ofNullable(message);
    }

    /** @return {@link e.E#causes} or empty {@link java.util.List} if causes are not set */
    public List<E> causes() {
        return Collections.unmodifiableList(causes);
    }

    /** @return {@link e.E#data} or empty {@link java.util.Map} if data are not set */
    public Map<String, String> data() {
        return data;
    }

    /** @return {@link e.E#time} or empty {@link java.util.Optional} if time is not set */
    public Optional<Long> time() {
        return Optional.ofNullable(time);
    }

    /**
     * Constructs an E containing given code
     *
     * @param c A code
     *
     * @return A new E containing given code
     */
    public E code(int c) {
        return new E(c, name, message, causes, data, time);
    }

    /**
     * Constructs an E containing given name
     *
     * @param n A name
     *
     * @return A new E containing given name
     */
    public E name(String n) {
        return new E(code, n, message, causes, data, time);
    }

    /**
     * Constructs an E containing given message
     *
     * @param m Message to set
     *
     * @return A new E containing given message
     */
    public E message(String m) {
        return new E(code, name, m, causes, data, time);
    }

    /**
     * Constructs an E adding given causes
     *
     * @param c Causes as a List
     *
     * @return A new E containing causes of this E and given causes
     */
    public E causes(List<E> c) {
        List<E> newCauses = new LinkedList<>(causes);
        newCauses.addAll(c);

        return new E(code, name, message, newCauses, data, time);
    }

    /**
     * Constructs an E adding given causes
     *
     * @param c Causes as variable arguments
     *
     * @return A new E containing causes of this E and given causes
     */
    public E causes(E... c) {
        List<E> newCauses = new LinkedList<>(causes);
        newCauses.addAll(Arrays.asList(c));

        return new E(code, name, message, newCauses, data, time);
    }

    /**
     * Constructs an E adding given cause
     *
     * @param e A cause
     *
     * @return A new E containing causes of this E and given causes
     */
    public E cause(E e) {
        List<E> newCauses = new LinkedList<>(causes);
        newCauses.add(e);

        return new E(code, name, message, newCauses, data, time);
    }

    /**
     * Constructs an E adding given data
     *
     * @param d Data as a key-value map
     *
     * @return A new E containing data of this E and given data
     */
    public E data(Map<String, String> d) {
        Map<String, String> newData = new LinkedHashMap<>(data);
        newData.putAll(d);

        return new E(code, name, message, causes, newData, time);
    }

    /**
     * Constructs an E adding given data entry
     *
     * @param <V> Type of value of data entry
     *
     * @param k Key of data entry
     * @param v Value of data entry
     *
     * @return A new E containing data of this E and given data entry
     */
    public <V> E data(String k, V v) {
        Map<String, String> newData = new LinkedHashMap<>(data);
        newData.put(k, String.valueOf(v));

        return new E(code, name, message, causes, newData, time);
    }

    /**
     * Constructs an E containing given time
     *
     * @param t A time
     *
     * @return A new E containing given time
     */
    public E time(long t) {
        return new E(code, name, message, causes, data, t);
    }

    /**
     * Constructs an E containing time set to now
     *
     * @return A new E containing time set to now
     *
     * @see System#currentTimeMillis
     */
    public E now() {
        return new E(code, name, message, causes, data, System.currentTimeMillis());
    }

    /**
     * Constructs an E adding given cause if condition holds
     *
     * @param condition Some condition on which to add the cause
     * @param e         A cause
     *
     * @return A new E containing causes of this E and given cause or this E as is if condition doesn't hold
     */
    public E causeIf(boolean condition, Supplier<E> e) {
        if (condition) {
            List<E> newCauses = new LinkedList<>(causes);
            newCauses.add(e.get());

            return new E(code, name, message, newCauses, data, time);
        }

        return this;
    }

    /**
     * Whether or not a code is set
     */
    public boolean hasCode() {
        return code != null;
    }

    /**
     * Whether or not a name is set
     */
    public boolean hasName() {
        return name != null;
    }

    /**
     * Whether or not a message is set
     */
    public boolean hasMessage() {
        return message != null;
    }

    /**
     * Whether or not a cause is set
     */
    public boolean hasCause() {
        return !causes.isEmpty();
    }

    /**
     * Whether or not a data is set
     */
    public boolean hasData() {
        return !data.isEmpty();
    }

    /**
     * Whether or not a time is set
     */
    public boolean hasTime() {
        return time != null;
    }

    /**
     * A trace of this error and its causes as a String, like the stack trace of an [kotlin.Exception]
     *
     * @return Trace String of this E and its causes
     *
     * @see e.E#toString
     */
    public String trace() {
        class TraceBuilder {
            private String line(E e, int level) {
                StringBuilder prefix = new StringBuilder();
                for (int i = 0; i < level; i++) {
                    prefix.append("  ");
                }

                return prefix.append(e.toString()).toString();
            }

            private List<String> traverse(E e, int level) {
                List<String> strings = new LinkedList<>();
                List<String> causeStrings = e.causes
                    .stream()
                    .flatMap(cause -> traverse(cause, level + 1).stream())
                    .collect(Collectors.toList());

                strings.add(line(e, level));
                strings.addAll(causeStrings);

                return strings;
            }
        }

        List<String> strings = new TraceBuilder().traverse(this, 0);
        StringJoiner joiner = new StringJoiner("\n");
        strings.forEach(joiner::add);

        return joiner.toString();
    }

    /**
     * Converts this E to a failed EOr&lt;A&gt;
     *
     * @param <A> The A type in resulting EOr
     *
     * @return An EOr&lt;A&gt; containing this E
     *
     * @see e.EOr
     */
    public <A> EOr<A> toEOr() {
        return EOr.from(this);
    }

    /**
     * Converts this E into an exception
     *
     * @return An {@link e.EException} containing this E
     */
    public EException toException() {
        return new EException(this);
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof E)) return false;
        E that = (E) o;

        return Objects.equals(this.code, that.code) &&
               Objects.equals(this.name, that.name) &&
               Objects.equals(this.message, that.message) &&
               this.causes.equals(that.causes) &&
               this.data.equals(that.data) &&
               Objects.equals(this.time, that.time);
    }

    @Override public int hashCode() {
        return Objects.hash(code, name, message, causes, data, time);
    }

    @Override public String toString() {
        if (!hasCode() && !hasName() && !hasMessage() && !hasData()) {
            return "[Empty E]";
        }

        StringJoiner joiner = new StringJoiner(" | ");

        if (hasCode())    { joiner.add("E" + code); }
        if (hasName())    { joiner.add(name); }
        if (hasMessage()) { joiner.add(message); }

        if (hasData()) {
            StringJoiner dataJoiner = new StringJoiner(", ", "[", "]");
            data.forEach((k, v) -> dataJoiner.add(k + " -> " + v));
            joiner.add(dataJoiner.toString());
        }

        return joiner.toString();
    }

    /** An empty E */
    public static final E empty = new E(null, null, null, null, null, null);

    /**
     * Constructs an E containing given code
     *
     * @param c A code
     *
     * @return A new E containing given code
     */
    public static E fromCode(int c) {
        return new E(c, null, null, null, null, null);
    }

    /**
     * Constructs an E containing given name
     *
     * @param n A name
     *
     * @return A new E containing given name
     */
    public static E fromName(String n) {
        return new E(null, n, null, null, null, null);
    }

    /**
     * Constructs an E containing given message
     *
     * @param m Message to set
     *
     * @return A new E containing given message
     */
    public static E fromMessage(String m) {
        return new E(null, null, m, null, null, null);
    }

    /**
     * Constructs an E adding given causes
     *
     * @param c Causes as a List
     *
     * @return A new E containing causes of this E and given causes
     */
    public static E fromCauses(List<E> c) {
        return new E(null, null, null, c, null, null);
    }

    /**
     * Constructs an E adding given causes
     *
     * @param c Causes as variable arguments
     *
     * @return A new E containing causes of this E and given causes
     */
    public static E fromCauses(E... c) {
        return new E(null, null, null, Arrays.asList(c), null, null);
    }

    /**
     * Constructs an E adding given cause
     *
     * @param e A cause
     *
     * @return A new E containing causes of this E and given causes
     */
    public static E fromCause(E e) {
        return new E(null, null, null, Collections.singletonList(e), null, null);
    }

    /**
     * Constructs an E adding given data
     *
     * @param d Data as a key-value map
     *
     * @return A new E containing data of this E and given data
     */
    public static E fromData(Map<String, String> d) {
        return new E(null, null, null, null, d, null);
    }

    /**
     * Constructs an E adding given data entry
     *
     * @param <V> Type of value of data entry
     *
     * @param k Key of data entry
     * @param v Value of data entry
     *
     * @return A new E containing data of this E and given data entry
     */
    public static <V> E fromData(String k, V v) {
        Map<String, String> data = new LinkedHashMap<>();
        data.put(k, String.valueOf(v));

        return new E(null, null, null, null, data, null);
    }

    /**
     * Constructs an E containing given time
     *
     * @param t A time
     *
     * @return A new E containing given time
     */
    public static E fromTime(Long t) {
        return new E(null, null, null, null, null, t);
    }

    /**
     * Constructs an E containing time set to now
     *
     * @return A new E containing time set to now
     *
     * @see System#currentTimeMillis
     */
    public static E fromNow() {
        return new E(null, null, null, null, null, System.currentTimeMillis());
    }

    /**
     * Constructs an E adding given cause if condition holds
     *
     * @param condition Some condition on which to add the cause
     * @param e         A cause
     *
     * @return A new E containing causes of this E and given cause or this E as is if condition doesn't hold
     */
    public static E fromCauseIf(boolean condition, Supplier<E> e) {
        if (condition) {
            List<E> causes = new LinkedList<>();
            causes.add(e.get());

            return new E(null, null, null, causes, null, null);
        }

        return empty;
    }

    /**
     * Constructs an E from given [kotlin.Throwable]
     *
     * @param throwable A Throwable
     *
     * @return A new E containing message of given Throwable or wrapped E in EException if Throwable is one
     */
    public static E fromThrowable(Throwable throwable) {
        if (throwable instanceof EException) {
            return ((EException) throwable).e;
        }

        return fromMessage(throwable.getMessage());
    }
}
