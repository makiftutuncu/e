package dev.akif.e.scala

import dev.akif.e.AbstractJsonStringEncoder

object JsonStringEncoder extends AbstractJsonStringEncoder[Map[String, String]] {
  override protected def encodeData(data: Map[String, String]): String =
    data.map { case (k, v) =>
      s""""${escape(k)}":"${escape(v)}""""
    }.mkString("{", ",", "}")
}
