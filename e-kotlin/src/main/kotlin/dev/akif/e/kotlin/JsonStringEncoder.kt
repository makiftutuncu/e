package dev.akif.e.kotlin

import dev.akif.e.AbstractJsonStringEncoder

object JsonStringEncoder : AbstractJsonStringEncoder<Throwable?, Map<String, String>>() {
    override fun encodeCause(cause: Throwable?): String =
        if (cause == null) "null" else """"${escape(cause.message)}""""

    override fun encodeData(data: Map<String, String>): String =
        data.map { e -> """"${escape(e.key)}":"${escape(e.value)}"""" }.joinToString(",", "{", "}")
}
