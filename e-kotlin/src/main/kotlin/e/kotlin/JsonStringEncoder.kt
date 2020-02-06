package e.kotlin

import java.util.*

object JsonStringEncoder : Encoder<String> {
    override fun encode(e: E): String {
        val joiner = StringJoiner(",", "{", "}")

        if (e.hasName())    joiner.add(""""name":"${escape(e.name())}"""")
        if (e.hasMessage()) joiner.add(""""message":"${escape(e.message())}"""")
        if (e.hasCode())    joiner.add(""""code":${e.code()}""")
        if (e.hasCause())   joiner.add(""""cause":${encodeCause(e.cause())}""")
        if (e.hasData())    joiner.add(""""data":${encodeData(e.data())}""")

        return joiner.toString()
    }

    private fun encodeCause(cause: Throwable?): String =
        if (cause?.message == null) "null" else """"${escape(cause.message!!)}""""

    private fun encodeData(data: Map<String, String>): String =
        data.map { e -> """"${escape(e.key)}":"${escape(e.value)}"""" }.joinToString(",", "{", "}")

    private fun escape(s: String): String = s.replace("\"", "\\\"")
}
