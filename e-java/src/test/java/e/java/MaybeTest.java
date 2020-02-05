package e.java;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import e.AbstractE;

public class MaybeTest {
    @Test void testConstructingFailure() {
        E e = new E("test");

        Maybe<String> maybe1 = Maybe.failure(e);

        assertFalse(maybe1.isSuccess());
        assertEquals(maybe1.eOptional().orElse(null), e);
        assertFalse(maybe1.valueOptional().isPresent());

        Maybe<Integer> maybe2 = e.toMaybe();

        assertFalse(maybe2.isSuccess());
        assertEquals(maybe2.eOptional().orElse(null), e);
        assertFalse(maybe2.valueOptional().isPresent());
    }

    @Test void testConstructingByCatching() {
        E e = new E("test");

        Maybe<String> maybe1 = Maybe.catching(() -> { throw new Exception("Test Exception"); }, e::cause);

        assertFalse(maybe1.isSuccess());
        assertEquals(maybe1.eOptional().map(e2 -> e2.cause().getMessage()).orElse(""), "Test Exception");
        assertFalse(maybe1.valueOptional().isPresent());

        Maybe<String> maybe2 = Maybe.catching(() -> "test", e::cause);

        assertTrue(maybe2.isSuccess());
        assertFalse(maybe2.eOptional().isPresent());
        assertEquals(maybe2.valueOptional().orElse(""), "test");
    }

    @Test void testConstructingSuccess() {
        Maybe<String> maybe = Maybe.success("test");

        assertTrue(maybe.isSuccess());
        assertFalse(maybe.eOptional().isPresent());
        assertEquals(maybe.valueOptional().orElse(""), "test");
    }

    @Test void testMapping() {
        Maybe<String> maybe1 = Maybe.failure(new E("test"));
        Maybe<String> maybe2 = Maybe.success("test");

        assertEquals(Maybe.failure(new E("test")), maybe1.map(String::toUpperCase));
        assertEquals(Maybe.success("TEST"), maybe2.map(String::toUpperCase));
    }

    @Test void testFlatMapping() {
        Maybe<String> maybe1 = Maybe.failure(new E("test-1"));
        Maybe<String> maybe2 = Maybe.success("test");

        assertEquals(Maybe.failure(new E("test-1")), maybe1.flatMap(s -> Maybe.success(s.toUpperCase())));
        assertEquals(Maybe.failure(new E("test-2")), maybe2.flatMap(s -> Maybe.failure(new E("test-2"))));
        assertEquals(Maybe.success("TEST"),   maybe2.flatMap(s -> Maybe.success(s.toUpperCase())));
    }

    @Test void testFolding() {
        Maybe<String> maybe1 = Maybe.failure(new E("error"));
        Maybe<String> maybe2 = Maybe.success("test");

        assertEquals("error", maybe1.fold(AbstractE::name, s -> s));
        assertEquals("test",  maybe2.fold(AbstractE::name, s -> s));
    }

    @Test void testGettingWithDefault() {
        Maybe<String> maybe1 = Maybe.failure(new E("test"));
        Maybe<String> maybe2 = Maybe.success("test");

        assertEquals("error", maybe1.getOrElse("error"));
        assertEquals("test",  maybe2.getOrElse("error"));
    }

    @Test void testGettingAlternative() {
        Maybe<String> maybe1 = Maybe.failure(new E("test-1"));
        Maybe<String> maybe2 = Maybe.failure(new E("test-2"));
        Maybe<String> maybe3 = Maybe.success("test");

        assertEquals(Maybe.failure(new E("test-2")), maybe1.orElse(maybe2));
        assertEquals(Maybe.success("test"),   maybe1.orElse(maybe3));
    }

    @Test void testEquality() {
        Maybe<String> maybe1 = Maybe.failure(new E("test-1"));
        Maybe<String> maybe2 = Maybe.failure(new E("test-1"));
        Maybe<String> maybe3 = Maybe.failure(new E("test-2"));
        Maybe<String> maybe4 = Maybe.success("test");
        Maybe<String> maybe5 = Maybe.success("test");
        Maybe<String> maybe6 = Maybe.success("TEST");

        assertEquals(maybe2,    maybe1);
        assertNotEquals(maybe3, maybe1);
        assertNotEquals(maybe4, maybe1);

        assertEquals(maybe5,    maybe4);
        assertNotEquals(maybe6, maybe4);
        assertNotEquals(maybe1, maybe4);
    }

    @Test void testHashCodeGeneration() {
        Maybe<String> maybe1 = Maybe.failure(new E("test-1"));
        Maybe<String> maybe2 = Maybe.failure(new E("test-1"));
        Maybe<String> maybe3 = Maybe.failure(new E("test-2"));
        Maybe<String> maybe4 = Maybe.success("test");
        Maybe<String> maybe5 = Maybe.success("test");
        Maybe<String> maybe6 = Maybe.success("TEST");

        assertEquals(maybe2.hashCode(),    maybe1.hashCode());
        assertNotEquals(maybe3.hashCode(), maybe1.hashCode());
        assertNotEquals(maybe4.hashCode(), maybe1.hashCode());

        assertEquals(maybe5.hashCode(),    maybe4.hashCode());
        assertNotEquals(maybe6.hashCode(), maybe4.hashCode());
        assertNotEquals(maybe1.hashCode(), maybe4.hashCode());
    }

    @Test void testToString() {
        Maybe<String> maybe1 = Maybe.failure(new E("test"));
        Maybe<String> maybe2 = Maybe.success("test");

        assertEquals("{\"name\":\"test\"}", maybe1.toString());
        assertEquals("test",         maybe2.toString());
    }
}
