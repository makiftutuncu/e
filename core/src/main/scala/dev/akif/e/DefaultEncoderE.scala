package dev.akif.e

object DefaultEncoderE extends EncoderE[String] {
  override def encode(e: E): String = {
    val strings = List(
      s""""code":${e.code}""",
      if (e.name.isEmpty) "" else s""""name":"${escape(e.name)}"""",
      if (e.message.isEmpty) "" else s""""message":"${escape(e.message)}"""",
      e.cause.fold("")(c => s""""cause":"${escape(c.getMessage)}""""),
      if (e.data.isEmpty) "" else s""""data":${e.data.map({case (k, v) => s""""${escape(k)}":"${escape(v)}""""}).mkString("{", ",", "}")}"""
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
