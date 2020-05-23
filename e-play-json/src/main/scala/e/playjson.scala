package e

import e.codec.{Codec, Decoder, Encoder}
import play.api.libs.json._

object playjson {
  implicit val eReads: Reads[E] = Json.reads[E]

  implicit val eWrites: Writes[E] = Json.writes[E]

  implicit def eOrReads[A](implicit aReads: Reads[A]): Reads[A or E] =
    Reads.apply[A or E] { json =>
      eReads.reads(json).fold(
        _ => aReads.reads(json).fold(JsError.apply, a => JsSuccess(a.orE)),
        e => JsSuccess(e.as[A])
      )
    }

  implicit def eOrWrites[A](implicit aWrites: Writes[A]): Writes[A or E] =
    Writes.apply[A or E] { eor =>
      eor.fold[JsValue](eWrites.writes, implicitly[Writes[A]].writes)
    }

  implicit def jsonDecoder[A](implicit aReads: Reads[A]): Decoder[JsValue, A] = { json: JsValue =>
    aReads.reads(json).asEither.orE { errors =>
      errors.foldLeft[E](Decoder.decodingError) {
        case (e, (path, validationErrors)) =>
          val key   = path.toJsonString
          val value = validationErrors.flatMap(_.messages).mkString("[", ", ", "]")
          e.cause(E.name("detail").data(key, value))
      }
    }
  }

  implicit def jsonEncoder[A](implicit aWrites: Writes[A]): Encoder[A, JsValue] = { a: A =>
    aWrites.writes(a)
  }

  implicit def jsonCodec[A: Reads : Writes]: Codec[A, JsValue] =
    new Codec[A, JsValue] {
      override def encode(input: A): JsValue      = jsonEncoder[A].encode(input)
      override def decode(input: JsValue): A or E = jsonDecoder[A].decode(input)
    }
}
