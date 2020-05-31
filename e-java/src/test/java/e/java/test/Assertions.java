package e.java.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import e.java.E;
import e.java.EOr;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class Assertions {
    private Assertions() {}

    public static void assertCode(E e, Integer code) {
        assertEquals(code != null,              e.hasCode());
        assertEquals(Optional.ofNullable(code), e.code());
    }

    public static void assertName(E e, String name) {
        assertEquals(name != null,              e.hasName());
        assertEquals(Optional.ofNullable(name), e.name());
    }

    public static void assertMessage(E e, String message) {
        assertEquals(message != null,              e.hasMessage());
        assertEquals(Optional.ofNullable(message), e.message());
    }

    public static void assertCauses(E e, List<E> causes) {
        assertEquals(!causes.isEmpty(), e.hasCause());
        assertEquals(causes,            e.causes());
    }

    public static void assertData(E e, Map<String, String> data) {
        assertEquals(!data.isEmpty(), e.hasData());
        assertEquals(data,            e.data());
    }

    public static void assertTime(E e, Long time) {
        assertEquals(time != null,              e.hasTime());
        assertEquals(Optional.ofNullable(time), e.time());
    }

    public static void assertAlmostSame(Long expected, Long actual, Long threshold) {
        assertTrue(
            Math.abs(actual - expected) < threshold,
            String.format(
                "Times were apart mode than %d ms, expected: %d, actual: %d",
                threshold,
                expected,
                actual
            )
        );
    }

    public static void assertAlmostSame(Long expected, Long actual) {
        assertAlmostSame(expected, actual, 1000L);
    }

    public static <A> void assertError(EOr<A> eor, E thatE) {
        eor.fold(
            thisE -> { assertEquals(thatE, thisE, "Expected: " + thisE.trace() + ", Actual: "+ thatE.trace()); return null; },
            a     -> fail(String.format("EOr did not contain error, it contained: %s", a))
        );
    }

    public static <A> void assertValue(EOr<A> eor, A thatA) {
        eor.fold(
            e     -> fail(String.format("EOr did not contain value, it contained: %s", e.trace())),
            thisA -> { assertEquals(thatA, thisA); return null; }
        );
    }
}

