package e.scala

import e.AbstractE

final case class E(override val name: String = "",
                   override val message: String = "",
                   override val code: Int = AbstractE.EMPTY_CODE,
                   override val cause: Option[Throwable] = None,
                   override val data: Map[String, String] = Map.empty) extends AbstractE[Option[Throwable], Map[String, String]](name, message, code, cause, data) {
  override def name(n: String): E               = E(n,    message, code, cause,   data)
  override def message(m: String): E            = E(name, m,       code, cause,   data)
  override def code(c: Int): E                  = E(name, message, c,    cause,   data)
  override def cause(c: Option[Throwable]): E   = E(name, message, code, c,       data)
  def cause(c: Throwable): E                    = E(name, message, code, Some(c), data)
  override def data(d: Map[String, String]): E  = E(name, message, code, cause,   d)
  def data(k: String, v: Any): E                = E(name, message, code, cause,   data + (k -> v.toString))
  def data(tuple: (String, Any)): E             = E(name, message, code, cause,   data + (tuple._1 -> tuple._2.toString))

  override def hasCause: Boolean = cause.isDefined

  override def hasData: Boolean = data.nonEmpty

  override def toException: Exception = new Exception(message, cause.orNull)

  def toMaybe[A]: Maybe[A] = Maybe.failure(this)

  override def toString: String = JsonStringEncoder.encode(this)
}

object E {
  def empty: E = E()
}
