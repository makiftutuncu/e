package e;

import static e.test.Assertions.*;
import static e.test.Helpers.*;

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

    @Test void gettingTraceOfAnE() {
        E e9 = E.fromCode(9);
        E e8 = E.fromMessage("Test 8");
        E e7 = E.fromName("test7");
        E e6 = E.fromName("test6").message("Test 6");
        E e5 = E.fromCode(5).message("Test 5");
        E e4 = E.fromCode(4).name("test4");
        E e3 = E.fromCode(3).name("test3").message("Test 3");
        E e2 = E.fromCode(2).name("test2").message("Test 2").causes(listOf(e3, E.empty));
        E e = new E(1, "test1", "Test 1", listOf(e2, e4, e5, e6, e7, e8, e9), mapOf(mapEntry("foo", "bar")), null);

        String expected =
            "E1 | test1 | Test 1 | [foo -> bar]\n" +
            "  E2 | test2 | Test 2\n" +
            "    E3 | test3 | Test 3\n" +
            "    [Empty E]\n" +
            "  E4 | test4\n" +
            "  E5 | Test 5\n" +
            "  test6 | Test 6\n" +
            "  test7\n" +
            "  Test 8\n" +
            "  E9";

        String actual = e.trace();

        assertEquals(expected, actual);
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

    @Test void convertingAnEToAString() {
        E e9 = E.fromCode(9);
        E e8 = E.fromMessage("Test 8");
        E e7 = E.fromName("test7");
        E e6 = E.fromName("test6").message("Test 6");
        E e5 = E.fromCode(5).message("Test 5");
        E e4 = E.fromCode(4).name("test4");
        E e3 = E.fromCode(3).name("test3").message("Test 3");
        E e2 = E.fromCode(2).name("test2").message("Test 2").causes(listOf(e3));
        E e1 = new E(1, "test1", "Test 1", listOf(e2, e4, e5, e6, e7, e8, e9), mapOf(mapEntry("foo", "bar")), null);

        assertEquals("[Empty E]", E.empty.toString());
        assertEquals("E1 | test1 | Test 1 | [foo -> bar]", e1.toString());
        assertEquals("E2 | test2 | Test 2", e2.toString());
        assertEquals("E3 | test3 | Test 3", e3.toString());
        assertEquals("E4 | test4", e4.toString());
        assertEquals("E5 | Test 5", e5.toString());
        assertEquals("test6 | Test 6", e6.toString());
        assertEquals("test7", e7.toString());
        assertEquals("Test 8", e8.toString());
        assertEquals("E9", e9.toString());
    }
}
