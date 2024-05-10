package e

import cats.syntax.all.*
import e.scala.*
import e.scala.codec.{CodecFor, Decoder}
import io.circe.CursorOp.DownField
import io.circe.{Decoder => CirceDecoder, Encoder => CirceEncoder, _}
import io.circe.syntax.*

object circe extends CodecFor[Json, CirceDecoder, CirceEncoder]:
    override given eDecoder: CirceDecoder[E] =
        rootObjectDecoder:
            (
              fieldDecoder[Int]("code"),
              fieldDecoder[String]("name"),
              fieldDecoder[String]("message"),
              fieldDecoder[List[E]]("causes")(using CirceDecoder.decodeList[E](using eDecoder)),
              fieldDecoder[Map[String, String]]("data"),
              fieldDecoder[Long]("time")
            ).mapN: (code, name, message, causes, data, time) =>
                E(code, name, message, causes.getOrElse(List.empty[E]), data.getOrElse(Map.empty[String, String]), time)

    override given eEncoder: CirceEncoder[E] =
        CirceEncoder.instance: e =>
            Json.obj(
              "code" := e.code,
              "name" := e.name,
              "message" := e.message,
              "causes" := (if e.hasCause then e.causes.asJson(using CirceEncoder.encodeList[E](using eEncoder))
                           else Json.Null),
              "data" := (if e.hasData then e.data.asJson else Json.Null),
              "time" := e.time
            ).dropNullValues

    given eorEncoder[A](using aEncoder: CirceEncoder[A]): CirceEncoder[EOr[A]] =
        CirceEncoder.instance:
            case EOr.Failure(e) => encode(e)
            case EOr.Success(a) => encode(a)

    override def decode[A](json: Json)(using aDecoder: CirceDecoder[A]): EOr[A] =
        aDecoder
            .decodeAccumulating(HCursor.fromJson(json))
            .toEither
            .toEOr: failures =>
                failures.foldLeft(Decoder.decodingError):
                    case (e, failure) =>
                        e.cause(
                          E(
                            name =
                                if failure.history.isEmpty then None
                                else Option(CursorOp.opsToPath(failure.history)),
                            message = Some(failure.message)
                          )
                        )

    override def encode[A](a: A)(using aEncoder: CirceEncoder[A]): Json = aEncoder.apply(a)

    private def rootObjectDecoder[A](decoder: => CirceDecoder[A]): CirceDecoder[A] =
        CirceDecoder.decodeJson.flatMap[A]: json =>
            if !json.isObject then CirceDecoder.failed(DecodingFailure("Expected: JsonObject", List.empty))
            else decoder

    private def fieldDecoder[A](field: String)(using aDecoder: CirceDecoder[A]): CirceDecoder[Option[A]] =
        CirceDecoder.instance: cursor =>
            cursor.downField(field).focus.filterNot(_.isNull) match
                case None =>
                    Right(Option.empty[A])

                case Some(j) =>
                    CirceDecoder[A]
                        .decodeJson(j)
                        .fold[CirceDecoder.Result[Option[A]]](
                          failure =>
                              val message = field match
                                  case "causes" => "List[E]"
                                  case "data"   => "Map[String, String]"
                                  case _        => failure.message
                              Left(DecodingFailure(s"Expected: $message", List(DownField(field))))
                          ,
                          a => Right(Some(a))
                        )
