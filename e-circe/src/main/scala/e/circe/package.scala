package e

import e.scala.implicits._
import e.scala.{E, Maybe}
import io.circe.syntax._
import io.circe.{Decoder, DecodingFailure, Encoder, Json}

package object circe {
  implicit val circeEncoderE: Encoder[E] =
    Encoder.instance(CodecForCirceJson.encode)

  implicit val circeDecoderE: Decoder[E] =
    Decoder.decodeJson.emap(json => CodecForCirceJson.decodeEither(json).left.map(_.toString))

  implicit def circeEncoderMaybe[A: Encoder]: Encoder[Maybe[A]] =
    Encoder.instance[Maybe[A]] {
      case Maybe.Failure(e) => circeEncoderE.apply(e)
      case Maybe.Success(a) => a.asJson
    }

  implicit class JsonExtensions(private val json: Json) {
    def decodeMaybe[A](ifFailure: DecodingFailure => E)(implicit circeDecoderA: Decoder[A]): Maybe[A] =
      circeDecoderA.decodeJson(json).fold(
        df => ifFailure(df).toMaybe[A],
        a  => a.toMaybe
      )
  }
}
