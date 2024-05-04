package e

import cats.{Functor, Semigroupal}
import cats.syntax.all.*
import e.scala.*
import e.scala.codec.{CodecFor, Decoder}
import play.api.libs.json.*

object playjson extends CodecFor[JsValue, Reads, Writes]:
    given jsResultFunctor: Functor[JsResult] =
        new Functor[JsResult]:
            override def map[A, B](fa: JsResult[A])(f: A => B): JsResult[B] = fa.map(f)

    given jsResultSemigroupal: Semigroupal[JsResult] =
        new Semigroupal[JsResult]:
            override def product[A, B](fa: JsResult[A], fb: JsResult[B]): JsResult[(A, B)] =
                (fa, fb) match
                    case (JsError(ea), JsError(eb)) => JsError(ea ++ eb)
                    case (JsError(ea), _) => JsError(ea)
                    case (_, JsError(eb)) => JsError(eb)
                    case (JsSuccess(a, ap), JsSuccess(b, bp)) => JsSuccess((a, b), ap ++ bp)

    override given eDecoder: Reads[E] =
        Reads: (json: JsValue) =>
            json.validate[JsObject]
                .flatMap: obj =>
                    (
                      (JsPath \ "code").readNullable[Int].reads(obj),
                      (JsPath \ "name").readNullable[String].reads(obj),
                      (JsPath \ "message").readNullable[String].reads(obj),
                      (JsPath \ "causes").readNullable[List[E]](using Reads.list[E](using eDecoder)).reads(obj),
                      (JsPath \ "data").readNullable[Map[String, String]].reads(obj),
                      (JsPath \ "time").readNullable[Long].reads(obj)
                    ).mapN: (code, name, message, causes, data, time) =>
                        E(
                          code,
                          name,
                          message,
                          causes.getOrElse(List.empty[E]),
                          data.getOrElse(Map.empty[String, String]),
                          time
                        )

    override given eEncoder: Writes[E] =
        Writes: (e: E) =>
            val empty = Json.obj()

            e.code.fold(empty)(c => Json.obj("code" -> c)) ++
                e.name.fold(empty)(n => Json.obj("name" -> n)) ++
                e.message.fold(empty)(m => Json.obj("message" -> m)) ++
                (if !e.hasCause then empty else Json.obj("causes" -> Json.toJson(e.causes)(using Writes.list[E](using eEncoder)))) ++
                (if !e.hasData then empty else Json.obj("data" -> Json.toJson(e.data))) ++
                e.time.fold(empty)(t => Json.obj("time" -> t))

    given eorEncoder[A](using aWrites: Writes[A]): Writes[EOr[A]] =
        Writes:
            case EOr.Failure(e) => encode(e)
            case EOr.Success(a) => encode(a)

    override def decode[A](json: JsValue)(using aReads: Reads[A]): EOr[A] =
        aReads
            .reads(json)
            .asEither
            .toEOr: errors =>
                errors.foldLeft[E](Decoder.decodingError):
                    case (e, (path, validationErrors)) =>
                        val name = path.toJsonString
                        val causes = validationErrors
                            .flatMap(_.messages.map(message => E(name = Some(name), message = Some(message))))
                            .toList
                        e.causes(causes)

    override def encode[A](a: A)(using aWrites: Writes[A]): JsValue = aWrites.writes(a)
