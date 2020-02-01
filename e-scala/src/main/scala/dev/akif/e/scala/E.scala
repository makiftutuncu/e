package dev.akif.e.scala

import dev.akif.e.AbstractE

final case class E(override val code: Int,
                   override val name: String = "",
                   override val message: String = "",
                   override val cause: Option[Throwable] = None,
                   override val data: Map[String, String] = Map.empty) extends AbstractE[Option[Throwable], Map[String, String]](code, name, message, cause, data) {
  def code(c: Int): E                  = E(c,    name, message, cause,   data)
  def name(n: String): E               = E(code, n,    message, cause,   data)
  def message(m: String): E            = E(code, name, m,       cause,   data)
  def cause(c: Throwable): E           = E(code, name, message, Some(c), data)
  def data(d: Map[String, String]): E  = E(code, name, message, cause,   d)
  def data(k: String, v: String): E    = E(code, name, message, cause,   data + (k -> v))
  def data(tuple: (String, String)): E = E(code, name, message, cause,   data + tuple)

  override def hasCause: Boolean = cause.isDefined

  override def hasData: Boolean = data.nonEmpty

  override def toException: Exception = new Exception(message, cause.orNull)

  override def toString: String = JsonStringEncoder.encode(this)
}

object E {
  def empty: E = E(AbstractE.EMPTY_CODE)
}
