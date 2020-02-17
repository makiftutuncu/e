package dev.akif.ektorexample.common

import com.google.gson.*
import java.lang.reflect.Type
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder

object ZDT : ZDTProvider {
    override fun now(): ZonedDateTime = ZonedDateTime.now().withZoneSameInstant(ZoneOffset.UTC).withNano(0)

    val formatter: DateTimeFormatter = DateTimeFormatterBuilder()
        .append(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        .optionalStart()
        .appendOffsetId()
        .toFormatter()

    val gsonAdapter: Any = object : JsonSerializer<ZonedDateTime>, JsonDeserializer<ZonedDateTime> {
        override fun serialize(zdt: ZonedDateTime?,
                               typeOfSrc: Type?,
                               context: JsonSerializationContext?): JsonElement = JsonPrimitive(zdt?.asString())

        override fun deserialize(json: JsonElement?,
                                 typeOfT: Type?,
                                 context: JsonDeserializationContext?): ZonedDateTime? = json?.asString?.asZDT()
    }
}
