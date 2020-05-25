package e

import cats.implicits._
import e.codec.{CodecFor, Decoder, Encoder}
import io.circe.CursorOp.DownField
import io.circe.{Decoder => CirceDecoder, Encoder => CirceEncoder, _}
import io.circe.syntax._

object circe extends CodecFor[Json, CirceDecoder, CirceEncoder] {
  override implicit def jsonDecoder[A: CirceDecoder]: Decoder[Json, A] = { json: Json =>
    CirceDecoder[A].decodeAccumulating(HCursor.fromJson(json)).toEither.orE { failuresNel =>
      val causes = failuresNel.foldLeft(List.empty[E]) {
        case (causes, failure) =>
          causes :+ E(
            name    = Option.when(failure.history.nonEmpty)(CursorOp.opsToPath(failure.history)),
            message = Some(s"Expected: ${failure.message}")
          )
      }

      Decoder.decodingError.causes(causes)
    }
  }

  override implicit def jsonEncoder[A: CirceEncoder]: Encoder[A, Json] = { a: A =>
    CirceEncoder[A].apply(a)
  }

  implicit val eCirceDecoder: CirceDecoder[E] =
    CirceDecoder.instance { cursor =>
      decodeIfObject(cursor) { _ =>
        (
          decodeIfExists[Int](cursor, "code"),
          decodeIfExists[String](cursor, "name"),
          decodeIfExists[String](cursor, "message"),
          decodeIfExists[List[E]](cursor, "causes"),
          decodeIfExists[Map[String, String]](cursor, "data"),
          decodeIfExists[Long](cursor, "time")
        ).mapN {
          case (code, name, message, causes, data, time) =>
            E(code, name, message, causes.getOrElse(List.empty[E]), data.getOrElse(Map.empty[String, String]), time)
        }
      }
    }

  implicit val eCirceEncoder: CirceEncoder[E] =
    CirceEncoder.instance { e =>
      Json.obj(
        "code"    := e.code,
        "name"    := e.name,
        "message" := e.message,
        "causes"  := (if (e.hasCause) e.causes.asJson else Json.Null),
        "data"    := (if (e.hasData)  e.data.asJson   else Json.Null),
        "time"    := e.time
      ).dropNullValues
    }

  implicit def eOrCirceDecoder[A: CirceDecoder]: CirceDecoder[A or E] =
    eCirceDecoder.either(CirceDecoder[A]).map(_.fold(e => e.as[A], a => a.orE))

  implicit def eOrCirceEncoder[A: CirceEncoder]: CirceEncoder[A or E] =
    CirceEncoder.instance[A or E] { eor =>
      eor.fold[Json](eCirceEncoder.apply, CirceEncoder[A].apply)
    }

  private def decodeIfObject[A: CirceDecoder](cursor: HCursor)(f: JsonObject => CirceDecoder.Result[A]): CirceDecoder.Result[A] =
    cursor.focus.flatMap(_.asObject) match {
      case None    => Left(DecodingFailure("JsonObject", List.empty))
      case Some(j) => f(j)
    }

  private def decodeIfExists[A: CirceDecoder](cursor: HCursor, field: String): CirceDecoder.Result[Option[A]] =
    cursor.downField(field).focus match {
      case None =>
        Right(None)

      case Some(j) =>
        CirceDecoder[Option[A]].decodeJson(j).fold(
          df => Left(DecodingFailure(
            field match {
              case "causes" => "List[E]"
              case "data"   => "Map[String, String]"
              case _        => df.message
            },
            List(DownField(field))
          )),
          a  => Right(a)
        )
    }
}
