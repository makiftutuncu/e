package e.kotlin

import e.AbstractDecoder.DecodingResult
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

object DecoderTest {
    @Test fun `test decoding as Maybe`() {
        val input           = "test"
        val decodingFailure = E("decoding-failure")
        val decoded         = E("decoded-e")

        val result1 = failingDecoder(decodingFailure).decode(input)

        assertFalse(result1.isSuccess)
        assertEquals(decodingFailure, result1.get())

        val result2 = succeedingDecoder(decoded).decode(input)

        assertTrue(result2.isSuccess)
        assertEquals(decoded, result2.get())
    }

    private fun failingDecoder(decodingFailure: E): Decoder<String> =
        object : Decoder<String> {
            override fun decode(input: String): DecodingResult<E> = DecodingResult.fail(decodingFailure)
        }

    private fun succeedingDecoder(decoded: E): Decoder<String> =
        object : Decoder<String> {
            override fun decode(input: String): DecodingResult<E> = DecodingResult.succeed(decoded)
        }
}
