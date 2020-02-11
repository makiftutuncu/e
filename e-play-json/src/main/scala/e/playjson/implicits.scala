package e.playjson

import e.scala.implicits._
import e.scala.{E, Maybe}
import play.api.libs.json._

object implicits {
  implicit val readsE: Reads[E] =
    Reads { json =>
      CodecForPlayJson.decodeEither(json).fold(
        decodingFailure => JsError(decodingFailure.toString),
        decoded         => JsSuccess(decoded)
      )
    }

  implicit val writesE: Writes[E] = Writes(CodecForPlayJson.encode)

  implicit def writesMaybe[A: Writes]: Writes[Maybe[A]] =
    Writes {
      case Maybe.Failure(e) => writesE.writes(e)
      case Maybe.Success(a) => Json.toJson(a)
    }

  implicit class JsValueExtensions(private val json: JsValue) {
    def readMaybe[A](ifFailure: JsError => E)(implicit readsA: Reads[A]): Maybe[A] =
      readsA.reads(json) match {
        case e: JsError      => ifFailure(e).toMaybe[A]
        case JsSuccess(a, _) => a.toMaybe
      }
  }
}
