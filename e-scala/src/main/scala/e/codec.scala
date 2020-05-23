package e

import scala.annotation.implicitNotFound

object codec {
  /**
   * Typeclass defining decoding and encoding together via [[e.codec.Decoder]] and [[e.codec.Encoder]]
   *
   * @tparam S Type of source
   * @tparam T Type of target
   */
  @implicitNotFound(
    """|No implicit instance is found of type e.codec.Codec[${S}, ${T}]. You may try following:
       |
       |* Make sure an instance of correct types is in scope (missing import?)
       |* Implement an implicit instance yourself
       |* Ensure both e.codec.Decoder[${T}, ${S}] and e.codec.Encoder[${S}, ${T}] are in scope
    """.stripMargin
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

  /**
   * Typeclass defining how to decode an input value to an output value, possibly failing with E
   *
   * @tparam I Type of input
   * @tparam O Type of output
   *
   * @see [[e.E]]
   * @see [[e.EOr]]
   */
  @implicitNotFound(
    """|No implicit instance is found of type e.codec.Decoder[${I}, ${O}]. You may try following:
       |
       |* Make sure an instance of correct types is in scope (missing import?)
       |* Implement an implicit instance yourself
    """.stripMargin
  )
  trait Decoder[-I, +O] {
    /**
     * Decodes an input, possibly failing with E
     *
     * @param input An input
     *
     * @return Decoded output or E
     *
     * @see [[e.EOr]]
     */
    def decode(input: I): O or E
  }

  object Decoder {
    /**
     * A default E to be used when decoding fails
     */
    val decodingError: E = E.name("decoding-error").message("Failed to decode!")

    /**
     * Summons an implicit decoder
     *
     * @param decoder Implicit instance of decoder
     *
     * @tparam I Type of input
     * @tparam O Type of output
     *
     * @return Provided implicit decoder
     */
    def apply[I, O](implicit decoder: Decoder[I, O]): Decoder[I, O] = decoder
  }

  implicit class DecoderSyntax[I, O](input: I)(implicit decoder: Decoder[I, O]) {
    /**
     * Decodes this value using given implicit decoder
     *
     * @return Decoded output or E
     */
    def decode: O or E = decoder.decode(input)
  }

  /**
   * Typeclass defining how to encode an input value to an output value
   *
   * @tparam I Type of input
   * @tparam O Type of output
   */
  @implicitNotFound(
    """|No implicit instance is found of type e.codec.Encoder[${I}, ${O}]. You may try following:
       |
       |* Make sure an instance of correct types is in scope (missing import?)
       |* Implement an implicit instance yourself
    """.stripMargin
  )
  trait Encoder[-I, +O] {
    /**
     * Encodes an input
     *
     * @param input An input
     *
     * @return Encoded output
     */
    def encode(input: I): O
  }

  object Encoder {
    /**
     * Summons an implicit encoder
     *
     * @param encoder Implicit instance of encoder
     *
     * @tparam I Type of input
     * @tparam O Type of output
     *
     * @return Provided implicit encoder
     */
    def apply[I, O](implicit encoder: Encoder[I, O]): Encoder[I, O] = encoder
  }

  implicit class EncoderSyntax[I, O](input: I)(implicit encoder: Encoder[I, O]) {
    /**
     * Encodes this value using given implicit encoder
     *
     * @return Encoded output
     */
    def encode: O = encoder.encode(input)
  }
}
