package e

import e.codec.{Codec, Decoder, Encoder}
import io.circe.{Json, Decoder => CirceDecoder, Encoder => CirceEncoder}

object circe {
  implicit val eCirceDecoder: CirceDecoder[E] =
    CirceDecoder.forProduct6[E, Option[Int], Option[String], Option[String], List[E], Map[String, String], Option[Long]](
      "code", "name", "message", "causes", "data", "time"
    ) {
      case (code, name, message, causes, data, time) => E(code, name, message, causes, data, time)
    }

  implicit val eCirceEncoder: CirceEncoder[E] =
    CirceEncoder.forProduct6("code", "name", "message", "causes", "data", "time") { e =>
      (e.code, e.name, e.message, e.causes, e.data, e.time)
    }

  implicit def eOrCirceDecoder[A: CirceDecoder]: CirceDecoder[A or E] =
    eCirceDecoder.either(CirceDecoder[A]).map(_.fold(e => e.as[A], a => a.orE))

  implicit def eOrCirceEncoder[A: CirceEncoder]: CirceEncoder[A or E] =
    CirceEncoder.instance[A or E] { eor =>
      eor.fold[Json](eCirceEncoder.apply, CirceEncoder[A].apply)
    }

  implicit def jsonDecoder[A: CirceDecoder]: Decoder[Json, A] = { json: Json =>
    CirceDecoder[A].decodeJson(json).orE { df =>
      Decoder.decodingError.data("reason", df.message)
    }
  }

  implicit def jsonEncoder[A: CirceEncoder]: Encoder[A, Json] = { a: A =>
    CirceEncoder[A].apply(a)
  }

  implicit def jsonCodec[A: CirceDecoder : CirceEncoder]: Codec[A, Json] =
    new Codec[A, Json] {
      override def encode(input: A): Json      = jsonEncoder[A].encode(input)
      override def decode(input: Json): A or E = jsonDecoder[A].decode(input)
    }
}
