package dev.akif.e.scala

object syntax {
  implicit class MaybeSyntaxE(private val e: E) {
    def maybe[A]: Maybe[A] = Left(e)
  }

  implicit class MaybeSyntax[+A](private val a: A) {
    def maybe: Maybe[A] = Right(a)
  }
}
