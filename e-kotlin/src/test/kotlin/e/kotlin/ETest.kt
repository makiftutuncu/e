package e.kotlin

import e.kotlin.test.Assertions
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
}
