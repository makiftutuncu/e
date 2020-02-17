package e.kotlin

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

object MaybeTest {
    @Test fun `test constructing a Failure`() {
        val e = E("test-name")

        val maybe1: Maybe<String> = Maybe.failure(e)

        assertFalse(maybe1.isSuccess())
        assertNull(maybe1.value)
        assertEquals(maybe1.e, e)

        val maybe2: Maybe<Int> = e.toMaybe()

        assertFalse(maybe2.isSuccess())
        assertNull(maybe2.value)
        assertEquals(maybe2.e, e)
    }

    @Test fun `test constructing a Success`() {
        val maybe1: Maybe<String> = Maybe.success("test")

        assertTrue(maybe1.isSuccess())
        assertEquals(maybe1.value, "test")
        assertNull(maybe1.e)

        val maybe2: Maybe<Int> = 42.toMaybe()

        assertTrue(maybe2.isSuccess())
        assertEquals(maybe2.value, 42)
        assertNull(maybe2.e)
    }

    @Test fun `test constructing unit`() {
        val maybe: Maybe<Unit> = Maybe.unit()

        assertTrue(maybe.isSuccess())
        assertEquals(maybe.value, Unit)
        assertNull(maybe.e)
    }

    @Test fun `test constructing a Failure on nullable values`() {
        val s: String? = null
        val e = E("test-name")

        val maybe1: Maybe<String> = Maybe.fromNullable(s, e)

        assertFalse(maybe1.isSuccess())
        assertNull(maybe1.value)
        assertEquals(maybe1.e, e)

        val maybe2: Maybe<String> = s.toMaybe(e)

        assertFalse(maybe2.isSuccess())
        assertNull(maybe2.value)
        assertEquals(maybe2.e, e)
    }

    @Test fun `test constructing by catching`() {
        val e = E("test-name")

        val maybe1: Maybe<String> = Maybe.catching({ throw Exception("Test Exception") }, { cause -> e.cause(cause) })

        assertFalse(maybe1.isSuccess())
        assertNull(maybe1.value)
        assertEquals(maybe1.e?.cause()?.message, "Test Exception")

        val maybe2: Maybe<String> = Maybe.catching({ "test" }, { cause -> e.cause(cause) })

        assertTrue(maybe2.isSuccess())
        assertEquals(maybe2.value, "test")
        assertNull(maybe2.e)
    }

    @Test fun `test constructing by catching Maybe`() {
        val e = E("test-name")

        val maybe1: Maybe<String> = Maybe.catchingMaybe({ throw Exception("Test Exception") }, { cause -> e.cause(cause) })

        assertFalse(maybe1.isSuccess())
        assertNull(maybe1.value)
        assertEquals(maybe1.e?.cause()?.message, "Test Exception")

        val maybe2: Maybe<String> = Maybe.catchingMaybe({ e.toMaybe<String>() }, { cause -> e.cause(cause) })

        assertFalse(maybe2.isSuccess())
        assertNull(maybe2.value)
        assertEquals(maybe2.e, e)

        val maybe3: Maybe<String> = Maybe.catchingMaybe({ "test".toMaybe() }, { cause -> e.cause(cause) })

        assertTrue(maybe3.isSuccess())
        assertEquals(maybe3.value, "test")
        assertNull(maybe3.e)
    }

    @Test fun `test mapping a Maybe`() {
        val e = E("test-name")

        val maybe1: Maybe<Int> = e.toMaybe()
        val maybe2: Maybe<Int> = 42.toMaybe()

        assertEquals(e.toMaybe<String>(), maybe1.map { it.toString() })
        assertEquals("42".toMaybe(),      maybe2.map { it.toString() })
    }

