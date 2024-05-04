package e.scala

/** A RuntimeException wrapping E to be used where errors are represented as E but an Exception is needed
  *
  * @param e
  *   An E
  * @see
  *   [[_root_.e.scala.E]]
  * @see
  *   java.lang.RuntimeException
  */
final case class EException(e: E) extends RuntimeException(e.toString):
    override def toString: String = e.toString
