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
      Option.when(e.hasName)(Json.obj("name" -> e.name)),
      Option.when(e.hasMessage)(Json.obj("message" -> e.message)),
      Option.when(e.hasCode)(Json.obj("code" -> e.code)),
      e.cause.fold[Option[JsObject]](None)(c => Some(Json.obj("cause" -> c.getMessage))),
      Option.when(e.hasData)(Json.obj("data" -> e.data)),
    )

    jsons.foldLeft(Json.obj()) {
      case (result, Some(json)) => result ++ json
      case (result, _)          => result
    }
  }
}
