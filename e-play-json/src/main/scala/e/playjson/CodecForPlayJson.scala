package e.playjson

import e.AbstractDecoder.DecodingResult
import e.scala.{Codec, E}
import play.api.libs.json.{JsObject, JsValue, Json}

object CodecForPlayJson extends Codec[JsValue] {
  override def decode(json: JsValue): DecodingResult[E] =
    json.asOpt[JsObject] match {
      case None =>
        val e = E("decoding-failure", "Input is not a Json object!").data("input" -> json.toString())

        DecodingResult.fail(e)

      case Some(obj) =>
        val name    = (obj \ "name").asOpt[String].getOrElse("")
        val code    = (obj \ "code").asOpt[Int].getOrElse(0)
        val message = (obj \ "message").asOpt[String].getOrElse("")
        val data    = (obj \ "data").asOpt[Map[String, String]].getOrElse(Map.empty)

        // Cannot know cause field because it isn't possible to construct the causing exception from just a serialized message string
        DecodingResult.succeed(E(name, message, code, None, data))
    }

  override def encode(e: E): JsValue = {
    val jsons = List(
      if (e.hasName) Some(Json.obj("name" -> e.name)) else None,
      if (e.hasMessage) Some(Json.obj("message" -> e.message)) else None,
      if (e.hasCode) Some(Json.obj("code" -> e.code)) else None,
      e.cause.map(c => Json.obj("cause" -> c.getMessage)),
      if (e.hasData) Some(Json.obj("data" -> e.data)) else None
    )

    jsons.foldLeft(Json.obj()) {
      case (result, Some(json)) => result ++ json
      case (result, _)          => result
    }
  }
}
