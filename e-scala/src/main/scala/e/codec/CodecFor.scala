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
  implicit val eDecoder: DE[E]

  implicit val eEncoder: EN[E]

  def decode[A](input: T)(implicit aDecoder: DE[A]): A or E

  def encode[A](input: A)(implicit aEncoder: EN[A]): T

  implicit def decoder[A](implicit aDecoder: DE[A]): Decoder[T, A] = { input: T => decode[A](input) }

  implicit def encoder[A](implicit aEncoder: EN[A]): Encoder[A, T] = { input: A => encode[A](input) }

  implicit def codec[A](implicit aDecoder: DE[A], aEncoder: EN[A]): Codec[A, T] =
    new Codec[A, T] {
      override def decode(input: T): A or E = decoder[A].decode(input)
      override def encode(input: A): T      = encoder[A].encode(input)
    }
}
