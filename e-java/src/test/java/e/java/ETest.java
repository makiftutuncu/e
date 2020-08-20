package e.java;

import static e.java.test.Assertions.*;
import static e.java.test.Helpers.*;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ETest {
    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Test void constructingAnE() {
        E empty = E.empty;
        assertCode(empty, null);
        assertName(empty, null);
        assertMessage(empty, null);
        assertCauses(empty, listOf());
        assertData(empty, mapOf());
        assertTime(empty, null);

        int code                 = 1;
        String name              = "test";
        String message           = "Test";
        List<E> causes           = listOf(E.fromName("cause1"), E.fromName("cause2"));
        Map<String, String> data = mapOf(mapEntry("foo", "bar"));
        long time                = 123456789L;

        E e = new E(code, name, message, causes, data, time);

        int code2 = 2;

        assertCode(e, code);
        assertCode(new E(code, null, null, null, null, null), code);
        assertCode(E.fromCode(code), code);
        assertCode(e.code(code2), code2);

        String name2 = "test2";

        assertName(e, name);
        assertName(new E(null, name, null, null, null, null), name);
        assertName(E.fromName(name), name);
        assertName(e.name(name2), name2);

        String message2 = "Test 2";

        assertMessage(e, message);
        assertMessage(new E(null, null, message, null, null, null), message);
        assertMessage(E.fromMessage(message), message);
        assertMessage(e.message(message2), message2);

        E cause3 = E.fromName("cause3");

        assertCauses(e, causes);
        assertCauses(new E(null, null, null, causes, null, null), causes);
        assertCauses(E.fromCauses(causes), causes);
        assertCauses(e.causes(listOf(cause3)), added(causes, cause3));

        assertCauses(e.cause(cause3), added(causes, cause3));
        assertCauses(E.fromCause(cause3), listOf(cause3));

        assertCauses(e.causeIf(true, () -> cause3), added(causes, cause3));
        assertCauses(e.causeIf(false, () -> cause3), causes);
        assertCauses(E.fromCauseIf(true, () -> cause3), listOf(cause3));
        assertCauses(E.fromCauseIf(false,  () -> cause3), listOf());

        assertData(e, data);
        assertData(new E(null, null, null, null, data, null), data);
        assertData(E.fromData(data), data);

        String key = "test";
        int value  = 42;

        assertData(e.data(key, value), added(data, key, String.valueOf(value)));
        assertData(E.fromData(key, value), mapOf(mapEntry(key, String.valueOf(value))));

        long now = System.currentTimeMillis();

        assertTime(e, time);
        assertTime(new E(null, null, null, null, null, time), time);
        assertTime(E.fromTime(time), time);
        assertTime(e.time(now), now);

        assertAlmostSame(now, e.now().time().get());
        assertAlmostSame(now, E.fromNow().time().get());
    }

    @Test void convertingAnEToAnEOr() {
        E e             = E.fromName("test").message("Test");
        EOr<String> eor = e.toEOr();

        assertError(eor, e);
    }

    @Test void convertingAnEToAnEException() {
        E e          = E.fromName("test").message("Test");
        Exception ex = e.toException();

        assertEquals(new EException(e), ex);
    }

    @Test void constructingAnEFromAThrowable() {
        assertEquals(E.fromMessage("Test"), E.fromThrowable(new Exception("Test")));
        assertEquals(E.fromName("test"), E.fromThrowable(E.fromName("test").toException()));
    }
}
