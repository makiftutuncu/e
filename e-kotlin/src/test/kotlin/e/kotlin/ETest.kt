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
        val e = E("test-name", "Test Message", 1, cause, data)

        assertTrue(e.hasName())
        assertEquals("test-name", e.name())

        assertTrue(e.hasMessage())
        assertEquals("Test Message", e.message())

        assertTrue(e.hasCode())
        assertEquals(1, e.code())

        assertTrue(e.hasCause())
        assertEquals("Test Exception", e.cause()?.message)

        assertTrue(e.hasData())
        assertEquals(1, e.data().size)
        assertEquals("data", e.data()["test"])
    }

    @Test fun `test constructing E with no fields`() {
        val e = E()

        assertFalse(e.hasName())
        assertEquals("", e.name())

        assertFalse(e.hasMessage())
        assertEquals("", e.message())

        assertFalse(e.hasCode())
        assertEquals(EMPTY_CODE, e.code())

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
        val e1 = E("test-name", "Test Message")

        val expected1 = Exception(e1.toString())
        val actual1   = e1.toException()

        assertEquals(expected1.message, actual1.message)
        assertNull(actual1.cause)

        val e2 = e1.cause(cause)

        val expected2 = Exception(e2.toString(), cause)
        val actual2   = e2.toException()

        assertEquals(expected2.message, actual2.message)
        assertEquals(expected2.cause,   actual2.cause)
    }

    @Test fun `test converting to a Maybe`() {
        val e = E("test-name", "Test Message")

        val expected = Maybe.failure<String>(e)
        val actual   = e.toMaybe<String>()

        assertEquals(expected, actual)
    }

    @Test fun `test equality`() {
        val e = E("test-name", "Test Message", 1, cause, data)

        assertEquals(e,    E("test-name", "Test Message", 1, cause,            data))
        assertNotEquals(e, E("test-name", "Test Message", 2, cause,            data))
        assertNotEquals(e, E("foo",       "Test Message", 1, cause,            data))
        assertNotEquals(e, E("test-name", "bar",          1, cause,            data))
        assertNotEquals(e, E("test-name", "Test Message", 1, Exception("baz"), data))
        assertNotEquals(e, E("test-name", "Test Message", 1, cause,            mapOf("foo" to "bar")))
    }

    @Test fun `test hash code generation`() {
        val e = E("test-name", "Test Message", 1, cause, data)

        assertNotEquals(e.hashCode(), E("test-name", "Test Message", 2, cause,            data).hashCode())
        assertNotEquals(e.hashCode(), E("foo",       "Test Message", 1, cause,            data).hashCode())
        assertNotEquals(e.hashCode(), E("test-name", "bar",          1, cause,            data).hashCode())
        assertNotEquals(e.hashCode(), E("test-name", "Test Message", 1, Exception("baz"), data).hashCode())
        assertNotEquals(e.hashCode(), E("test-name", "Test Message", 1, cause,            mapOf("foo" to "bar")).hashCode())
    }
}
