package e

import cats.implicits._
import e.codec.{CodecFor, Decoder}
import io.circe.CursorOp.DownField
import io.circe.{Decoder => CirceDecoder, Encoder => CirceEncoder, _}
import io.circe.syntax._

object circe extends CodecFor[Json, CirceDecoder, CirceEncoder] {
  override implicit val eDecoder: CirceDecoder[E] =
    rootObjectDecoder {
      (
        fieldDecoder[Int]("code"),
        fieldDecoder[String]("name"),
        fieldDecoder[String]("message"),
        fieldDecoder[List[E]]("causes"),
        fieldDecoder[Map[String, String]]("data"),
        fieldDecoder[Long]("time")
      ).mapN {
        case (code, name, message, causes, data, time) =>
          E(code, name, message, causes.getOrElse(List.empty[E]), data.getOrElse(Map.empty[String, String]), time)
      }
    }

  override implicit val eEncoder: CirceEncoder[E] =
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

  override def decode[A](json: Json)(implicit aDecoder: CirceDecoder[A]): EOr[A] =
    aDecoder.decodeAccumulating(HCursor.fromJson(json)).toEither.orE { failures =>
      failures.foldLeft(Decoder.decodingError) {
        case (e, failure) =>
          e.cause(
            E(
              name    = Option.when(failure.history.nonEmpty)(CursorOp.opsToPath(failure.history)),
              message = Some(failure.message)
            )
          )
      }
    }

  override def encode[A](a: A)(implicit aEncoder: CirceEncoder[A]): Json = aEncoder.apply(a)

  private def rootObjectDecoder[A](decoder: => CirceDecoder[A]): CirceDecoder[A] =
    CirceDecoder.decodeJson.flatMap[A] { json =>
      if (!json.isObject) {
        CirceDecoder.failed(DecodingFailure("Expected: JsonObject", List.empty))
      } else {
        decoder
      }
    }

  private def fieldDecoder[A](field: String)(implicit aDecoder: CirceDecoder[A]): CirceDecoder[Option[A]] =
    CirceDecoder.instance { cursor =>
      cursor.downField(field).focus.filterNot(_.isNull) match {
        case None =>
          Right(Option.empty[A])

        case Some(j) =>
          CirceDecoder[A].decodeJson(j).fold[CirceDecoder.Result[Option[A]]](
            failure => Left(
              DecodingFailure(
                "Expected: " + (field match {
                  case "causes" => "List[E]"
                  case "data"   => "Map[String, String]"
                  case _        => failure.message
                }),
                List(DownField(field))
              )
            ),
            a => Right(Some(a))
          )
      }
    }
}
