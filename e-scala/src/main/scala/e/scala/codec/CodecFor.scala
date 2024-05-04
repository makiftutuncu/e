package e.scala.codec

import e.scala.{E, EOr}

/** Helper trait creating a [[e.scala.codec.Codec]] implementation of a third-party type
  *
  * @tparam T
  *   Target type
  * @tparam DE
  *   Decoder type
  * @tparam EN
  *   Encoder type
  */
trait CodecFor[T, DE[_], EN[_]]:
    given eDecoder: DE[E]

    given eEncoder: EN[E]

    def decode[A](input: T)(using aDecoder: DE[A]): EOr[A]

    def encode[A](input: A)(using aEncoder: EN[A]): T

    def makeDecoder[A](using aDecoder: DE[A]): Decoder[T, A] = (input: T) => decode[A](input)

    def makeEncoder[A](using aEncoder: EN[A]): Encoder[A, T] = (input: A) => encode[A](input)

    def makeCodec[A](using aDecoder: DE[A], aEncoder: EN[A]): Codec[A, T] =
        new Codec[A, T]:
            override def decode(input: T): EOr[A] = makeDecoder[A].decode(input)
            override def encode(input: A): T = makeEncoder[A].encode(input)
