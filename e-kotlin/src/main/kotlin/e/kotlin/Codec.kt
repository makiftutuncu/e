package e.kotlin

import e.AbstractCodec
import e.AbstractDecoder
import e.AbstractEncoder

interface Codec<A> : AbstractCodec<E, A>

interface Decoder<IN> : AbstractDecoder<IN, E>

interface Encoder<OUT> : AbstractEncoder<E, OUT>

fun <IN> Decoder<IN>.decodeMaybe(input: IN): Maybe<E> {
    val result = this.decode(input)
    val e      = result.get()

    return when {
        !result.isSuccess -> Maybe.failure(e)
        else              -> Maybe.success(e)
    }
}
