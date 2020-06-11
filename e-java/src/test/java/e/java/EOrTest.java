package e.java;

import static e.java.test.Assertions.*;
import static e.java.test.Helpers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class EOrTest {
    @Test void constructingAnEOr() {
        assertValue(EOr.unit, null);

        E e = E.fromName("test").message("Test");
        assertError(EOr.from(e), e);
        assertError(e.toEOr(), e);

        assertValue(EOr.from("test"), "test");

        String nullable1 = null;
        String nullable2 = "test";
        E eNull = E.fromName("null");
        assertError(EOr.fromNullable(nullable1, () -> eNull), eNull);
        assertValue(EOr.fromNullable(nullable2, () -> eNull), "test");

        Optional<String> optional1 = Optional.empty();
        Optional<String> optional2 = Optional.of("test");
        E eEmpty = E.fromName("null");
        assertError(EOr.fromOptional(optional1, () -> eEmpty), eEmpty);
        assertValue(EOr.fromOptional(optional2, () -> eEmpty), "test");

        UnsafeSupplier<String> t1 = () -> { throw new Exception("test"); };
        UnsafeSupplier<String> t2 = () -> "Test";
        assertError(EOr.catching(t1, E::fromThrowable), E.fromMessage("test"));
        assertValue(EOr.catching(t2, E::fromThrowable), "Test");
    }

    @Test void mappingAnEOr() {
        E failed = E.fromName("failed");

        assertError(failed.toEOr().map(String::valueOf), failed);
        assertValue(EOr.from(42).map(String::valueOf), "42");
    }

    @Test void flatMappingAnEOr() {
        E failed1 = E.fromName("failed1");
        E failed2 = E.fromName("failed2");

        assertError(failed1.toEOr().flatMap(i -> failed2.toEOr()), failed1);
        assertError(failed1.toEOr().flatMap(i -> EOr.from(String.valueOf(i))), failed1);

        assertError(EOr.from(42).flatMap(i -> failed2.toEOr()), failed2);
        assertValue(EOr.from(42).flatMap(i -> EOr.from(String.valueOf(i))), "42");
    }

    @Test void mappingErrorOfAnEOr() {
        assertValue(EOr.from(42).mapError(e -> e.code(1)), 42);

        assertError(E.fromName("test").toEOr().mapError(e -> e.name((e.name().orElse("")).toUpperCase())), E.fromName("TEST"));
    }

    @Test void flatMappingErrorOfAnEOr() {
        assertValue(EOr.from(42).flatMapError(e -> e.code(1).toEOr()), 42);
        assertValue(EOr.from(42).flatMapError(e -> EOr.from(43)), 42);

        assertValue(E.fromName("test").toEOr().flatMapError(e -> EOr.from(42)), 42);
        assertError(E.fromName("test").toEOr().flatMapError(e -> e.code(1).toEOr()), E.fromCode(1).name("test"));
    }

    @Test void foldingAnEOr() {
        assertEquals("",  E.empty.<Integer>toEOr().fold(e -> e.code().map(String::valueOf).orElse(""), String::valueOf));
        assertEquals("1", E.fromCode(1).<Integer>toEOr().fold(e -> e.code().map(String::valueOf).orElse(""), String::valueOf));

        assertEquals("42", EOr.from(42).fold(e -> e.code().map(String::valueOf).orElse(""), String::valueOf));
    }

    @Test void gettingValueOfAnEOrOrADefaultValue() {
        assertEquals("", E.fromCode(1).<String>toEOr().getOrElse(() -> ""));

        assertEquals("test", EOr.from("test").getOrElse(() -> ""));
    }

    @Test void gettingAnEOrOrAnAlternativeOneOnError() {
        E e1 = E.fromCode(1);
        E e2 = E.fromCode(2);

        assertError(e1.toEOr().orElse(e2::toEOr), e2);

        assertValue(EOr.from("test").orElse(e2::toEOr), "test");
        assertValue(e1.toEOr().orElse(() -> EOr.from("test")), "test");
    }

    @Test void gettingAnEOrOrANextOneOnValue() {
        E e1 = E.fromCode(1);
        E e2 = E.fromCode(2);

        assertError(e1.toEOr().andThen(e2::toEOr), e1);
        assertError(e1.toEOr().andThen(() -> EOr.from("test")), e1);

        assertError(EOr.from("test").andThen(e2::toEOr), e2);
        assertValue(EOr.from("test").andThen(() -> EOr.from(42)), 42);
    }

    @Test void performingSideEffectOnAnEOrOnError() {
        final AtomicInteger counter = new AtomicInteger(0);

        EOr.from("test").onError(i -> counter.getAndIncrement());
        assertEquals(0, counter.get());

        listOf(E.fromName("test1"), E.fromName("test2"), E.fromName("test3")).forEach(e -> e.toEOr().onError(s -> counter.getAndIncrement()));
        assertEquals(3, counter.get());
    }

    @Test void performingSideEffectOnAnEOrOnValue() {
        E e = E.fromCode(1);
        final AtomicInteger counter = new AtomicInteger(0);

        e.toEOr().onValue(i -> counter.getAndIncrement());
        assertEquals(0, counter.get());

        listOf("test1", "test2", "test3").forEach(t -> EOr.from(t).onValue(s -> counter.getAndIncrement()));
        assertEquals(3, counter.get());
    }

    @Test void performingSideEffectOnAnEOrUsingForEach() {
        E e = E.fromCode(1);
        final AtomicInteger counter = new AtomicInteger(0);

        e.toEOr().forEach(i -> counter.getAndIncrement());
        assertEquals(0, counter.get());

        listOf("test1", "test2", "test3").forEach(t -> EOr.from(t).forEach(s -> counter.getAndIncrement()));
        assertEquals(3, counter.get());
    }

    @Test void filteringAnEOr() {
        E e = E.fromCode(1);
        E negative = E.fromName("negative");

        assertError(e.<Integer>toEOr().filter(i -> i > 0), e);
        assertError(e.<Integer>toEOr().filter(i -> i > 0, i -> negative), e);

        assertValue(EOr.from(42).filter(i -> i > 0), 42);
        assertError(EOr.from(-42).filter(i -> i > 0), EOr.filteredError.data("value", -42));

        assertValue(EOr.from(42).filter(i -> i > 0, i -> negative.data("value", i)), 42);
        assertError(EOr.from(-42).filter(i -> i > 0, i -> negative.data("value", i)), negative.data("value", -42));
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Test void equalityAndHashCodeOfEOr() {
        EOr<String> eor1 = EOr.from("test1");
        EOr<String> eor2 = EOr.from("test2");

        assertEquals(EOr.from("test1"), eor1);
        assertEquals(EOr.from("test1").hashCode(), eor1.hashCode());

        assertNotEquals(eor1, eor2);
        assertNotEquals(eor1.hashCode(), eor2.hashCode());

        E e1 = new E(1, "test", "Test", listOf(E.fromName("test")), mapOf(mapEntry("test", "test")), 123456789L);

        List<E> differentEs = listOf(
            new E(null, e1.name().get(), e1.message().get(), e1.causes(), e1.data(), e1.time().get()),
            e1.code(2),
            new E(e1.code().get(), null, e1.message().get(), e1.causes(), e1.data(), e1.time().get()),
            e1.name("test2"),
            new E(e1.code().get(), e1.name().get(), null, e1.causes(), e1.data(), e1.time().get()),
            e1.message("Test2"),
            new E(e1.code().get(), e1.name().get(), e1.message().get(), null, e1.data(), e1.time().get()),
            e1.cause(E.empty),
            new E(e1.code().get(), e1.name().get(), e1.message().get(), e1.causes(), null, e1.time().get()),
            e1.data("foo", "bar"),
            new E(e1.code().get(), e1.name().get(), e1.message().get(), e1.causes(), e1.data(), null),
            e1.now()
        );

        differentEs.forEach(e2 -> {
            EOr<String> eor3 = e1.toEOr();
            EOr<String> eor4 = e2.toEOr();

            assertNotEquals(eor1, eor3);
            assertNotEquals(eor1.hashCode(), eor3.hashCode());

            assertNotEquals(eor1, eor4);
            assertNotEquals(eor1.hashCode(), eor4.hashCode());
        });
    }

    @Test void convertingAnEOrToString() {
        assertEquals("test", E.fromName("test").toEOr().toString());
        assertEquals("42", EOr.from(42).toString());
    }
}
