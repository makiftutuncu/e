package dev.akif.e.codec

import dev.akif.e.E

object StringEncoder : Encoder<String> {
    override fun encode(e: E): String =
        """{"code":${e.code}""" +
        (if (!e.hasName())    "" else ""","name":"${escape(e.name)}"""") +
        (if (!e.hasMessage()) "" else ""","message":"${escape(e.message)}"""") +
        (if (!e.hasData())    "" else ""","data":${e.data.map { """"${escape(it.key)}":"${escape(it.value)}"""" }.joinToString(",", "{", "}")}""") +
        "}"

    private fun escape(s: String) = s.replace("\"".toRegex(), "\\\\\"")
}
