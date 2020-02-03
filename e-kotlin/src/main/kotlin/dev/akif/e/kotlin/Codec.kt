package dev.akif.e.kotlin

import dev.akif.e.AbstractCodec
import dev.akif.e.AbstractDecoder
import dev.akif.e.AbstractEncoder

interface Codec<A> : AbstractCodec<E, A>

interface Decoder<IN> : AbstractDecoder<IN, E>

interface Encoder<OUT> : AbstractEncoder<E, OUT>

fun <IN> Decoder<IN>.decodeMaybe(input: IN): Maybe<E> {
    val result        = this.decode(input)
    val decodingError = result.decodingError()
    val decoded       = result.decoded()

    return when {
        decodingError.isPresent -> Failure(decodingError.get())
        decoded.isPresent       -> Success(decoded.get())
        else                    -> Failure(E.empty()
                                            .name("decoding-failure")
                                            .message("Invalid Decoder because it contains neither a decoding error nor a decoded E!")
                                            .data("class", this::class.java.canonicalName)
                                            .data("input", input.toString()))
    }
}
