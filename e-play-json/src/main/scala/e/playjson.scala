package e

import cats.{Functor, Semigroupal}
import cats.implicits._
import e.scala._
import e.scala.codec.{CodecFor, Decoder}
import play.api.libs.json._

object playjson extends CodecFor[JsValue, Reads, Writes] {
  override implicit val eDecoder: Reads[E] = Reads { json: JsValue =>
    json.validate[JsObject].flatMap { obj =>
      (
        (JsPath \ "code").readNullable[Int].reads(obj),
        (JsPath \ "name").readNullable[String].reads(obj),
        (JsPath \ "message").readNullable[String].reads(obj),
        (JsPath \ "causes").readNullable[List[E]].reads(obj),
        (JsPath \ "data").readNullable[Map[String, String]].reads(obj),
        (JsPath \ "time").readNullable[Long].reads(obj)
      ).mapN {
        case (code, name, message, causes, data, time) =>
          E(code, name, message, causes.getOrElse(List.empty[E]), data.getOrElse(Map.empty[String, String]), time)
      }
    }
  }

  override implicit val eEncoder: Writes[E] = Writes { e: E =>
    val empty = Json.obj()

    e.code.fold(empty)(c => Json.obj("code" -> c)) ++
    e.name.fold(empty)(n => Json.obj("name" -> n)) ++
    e.message.fold(empty)(m => Json.obj("message" -> m)) ++
    (if (!e.hasCause) empty else Json.obj("causes" -> Json.toJson(e.causes))) ++
    (if (!e.hasData)  empty else Json.obj("data" -> Json.toJson(e.data))) ++
    e.time.fold(empty)(t => Json.obj("time" -> t))
  }

  override def decode[A](json: JsValue)(implicit aReads: Reads[A]): EOr[A] =
    aReads.reads(json).asEither.orE { errors =>
      errors.foldLeft[E](Decoder.decodingError) {
        case (e, (path, validationErrors)) =>
          val name   = path.toJsonString
          val causes = validationErrors.flatMap(_.messages.map(message => E(name = Some(name), message = Some(message)))).toList
          e.causes(causes)
      }
    }

  override def encode[A](a: A)(implicit aWrites: Writes[A]): JsValue = aWrites.writes(a)

  private implicit val jsResultFunctor: Functor[JsResult] =
    new Functor[JsResult] {
      override def map[A, B](fa: JsResult[A])(f: A => B): JsResult[B] = fa.map(f)
    }

  private implicit val jsResultSemigroupal: Semigroupal[JsResult] =
    new Semigroupal[JsResult] {
      override def product[A, B](fa: JsResult[A], fb: JsResult[B]): JsResult[(A, B)] =
        (fa, fb) match {
          case (JsError(ea), JsError(eb))           => JsError(ea ++ eb)
          case (JsError(ea), _)                     => JsError(ea)
          case (_, JsError(eb))                     => JsError(eb)
          case (JsSuccess(a, ap), JsSuccess(b, bp)) => JsSuccess((a, b), ap ++ bp)
        }
    }
}
