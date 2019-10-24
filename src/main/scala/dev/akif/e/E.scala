package dev.akif.e

final case class E(code: Int,
                   name: String,
                   message: String,
                   cause: Option[Throwable],
                   data: Map[String, String]) extends Product with Serializable { self =>
  def code(c: Int): E = copy(code = c)

  def name(n: String): E = copy(name = n)

  def message(m: String): E = copy(message = m)

  def cause(throwable: Throwable): E = copy(cause = Some(throwable))

  def data(d: Map[String, String]): E = copy(data = d)

  override def toString: String = DefaultEncoderE.encode(self)
}

object E {
  val empty: E = E(0, "", "", None, Map.empty)

  def apply: E = empty

  def code(c: Int): E = empty.code(c)

  def name(n: String): E = empty.name(n)

  def message(m: String): E = empty.message(m)

  def cause(throwable: Throwable): E = empty.cause(throwable)

  def data(d: Map[String, String]): E = empty.data(d)

  def apply(code: Int, name: String): E = empty.code(code).name(name)

  def apply(code: Int, name: String, message: String): E = apply(code, name).message(message)

  def apply(code: Int, name: String, message: String, data: Map[String, String]): E = apply(code, name, message).data(data)

  def apply(code: Int, name: String, cause: Throwable): E = apply(code, name, cause.getMessage).cause(cause)

  def apply(code: Int, name: String, message: String, cause: Throwable): E = apply(code, name, message).cause(cause)
}
