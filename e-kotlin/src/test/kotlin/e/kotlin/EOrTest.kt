package e.kotlin

import e.kotlin.test.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.lang.Exception

object EOrTest: Assertions {
    @Test fun `constructing an EOr`() {
        EOr.unit.assertValue(Unit)

        val e = E.name("test").message("Test")
        EOr.from<String>(e).assertError(e)
        e.toEOr<String>().assertError(e)

        EOr.from("test").assertValue("test")
        "test".toEOr().assertValue("test")

        val nullable1: String? = null
        val nullable2: String? = "test"
        val eNull = E.name("null")
        EOr.fromNullable(nullable1) { eNull }.assertError(eNull)
        nullable1.toEOr { eNull }.assertError(eNull)
        nullable2.toEOr { eNull }.assertValue("test")

        val t1: () -> String = { throw Exception("test") }
        val t2: () -> String = { "Test" }
        EOr.catching(t1) { it.toE() }.assertError(E.message("test"))
        t1.catching { it.toE() }.assertError(E.message("test"))
        EOr.catching(t2) { it.toE() }.assertValue("Test")
        t2.catching { it.toE() }.assertValue("Test")
    }

    @Test fun `mapping an EOr`() {
        val failed = E.name("failed")

        failed.toEOr<Int>().map { it.toString() }.assertError(failed)
        42.toEOr().map { it.toString() }.assertValue("42")
    }

    @Test fun `flat mapping an EOr`() {
        val failed1 = E.name("failed1")
        val failed2 = E.name("failed2")

        failed1.toEOr<Int>().flatMap { failed2.toEOr<String>() }.assertError(failed1)
        failed1.toEOr<Int>().flatMap { it.toString().toEOr() }.assertError(failed1)

        42.toEOr().flatMap { failed2.toEOr<String>() }.assertError(failed2)
        42.toEOr().flatMap { it.toString().toEOr() }.assertValue("42")
    }

    @Test fun `mapping error of an EOr`() {
        42.toEOr().mapError { it.code(1) }.assertValue(42)

        E.name("test").toEOr<Int>().mapError { it.name((it.name ?: "").uppercase()) }.assertError(E.name("TEST"))
    }

    @Test fun `flat mapping error of an EOr`() {
        42.toEOr().flatMapError { it.code(1).toEOr() }.assertValue(42)
        42.toEOr().flatMapError { 43.toEOr() }.assertValue(42)

        E.name("test").toEOr<Int>().flatMapError { 42.toEOr() }.assertValue(42)
        E.name("test").toEOr<Int>().flatMapError { it.code(1).toEOr() }.assertError(E.code(1).name("test"))
    }

    @Test fun `folding an EOr`() {
        assertEquals("",  E.empty.toEOr<Int>().fold({ it.code?.toString() ?: "" }, { it.toString() }))
        assertEquals("1", E.code(1).toEOr<Int>().fold({ it.code?.toString() ?: "" }, { it.toString() }))

        assertEquals("42", 42.toEOr().fold({ it.code?.toString() ?: "" }, { it.toString() }))
    }

    @Test fun `getting value of an EOr or a default value`() {
        assertEquals("", E.code(1).toEOr<String>().getOrElse { "" })

        assertEquals("test", "test".toEOr().getOrElse { "" })
    }

    @Test fun `getting an EOr or an alternative one on error`() {
        val e1 = E.code(1)
        val e2 = E.code(2)

        e1.toEOr<String>().orElse { e2.toEOr() }.assertError(e2)

        "test".toEOr().orElse { e2.toEOr() }.assertValue("test")
        e1.toEOr<String>().orElse { "test".toEOr() }.assertValue("test")
    }

    @Test fun `getting an EOr or a next one on value`() {
        val e1 = E.code(1)
        val e2 = E.code(2)

        e1.toEOr<String>().andThen { e2.toEOr<String>() }.assertError(e1)
        e1.toEOr<String>().andThen { "test".toEOr() }.assertError(e1)

        "test".toEOr().andThen { e2.toEOr<String>() }.assertError(e2)
        "test".toEOr().andThen { 42.toEOr() }.assertValue(42)
    }

    @Test fun `performing side-effect on an EOr on error`() {
        var counter = 0

        "test".toEOr().onError {
            counter += 1
        }
        assertEquals(0, counter)

        listOf(E.name("test1"), E.name("test2"), E.name("test3")).forEach {
            it.toEOr<String>().onError {
                counter += 1
            }
        }
        assertEquals(3, counter)
    }

    @Test fun `performing side-effect on an EOr on value`() {
        val e = E.code(1)
        var counter = 0

        e.toEOr<String>().onValue {
            counter += 1
        }
        assertEquals(0, counter)

        listOf("test1", "test2", "test3").forEach {
            it.toEOr().onValue {
                counter += 1
            }
        }
        assertEquals(3, counter)
    }

    @Test fun `performing side-effect on an EOr using forEach`() {
        val e = E.code(1)
        var counter = 0

        e.toEOr<String>().forEach {
            counter += 1
        }
        assertEquals(0, counter)

        listOf("test1", "test2", "test3").forEach {
            it.toEOr().forEach {
                counter += 1
            }
        }
        assertEquals(3, counter)
    }

    @Test fun `filtering an EOr`() {
        val e = E.code(1)
        val negative = E.name("negative")

        e.toEOr<Int>().filter({ it > 0 }).assertError(e)
        e.toEOr<Int>().filter({ it > 0 }) { negative }.assertError(e)

        42.toEOr().filter({ it > 0 }).assertValue(42)
        (-42).toEOr().filter({ it > 0 }).assertError(EOr.filteredError.data("value", -42))

        42.toEOr().filter({ it > 0 }) { negative.data("value", it) }.assertValue(42)
        (-42).toEOr().filter({ it > 0 }) { negative.data("value", it) }.assertError(negative.data("value", -42))
    }

    @Test fun `equality and hash code of EOr`() {
        val eor1 = "test1".toEOr()
        val eor2 = "test2".toEOr()

        assertEquals("test1".toEOr(), eor1)
        assertEquals("test1".toEOr().hashCode(), eor1.hashCode())

        assertNotEquals(eor1, eor2)
        assertNotEquals(eor1.hashCode(), eor2.hashCode())

        val e1 = E(1, "test", "Test", listOf(E.name("test")), mapOf("test" to "test"), 123456789L)

        val differentEs = listOf(
            e1.copy(code = null),
            e1.code(2),
            e1.copy(name = null),
            e1.name("test2"),
            e1.copy(message = null),
            e1.message("Test2"),
            e1.copy(causes = emptyList()),
            e1.cause(E.empty),
            e1.copy(data = emptyMap()),
            e1.data("foo", "bar"),
            e1.copy(time = null),
            e1.now()
        )

        differentEs.forEach { e2 ->
            val eor3 = e1.toEOr<String>()
            val eor4 = e2.toEOr<String>()

            assertNotEquals(eor1, eor3)
            assertNotEquals(eor1.hashCode(), eor3.hashCode())

            assertNotEquals(eor1, eor4)
            assertNotEquals(eor1.hashCode(), eor4.hashCode())
        }
    }

    @Test fun `converting an EOr to String`() {
        assertEquals("""{"name":"test"}""", E.name("test").toEOr<String>().toString())
        assertEquals("42", 42.toEOr().toString())
    }
}
