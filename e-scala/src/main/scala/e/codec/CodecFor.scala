package e.codec

import e.{E, or}

/**
 * Helper trait that fixes the target type for creating a [[e.codec.Codec]] implementation of that type
 *
 * @tparam T  Target type
 * @tparam DE Decoder type
 * @tparam EN Encoder type
 */
trait CodecFor[T, DE[_], EN[_]] {
  implicit def jsonDecoder[A: DE]: Decoder[T, A]

  implicit def jsonEncoder[A: EN]: Encoder[A, T]

  implicit def jsonCodec[A: DE : EN]: Codec[A, T] =
    new Codec[A, T] {
      override def encode(input: A): T      = jsonEncoder[A].encode(input)
      override def decode(input: T): A or E = jsonDecoder[A].decode(input)
    }
}
