package e.codec

import scala.annotation.implicitNotFound

/**
 * Typeclass defining decoding and encoding together via [[e.codec.Decoder]] and [[e.codec.Encoder]]
 *
 * @tparam S Type of source
 * @tparam T Type of target
 */
@implicitNotFound(
  """No implicit instance is found of type e.codec.Codec[${S}, ${T}]. You may try following:
    |
    |* Make sure an instance of correct types is in scope (missing import?)
    |* Implement an implicit instance yourself
    |* Ensure both e.codec.Decoder[${T}, ${S}] and e.codec.Encoder[${S}, ${T}] are in scope""".stripMargin
)
trait Codec[S, T] extends Decoder[T, S] with Encoder[S, T]

object Codec {
  /**
   * Summons an implicit codec
   *
   * @param codec Implicit instance of codec
   *
   * @tparam S Type of source
   * @tparam T Type of target
   *
   * @return Provided implicit codec
   */
  def apply[S, T](implicit codec: Codec[S, T]): Codec[S, T] = codec
}
