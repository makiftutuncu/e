package e.kotlin

import e.AbstractE.EMPTY_CODE
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

object ETest {
    private lateinit var cause: Throwable
    private lateinit var data: Map<String, String>

    @BeforeEach fun reset() {
        cause = Exception("Test Exception")
        data  = mapOf("test" to "data")
    }

    @Test fun `test constructing E with all fields`() {
        val e = E(1, "test-name", "Test Message", cause, data)

        assertTrue(e.hasCode())
        assertEquals(1, e.code())

        assertTrue(e.hasName())
        assertEquals("test-name", e.name())

        assertTrue(e.hasMessage())
        assertEquals("Test Message", e.message())

        assertTrue(e.hasCause())
        assertEquals("Test Exception", e.cause()?.message)

        assertTrue(e.hasData())
        assertEquals(1, e.data().size)
        assertEquals("data", e.data()["test"])
    }

    @Test fun `test constructing E with no fields`() {
        val e = E()

        assertFalse(e.hasCode())
        assertEquals(EMPTY_CODE, e.code())

        assertFalse(e.hasName())
        assertEquals("", e.name())

        assertFalse(e.hasMessage())
        assertEquals("", e.message())

        assertFalse(e.hasCause())
        assertNull(e.cause())

        assertFalse(e.hasData())
        assertEquals(mapOf<String, String>(), e.data())
    }

    @Test fun `test constructing E as empty`() {
        val actual   = E.empty()
        val expected = E()

        assertEquals(expected, actual)
    }

    @Test fun `test converting to exception`() {
        val e1 = E(1, "test-name", "Test Message")

        val expected1 = Exception("Test Message")
        val actual1   = e1.toException()

        assertEquals(expected1.message, actual1.message)
        assertNull(expected1.cause)

        val e2 = e1.cause(cause)

        val expected2 = Exception("Test Message", cause)
        val actual2   = e2.toException()

        assertEquals(expected2.message, actual2.message)
        assertEquals(expected2.cause,   actual2.cause)
    }

    @Test fun `test equality`() {
        val e = E(1, "test-name", "Test Message", cause, data)

        assertEquals(e,    E(1, "test-name", "Test Message", cause,            data))
        assertNotEquals(e, E(2, "test-name", "Test Message", cause,            data))
        assertNotEquals(e, E(1, "foo",       "Test Message", cause,            data))
        assertNotEquals(e, E(1, "test-name", "bar",          cause,            data))
        assertNotEquals(e, E(1, "test-name", "Test Message", Exception("baz"), data))
        assertNotEquals(e, E(1, "test-name", "Test Message", cause,            mapOf("foo" to "bar")))
    }

    @Test fun `test hash code generation`() {
        val e = E(1, "test-name", "Test Message", cause, data)

        assertNotEquals(e.hashCode(), E(2, "test-name", "Test Message", cause,            data).hashCode())
        assertNotEquals(e.hashCode(), E(1, "foo",       "Test Message", cause,            data).hashCode())
        assertNotEquals(e.hashCode(), E(1, "test-name", "bar",          cause,            data).hashCode())
        assertNotEquals(e.hashCode(), E(1, "test-name", "Test Message", Exception("baz"), data).hashCode())
        assertNotEquals(e.hashCode(), E(1, "test-name", "Test Message", cause,            mapOf("foo" to "bar")).hashCode())
    }
}
