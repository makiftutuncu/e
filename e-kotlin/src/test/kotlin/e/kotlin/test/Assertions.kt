package e.kotlin.test

import e.kotlin.E
import e.kotlin.EOr
import org.junit.jupiter.api.Assertions.*
import kotlin.math.absoluteValue

interface Assertions {
    fun E.assertCode(code: Int?) {
        assertEquals(code != null, this.hasCode)
        assertEquals(code,         this.code)
    }

    fun E.assertName(name: String?) {
        assertEquals(name != null, this.hasName)
        assertEquals(name,         this.name)
    }

    fun E.assertMessage(message: String?) {
        assertEquals(message != null, this.hasMessage)
        assertEquals(message,         this.message)
    }

    fun E.assertCauses(causes: List<E>) {
        assertEquals(causes.isNotEmpty(), this.hasCause)
        assertEquals(causes,              this.causes)
    }

    fun E.assertData(data: Map<String, String>) {
        assertEquals(data.isNotEmpty(), this.hasData)
        assertEquals(data,              this.data)
    }

    fun E.assertTime(time: Long?) {
        assertEquals(time != null, this.hasTime)
        assertEquals(time,         this.time)
    }

    fun assertAlmostSame(expected: Long, actual: Long?, threshold: Long = 1000L) {
        assertTrue(
            ((actual ?: 0L) - expected).absoluteValue < threshold,
            "Times were apart mode than $threshold ms, expected: $expected, actual: $actual"
        )
    }

    fun <A> EOr<A>.assertError(thatE: E) {
        fold(
            { thisE -> assertEquals(thatE, thisE, "Expected: ${thatE.trace()}, Actual: ${thisE.trace()}") },
            { a     -> fail<A>("EOr did not contain error, it contained: $a") }
        )
    }

    fun <A> EOr<A>.assertValue(thatA: A) {
        fold(
            { e     -> fail<A>("EOr did not contain value, it contained: ${e.trace()}") },
            { thisA -> assertEquals(thatA, thisA) }
        )
    }
}
