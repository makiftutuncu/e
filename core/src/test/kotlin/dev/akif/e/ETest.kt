package dev.akif.e

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

object ETest {
    // TODO: Test `has*` methods

    @Test fun `test with empty values`() {
        val e = E.empty()

        assertEquals(-1, e.code)
        assertEquals("", e.name)
        assertEquals("", e.message)
        assertNull(e.cause)
        assertEquals(mapOf<String, String>(), e.data)
    }

    @Test fun `test with only code`() {
        val e = E.empty().withCode(42)

        assertEquals(42, e.code)
        assertEquals("", e.name)
        assertEquals("", e.message)
        assertNull(e.cause)
        assertEquals(mapOf<String, String>(), e.data)
    }

    @Test fun `test with only name`() {
        val e = E.empty().withName("test")

        assertEquals(-1, e.code)
        assertEquals("test", e.name)
        assertEquals("", e.message)
        assertNull(e.cause)
        assertEquals(mapOf<String, String>(), e.data)
    }

    @Test fun `test with only message`() {
        val e: E = E.empty().withMessage("Test")

        assertEquals(-1, e.code)
        assertEquals("", e.name)
        assertEquals("Test", e.message)
        assertNull(e.cause)
        assertEquals(mapOf<String, String>(), e.data)
    }

    @Test fun `test with only cause`() {
        val e = E.empty().withCause(Exception("test"))

        assertEquals(-1, e.code)
        assertEquals("", e.name)
        assertEquals("", e.message)
        assertEquals("test", e.cause?.message)
        assertEquals(mapOf<String, String>(), e.data)
    }

    @Test fun `test with only data`() {
        val e = E.empty().withData(mapOf("foo" to "bar")).withData("a", "b")

        assertEquals(-1, e.code)
        assertEquals("", e.name)
        assertEquals("", e.message)
        assertNull(e.cause)
        assertEquals(mapOf("foo" to "bar", "a" to "b"), e.data)
    }

    @Test fun `test with some fields`() {
        val e = E.of(code = 1, name = "test", data = mapOf("foo" to "bar"))

        assertEquals(1, e.code)
        assertEquals("test", e.name)
        assertEquals("", e.message)
        assertNull(e.cause)
        assertEquals(mapOf("foo" to "bar"), e.data)
    }

    @Test fun `test with all fields`() {
        val e = E.of(1, "test", "Test", Exception("test"), mapOf("foo" to "bar"))

        assertEquals(1, e.code)
        assertEquals("test", e.name)
        assertEquals("Test", e.message)
        assertEquals("test", e.cause?.message)
        assertEquals(mapOf("foo" to "bar"), e.data)
    }

    // TODO: Test `throwNow`

    // TODO: Test `equals`

    // TODO: Test `hashCode`

    @Test fun `test String representation`() {
        val e = E.of(1, "test", "Test", Exception("test"), mapOf("foo" to "bar"))

        val expected = """{"code":1,"name":"test","message":"Test","data":{"foo":"bar"}}"""
        val actual   = e.toString()

        assertEquals(expected, actual)
    }
}
