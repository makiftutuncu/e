package dev.akif.e

object DefaultEncoderE extends EncoderE[String] {
  override def encode(e: E): String = {
    val strings = List(
      s""""code":${e.code}""",
      if (e.name.isEmpty) "" else s""""name":"${e.name}"""",
      if (e.message.isEmpty) "" else s""""message":"${e.message}"""",
      e.cause.fold("")(c => s""""cause":"${c.getMessage}""""),
      if (e.data.isEmpty) "" else s""""data":${e.data.map({case (k, v) => s""""$k":"$v""""}).mkString("{", ",", "}")}"""
    )

    val builder = strings.foldLeft(new StringBuilder()) {
      case (sb, "") => sb
      case (sb, s)  => sb.append(s).append(",")
    }

    builder.insert(0, "{").append("}").toString()
  }
}
