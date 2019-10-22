package dev.akif.e

final case class E(code: Int,
                   name: String,
                   message: String,
                   cause: Option[Throwable],
                   data: Map[String, String]) extends Product with Serializable { self =>
  def code(c: Int): E = copy(code = c)

  def name(n: String): E = copy(name = n)

  def message(m: String): E = copy(message = m)

  def cause(throwable: Throwable): E =
    cause.fold(copy(cause = Some(throwable))) { existing =>
      copy(cause = Some(throwable.initCause(existing)))
    }

  def data(d: Map[String, String]): E = copy(data = self.data ++ d)

  override def toString: String =
    List(
      s""""code":$code""",
      if (name.isEmpty) "" else s""""name":"$name"""",
      if (message.isEmpty) "" else s""""message":"$message"""",
      cause.fold("")(c => s""""cause":"${c.getMessage}""""),
      if (data.isEmpty) "" else s""""data":${data.map({case (k, v) => s""""$k":"$v""""}).mkString("{", ",", "}")}"""
    ).filterNot(_.isEmpty)
     .mkString("{", ",", "}")
}

object E {
  val empty: E = E(0, "", "", None, Map.empty)

  def code(c: Int): E = empty.code(c)

  def name(n: String): E = empty.name(n)

  def message(m: String): E = empty.message(m)

  def cause(throwable: Throwable): E = empty.cause(throwable)

  def data(d: Map[String, String]): E = empty.data(d)
}
