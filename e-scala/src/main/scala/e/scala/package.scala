package e

package object scala {
  type Maybe[+A] = Either[E, A]
}
