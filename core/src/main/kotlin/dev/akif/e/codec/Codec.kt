package dev.akif.e.codec

import dev.akif.e.E

interface Codec<in IN, out OUT> : Decoder<IN>, Encoder<OUT>

interface Decoder<in IN> {
    @Throws(DecodingError::class)
    fun decode(input: IN): E
}

interface Encoder<out OUT> {
    fun encode(e: E): OUT
}

fun <IN> IN.decodeWith(decoder: Decoder<IN>): E    = decoder.decode(this)
fun <OUT> E.encodeWith(encoder: Encoder<OUT>): OUT = encoder.encode(this)
