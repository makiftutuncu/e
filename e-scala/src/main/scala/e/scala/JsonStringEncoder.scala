package e.scala

object JsonStringEncoder extends Encoder[String] {
  override def encode(e: E): String =
    List(
      Option.when(e.hasName)("name" -> s""""${escape(e.name)}""""),
      Option.when(e.hasMessage)("message" -> s""""${escape(e.message)}""""),
      Option.when(e.hasCode)("code" -> s"${e.code}"),
      encodeCause(e.cause).map(c => "cause" -> c),
      Option.when(e.hasData)("data" -> s"${encodeData(e.data)}")
    ).collect {
      case Some((k, v)) => s""""$k":$v"""
    }.mkString("{", ",", "}")

  protected def encodeCause(cause: Option[Throwable]): Option[String] =
    cause.map(c => s""""${escape(c.getMessage)}"""")

  protected def encodeData(data: Map[String, String]): String =
    data.map { case (k, v) => s""""${escape(k)}":"${escape(v)}"""" }.mkString("{", ",", "}")

  private def escape(s: String): String = s.replace("\"", "\\\"")
}
