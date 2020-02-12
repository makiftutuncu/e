package e.scala

object JsonStringEncoder extends Encoder[String] {
  override def encode(e: E): String =
    List(
      if (e.hasName) Some("name" -> s""""${escape(e.name)}"""") else None,
      if (e.hasMessage) Some("message" -> s""""${escape(e.message)}"""") else None,
      if (e.hasCode) Some("code" -> s"${e.code}") else None,
      encodeCause(e.cause).map(c => "cause" -> c),
      if (e.hasData) Some("data" -> s"${encodeData(e.data)}") else None
    ).collect {
      case Some((k, v)) => s""""$k":$v"""
    }.mkString("{", ",", "}")

  protected def encodeCause(cause: Option[Throwable]): Option[String] =
    cause.map(c => s""""${escape(c.getMessage)}"""")

  protected def encodeData(data: Map[String, String]): String =
    data.map { case (k, v) => s""""${escape(k)}":"${escape(v)}"""" }.mkString("{", ",", "}")

  private def escape(s: String): String = s.replace("\"", "\\\"")
}
