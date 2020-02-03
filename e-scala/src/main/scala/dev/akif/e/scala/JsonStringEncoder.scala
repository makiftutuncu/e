package dev.akif.e.scala

import dev.akif.e.AbstractJsonStringEncoder

object JsonStringEncoder extends AbstractJsonStringEncoder[Option[Throwable], Map[String, String]] {
  override protected def encodeCause(cause: Option[Throwable]): String =
    cause.fold("null")(c => s""""${escape(c.getMessage)}"""")

  override protected def encodeData(data: Map[String, String]): String =
    data.map { case (k, v) => s""""${escape(k)}":"${escape(v)}"""" }.mkString("{", ",", "}")
}
