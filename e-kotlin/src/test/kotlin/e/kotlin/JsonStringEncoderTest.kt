package e.kotlin

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

object JsonStringEncoderTest {
    private lateinit var cause: Throwable
    private lateinit var data: Map<String, String>

    @BeforeEach fun reset() {
        cause = Exception("Test Exception")
        data  = mapOf("test" to "data")
    }

    @Test fun `test encoding E with all fields`() {
        val e = E(1, "test-name", "Test Message", cause, data)

        val expected = """{"code":1,"name":"test-name","message":"Test Message","cause":"Test Exception","data":{"test":"data"}}"""
        val actual   = JsonStringEncoder.encode(e)

        assertEquals(expected, actual)
    }

    @Test fun `test encoding E with some fields`() {
        val e = E(name = "test-name", message = "Test Message")

        val expected = """{"name":"test-name","message":"Test Message"}"""
        val actual   = JsonStringEncoder.encode(e)

        assertEquals(expected, actual)
    }

    @Test fun `test encoding E with quotes`() {
        val e = E(name = "test-name", data = mapOf("test" to "da\"ta", "f\"oo" to "bar"))

        val expected = """{"name":"test-name","data":{"test":"da\"ta","f\"oo":"bar"}}"""
        val actual   = JsonStringEncoder.encode(e)

        assertEquals(expected, actual)
    }

    @Test fun `test encoding E with no fields`() {
        val e = E()

        val expected = """{}"""
        val actual   = JsonStringEncoder.encode(e)

        assertEquals(expected, actual)
    }
}
