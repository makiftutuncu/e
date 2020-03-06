package e.scala

class EException(val e: E) extends Exception(e.toString, e.cause.orNull)

object EException {
  def apply(e: E): EException = new EException(e)

  def unapply(ee: EException): Option[E] = Some(ee.e)
}
