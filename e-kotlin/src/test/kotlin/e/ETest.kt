package e

import e.test.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

object ETest: Assertions {
    @Test fun `constructing an E`() {
        val empty = E.empty
        empty.assertCode(null)
        empty.assertName(null)
        empty.assertMessage(null)
        empty.assertCauses(emptyList())
        empty.assertData(emptyMap())
        empty.assertTime(null)

        val code    = 1
        val name    = "test"
        val message = "Test"
        val causes  = listOf(E.name("cause1"), E.name("cause2"))
        val data    = mapOf("foo" to "bar")
        val time    = 123456789L

        val e = E(code, name, message, causes, data, time)

        val code2 = 2

        e.assertCode(code)
        E(code = code).assertCode(code)
        E(code = null).assertCode(null)
        E.code(code).assertCode(code)
        e.code(code2).assertCode(code2)

        val name2 = "test2"

        e.assertName(name)
        E(name = name).assertName(name)
        E(name = null).assertName(null)
        E.name(name).assertName(name)
        e.name(name2).assertName(name2)

        val message2 = "Test 2"

        e.assertMessage(message)
        E(message = message).assertMessage(message)
        E(message = null).assertMessage(null)
        E.message(message).assertMessage(message)
        e.message(message2).assertMessage(message2)

        val cause3 = E.name("cause3")

        e.assertCauses(causes)
        E(causes = causes).assertCauses(causes)
        E.causes(causes).assertCauses(causes)
        e.causes(listOf(cause3)).assertCauses(causes + cause3)

        e.cause(cause3).assertCauses(causes + cause3)
        E.cause(cause3).assertCauses(listOf(cause3))
        e.causeIf(true) { cause3 }.assertCauses(causes + cause3)
        e.causeIf(false) { cause3 }.assertCauses(causes)
        E.causeIf(true) { cause3 }.assertCauses(listOf(cause3))
        E.causeIf(false) { cause3 }.assertCauses(emptyList())

        e.assertData(data)
        E(data = data).assertData(data)
        E.data(data).assertData(data)

        val key   = "test"
        val value = 42

        e.data(key, value).assertData(data + (key to value.toString()))
        E.data(key, value).assertData(mapOf(key to value.toString()))
        e.data(key to value).assertData(data + (key to value.toString()))
        E.data(key to value).assertData(mapOf(key to value.toString()))

        val now = System.currentTimeMillis()

        e.assertTime(time)
        E(time = time).assertTime(time)
        E(time = null).assertTime(null)
        E.time(time).assertTime(time)
        e.time(now).assertTime(now)

        assertAlmostSame(now, e.now().time)
        assertAlmostSame(now, E.now().time)
    }

    @Test fun `getting trace of an E`() {
        val e9 = E(code = 9)
        val e8 = E(message = "Test 8")
        val e7 = E(name = "test7")
        val e6 = E(name = "test6", message = "Test 6")
        val e5 = E(code = 5, message = "Test 5")
        val e4 = E(code = 4, name = "test4")
        val e3 = E(code = 3, name = "test3", message = "Test 3")
        val e2 = E(code = 2, name = "test2", message = "Test 2", causes = listOf(e3, E.empty))
        val e = E(
            code    = 1,
            name    = "test1",
            message = "Test 1",
            data    = mapOf("foo" to "bar"),
            causes  = listOf(e2, e4, e5, e6, e7, e8, e9)
        )

        val expected =
            """E1 | test1 | Test 1 | [foo -> bar]
              |  E2 | test2 | Test 2
              |    E3 | test3 | Test 3
              |    [Empty E]
              |  E4 | test4
              |  E5 | Test 5
              |  test6 | Test 6
              |  test7
              |  Test 8
              |  E9""".trimMargin()

        val actual = e.trace()

        assertEquals(expected, actual)
    }

    @Test fun `converting an E to an EOr`() {
        val e   = E(name = "test", message = "Test")
        val eor = e.toEOr<String>()

        eor.assertError(e)
    }

    @Test fun `converting an E to an EException`() {
        val e  = E(name = "test", message = "Test")
        val ex = e.toException()

        assertEquals(EException(e), ex)
    }

    @Test fun `constructing an E from a Throwable`() {
        assertEquals(E.message("Test"), Exception("Test").toE())
        assertEquals(E.message("Test"), E.fromThrowable(Exception("Test")))
        assertEquals(E.name("test"), E.fromThrowable(E.name("test").toException()))
    }

    @Test fun `converting an E to a String`() {
        val e9 = E(code = 9)
        val e8 = E(message = "Test 8")
        val e7 = E(name = "test7")
        val e6 = E(name = "test6", message = "Test 6")
        val e5 = E(code = 5, message = "Test 5")
        val e4 = E(code = 4, name = "test4")
        val e3 = E(code = 3, name = "test3", message = "Test 3")
        val e2 = E(code = 2, name = "test2", message = "Test 2", causes = listOf(e3))
        val e1 = E(
            code    = 1,
            name    = "test1",
            message = "Test 1",
            data    = mapOf("foo" to "bar"),
            causes  = listOf(e2, e4, e5, e6, e7, e8, e9)
        )

        assertEquals("[Empty E]", E.empty.toString())
        assertEquals("E1 | test1 | Test 1 | [foo -> bar]", e1.toString())
        assertEquals("E2 | test2 | Test 2", e2.toString())
        assertEquals("E3 | test3 | Test 3", e3.toString())
        assertEquals("E4 | test4", e4.toString())
        assertEquals("E5 | Test 5", e5.toString())
        assertEquals("test6 | Test 6", e6.toString())
        assertEquals("test7", e7.toString())
        assertEquals("Test 8", e8.toString())
        assertEquals("E9", e9.toString())
    }
}
