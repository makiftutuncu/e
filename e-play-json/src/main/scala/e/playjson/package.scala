package e

import e.scala.implicits._
import e.scala.{E, Maybe}
import play.api.libs.json._

package object playjson {
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
      case Left(e)  => writesE.writes(e)
      case Right(a) => Json.toJson(a)
    }

  implicit class JsValueExtensions(private val json: JsValue) {
    def readOrE[A](makeE: JsError => E)(implicit readsA: Reads[A]): Maybe[A] =
      readsA.reads(json) match {
        case e: JsError      => makeE(e).maybe[A]
        case JsSuccess(a, _) => a.maybe
      }
  }
}
