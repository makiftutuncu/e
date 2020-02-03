package dev.akif.e

import dev.akif.e.scala.{E, Maybe}
import dev.akif.e.scala.implicits._
import io.circe.{Decoder, DecodingFailure, Encoder, Json}

package object circe {
  implicit val circeEncoderE: Encoder[E] =
    Encoder.instance(CodecForCirceJson.encode)

  implicit val circeDecoderE: Decoder[E] =
    Decoder.decodeJson.emap(json => CodecForCirceJson.decodeEither(json).left.map(_.toString))

  implicit class JsonExtensions(private val json: Json) {
    def decodeOrE[A](makeE: DecodingFailure => E)(implicit circeDecoderA: Decoder[A]): Maybe[A] =
      circeDecoderA.decodeJson(json).fold(
        df => makeE(df).maybe[A],
        a  => a.maybe
      )
  }
}
