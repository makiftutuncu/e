package dev.akif.e.kotlin

import dev.akif.e.AbstractCodec
import dev.akif.e.AbstractDecoder
import dev.akif.e.AbstractEncoder

interface Codec<A> : AbstractCodec<E, A>

interface Decoder<IN> : AbstractDecoder<IN, E>

interface Encoder<OUT> : AbstractEncoder<E, OUT>

fun <IN> Decoder<IN>.decodeMaybe(input: IN): Maybe<E> {
    val result = this.decode(input)
    val e      = result.get()

    return when {
        !result.isSuccess -> Failure(e)
        else              -> Success(e)
    }
}