    @Test fun `test flat mapping a Maybe`() {
        val e1 = E("test-name")
        val e2 = E(message = "Test Message")

        val maybe1: Maybe<Int> = e1.toMaybe()
        val maybe2: Maybe<Int> = 42.toMaybe()

        assertEquals(e1.toMaybe<String>(), maybe1.flatMap { e2.toMaybe<String>() })
        assertEquals(e1.toMaybe<String>(), maybe1.flatMap { it.toString().toMaybe() })

        assertEquals(e2.toMaybe<String>(), maybe2.flatMap { e2.toMaybe<String>() })
        assertEquals("42".toMaybe(),       maybe2.flatMap { it.toString().toMaybe() })
    }

    @Test fun `test folding a Maybe`() {
        val e = E("test-name")

        val maybe1: Maybe<Int> = e.toMaybe()
        val maybe2: Maybe<Int> = 42.toMaybe()

        assertEquals("0",  maybe1.fold({ "0" }) { it.toString() })
        assertEquals("42", maybe2.fold({ "0" }) { it.toString() })
    }

    @Test fun `test getting a Maybe with a default value`() {
        val e = E("test-name")

        val maybe1: Maybe<Int> = e.toMaybe()
        val maybe2: Maybe<Int> = 42.toMaybe()

        assertEquals(0,  maybe1.getOrElse { 0 })
        assertEquals(42, maybe2.getOrElse { 0 })
    }

    @Test fun `test replacing a Maybe with an alternative`() {
        val maybe1 = E("test-1").toMaybe<String>()
        val maybe2 = E("test-2").toMaybe<String>()
        val maybe3 = "test-1".toMaybe()
        val maybe4 = "test-2".toMaybe()

        assertEquals(E("test-2").toMaybe<String>(), maybe1.orElse { maybe2 })
        assertEquals("test-1".toMaybe(),            maybe1.orElse { maybe3 })
        assertEquals("test-1".toMaybe(),            maybe3.orElse { maybe1 })
        assertEquals("test-1".toMaybe(),            maybe3.orElse { maybe4 })
    }

    @Test fun `test ignoring value and moving to another Maybe`() {
        val maybe1 = E("test").toMaybe<String>()
        val maybe2 = "test-1".toMaybe()
        val maybe3 = "test-2".toMaybe()

        assertEquals(maybe1, maybe1.andThen { E("test-2").toMaybe<String>() })
        assertEquals(maybe1, maybe1.andThen { "test".toMaybe() })
        assertEquals(maybe1, maybe2.andThen { maybe1 })
        assertEquals(maybe3, maybe2.andThen { maybe3 })
    }

    @Test fun `test equality`() {
        val e1 = E("test-name")
        val e2 = E(message = "Test Message")

        assertEquals(e1.toMaybe<String>(),    e1.toMaybe<String>())
        assertNotEquals(e1.toMaybe<String>(), e2.toMaybe<String>())
        assertNotEquals(e1.toMaybe<String>(), "42".toMaybe())

        assertEquals("42".toMaybe(), "42".toMaybe())
        assertNotEquals("42".toMaybe(), "43".toMaybe())
        assertNotEquals("42".toMaybe(), e1.toMaybe<String>())
    }

    @Test fun `test hash code generation`() {
        val e1 = E("test-name")
        val e2 = E(message = "Test Message")

        assertEquals(e1.toMaybe<String>().hashCode(),    e1.toMaybe<String>().hashCode())
        assertNotEquals(e1.toMaybe<String>().hashCode(), e2.toMaybe<String>().hashCode())
        assertNotEquals(e1.toMaybe<String>().hashCode(), "42".toMaybe().hashCode())

        assertEquals("42".toMaybe().hashCode(), "42".toMaybe().hashCode())
        assertNotEquals("42".toMaybe().hashCode(), "43".toMaybe().hashCode())
        assertNotEquals("42".toMaybe().hashCode(), e1.toMaybe<String>().hashCode())
    }

    @Test fun `test toString`() {
        assertEquals(E("test-name").toMaybe<String>().toString(), """{"name":"test-name"}""")
        assertEquals("42".toMaybe().toString(),                   "42")
    }
}
