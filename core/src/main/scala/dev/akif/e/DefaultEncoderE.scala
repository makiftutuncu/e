package dev.akif.e

import scala.collection.JavaConverters.mapAsScalaMap

object DefaultEncoderE extends EncoderE[String] {
  override def encode(e: E): String = {
    val strings = List(
      s""""code":${e.code}""",
      if (!e.hasName)    "" else s""""name":"${escape(e.name)}"""",
      if (!e.hasMessage) "" else s""""message":"${escape(e.message)}"""",
      if (!e.hasCause)   "" else s""""cause":"${escape(e.cause.getMessage)}"""",
      if (!e.hasData)    "" else s""""data":${mapAsScalaMap(e.data).map({case (k, v) => s""""${escape(k)}":"${escape(v)}""""}).mkString("{", ",", "}")}"""
    )

    val size = strings.size

    val (_, builder) = strings.foldLeft(1 -> new StringBuilder("{")) {
      case ((i, sb), "") if i == size && sb.last == ',' => (i + 1) -> sb.dropRight(1)
      case ((i, sb), "")                                => (i + 1) -> sb
      case ((i, sb), s) if i < size                     => (i + 1) -> sb.append(s).append(",")
      case ((i, sb), s)                                 => (i + 1) -> sb.append(s)
    }

    builder.append("}").toString()
  }

  private def escape(s: String): String = s.replaceAll("\"", "\\\\\"")
}
