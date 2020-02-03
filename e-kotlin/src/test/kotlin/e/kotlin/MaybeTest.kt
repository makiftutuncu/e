package e.kotlin

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

object MaybeTest {
    @Test fun `test constructing a Failure`() {
        val e = E(name = "test-name")

        val maybe1: Maybe<String> = Failure(e)

        assertTrue(maybe1.isFailure())
        assertFalse(maybe1.isSuccess())
        assertNull(maybe1.a)
        assertEquals(maybe1.e, e)

        val maybe2: Maybe<Int> = e.maybe()

        assertTrue(maybe2.isFailure())
        assertFalse(maybe2.isSuccess())
        assertNull(maybe2.a)
        assertEquals(maybe2.e, e)
    }

    @Test fun `test constructing a Failure on nullable values`() {
        val s: String? = null
        val e = E(name = "test-name")

        val maybe: Maybe<String> = s.orE(e)

        assertTrue(maybe.isFailure())
        assertFalse(maybe.isSuccess())
        assertNull(maybe.a)
        assertEquals(maybe.e, e)
    }

    @Test fun `test constructing a Success`() {
        val maybe1: Maybe<String> = Success("test")

        assertFalse(maybe1.isFailure())
        assertTrue(maybe1.isSuccess())
        assertEquals(maybe1.a, "test")
        assertNull(maybe1.e)

        val maybe2: Maybe<Int> = 42.maybe()

        assertFalse(maybe2.isFailure())
        assertTrue(maybe2.isSuccess())
        assertEquals(maybe2.a, 42)
        assertNull(maybe2.e)
    }

    @Test fun `test mapping a Maybe`() {
        val e = E(name = "test-name")

        val maybe1: Maybe<Int> = Failure(e)
        val maybe2: Maybe<Int> = Success(42)

        assertEquals(e.maybe<String>(), maybe1.map { it.toString() })
        assertEquals("42".maybe(),      maybe2.map { it.toString() })
    }

    @Test fun `test flatmapping a Maybe`() {
        val e1 = E(name = "test-name")
        val e2 = E(message = "Test Message")

        val maybe1: Maybe<Int> = Failure(e1)
        val maybe2: Maybe<Int> = Success(42)

        assertEquals(e1.maybe<String>(), maybe1.flatMap { e2.maybe<String>() })
        assertEquals(e1.maybe<String>(), maybe1.flatMap { it.toString().maybe() })

        assertEquals(e2.maybe<String>(), maybe2.flatMap { e2.maybe<String>() })
        assertEquals("42".maybe(),       maybe2.flatMap { it.toString().maybe() })
    }

    @Test fun `test folding a Maybe`() {
        val e = E(name = "test-name")

        val maybe1: Maybe<Int> = Failure(e)
        val maybe2: Maybe<Int> = Success(42)

        assertEquals("0",  maybe1.fold({ "0" }) { it.toString() })
        assertEquals("42", maybe2.fold({ "0" }) { it.toString() })
    }

    @Test fun `test equality`() {
        val e1 = E(name = "test-name")
        val e2 = E(message = "Test Message")

        assertEquals(e1.maybe<String>(),    e1.maybe<String>())
        assertNotEquals(e1.maybe<String>(), e2.maybe<String>())
        assertNotEquals(e1.maybe<String>(), "42".maybe())

        assertEquals("42".maybe(), "42".maybe())
        assertNotEquals("42".maybe(), "43".maybe())
        assertNotEquals("42".maybe(), e1.maybe<String>())
    }

    @Test fun `test hash code generation`() {
        val e1 = E(name = "test-name")
        val e2 = E(message = "Test Message")

        assertEquals(e1.maybe<String>().hashCode(),    e1.maybe<String>().hashCode())
        assertNotEquals(e1.maybe<String>().hashCode(), e2.maybe<String>().hashCode())
        assertNotEquals(e1.maybe<String>().hashCode(), "42".maybe().hashCode())

        assertEquals("42".maybe().hashCode(), "42".maybe().hashCode())
        assertNotEquals("42".maybe().hashCode(), "43".maybe().hashCode())
        assertNotEquals("42".maybe().hashCode(), e1.maybe<String>().hashCode())
    }
}
