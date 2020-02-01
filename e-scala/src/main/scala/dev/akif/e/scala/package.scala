package dev.akif.e

package object scala {
  type Maybe[+A] = Either[E, A]

  trait Decoder[IN] extends AbstractDecoder[IN, E]

  trait Encoder[OUT] extends AbstractEncoder[E, OUT]

  trait Codec[IN, OUT] extends AbstractCodec[E, IN, OUT]

  implicit class DecoderExtensions[A](private val decoder: Decoder[A]) {
    def decodeEither(a: A): Either[E, E] = {
      val result        = decoder.decode(a)
      val decodingError = result.decodingError()
      val decoded       = result.get()

      if (decodingError.isPresent) {
        Left(decodingError.get())
      } else if (decoded.isPresent) {
        Right(decoded.get())
      } else {
        val e = E.empty
          .name("invalid-decoder")
          .message("Invalid Decoder because it contains neither a decoding error nor a decoded E!")
          .data("class", decoder.getClass.getCanonicalName)
          .data("input", a.toString)

        Left(e)
      }
    }
  }

  implicit class MaybeExtensions[A](private val maybe: Maybe[A]) {
    val isError: Boolean  = maybe.isLeft
    val hasError: Boolean = isError

    val isValue: Boolean  = maybe.isRight
    val hasValue: Boolean = isValue
  }
}
