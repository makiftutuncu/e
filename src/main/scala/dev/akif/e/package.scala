package dev.akif

package object e {
  type Maybe[+A] = Either[E, A]
}
