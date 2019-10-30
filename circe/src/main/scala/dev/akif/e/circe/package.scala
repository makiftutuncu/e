package dev.akif.e

import java.util.{Map => JMap, HashMap => JHMap}

import io.circe.{Decoder, Encoder, Json, JsonObject}

import scala.util.Try

package object circe {
  implicit val encoderEJson: EncoderE[Json] = { e: E =>
    val jsons = List(
      if (!e.hasCode)    Json.Null else Json.obj("code"    -> Json.fromInt(e.code)),
      if (!e.hasName)    Json.Null else Json.obj("name"    -> Json.fromString(e.name)),
      if (!e.hasMessage) Json.Null else Json.obj("message" -> Json.fromString(e.message)),
      if (!e.hasCause)   Json.Null else Json.obj("cause"   -> Json.fromString(e.cause.getMessage)),
      if (!e.hasData)    Json.Null else Json.obj("data"    -> map2Json(mapJava2Scala(e.data)))
    )

    jsons.foldLeft(Json.obj()) {
      case (result, json) if json != Json.Null => result deepMerge json
      case (result, _)                         => result
    }
  }

  implicit val encoderECirce: Encoder[E] = Encoder.instance(encoderEJson.encode)

  implicit val decoderEJson: DecoderE[Json] = { json: Json =>
    json.as[JsonObject] match {
      case Left(_) =>
        throw new DecodingFailure(s"'$json' is not a Json object!")

      case Right(obj) =>
        val code    = obj("code").flatMap(_.asNumber).flatMap(_.toInt).getOrElse(0)
        val name    = obj("name").flatMap(_.asString).getOrElse("")
        val message = obj("message").flatMap(_.asString).getOrElse("")
        val data    = obj("data").flatMap(_.as[Map[String, String]].toOption).getOrElse(Map.empty)

        // Cannot know cause field because it isn't possible to construct the causing exception from just a serialized message string
        new E(code, name, message, null, mapScala2Java(data))
    }
  }

  implicit val decoderECirce: Decoder[E] = Decoder.decodeJson.emapTry(json => Try(decoderEJson.decodeOrThrow(json)))

  private def mapJava2Scala(jMap: JMap[String, String]): Map[String, String] = {
    var map = Map.empty[String, String]
    val iterator = jMap.entrySet().iterator()
    while (iterator.hasNext) {
      val entry = iterator.next()
      map += (entry.getKey -> entry.getValue)
    }
    map
  }

  private def mapScala2Java(map: Map[String, String]): JMap[String, String] =
    map.foldLeft(new JHMap[String, String]) {
      case (m, (k, v)) =>
        m.put(k, v);
        m
    }

  private def map2Json(map: Map[String, String]): Json =
    Json.fromFields {
      map.map {
        case (key, value) =>
          key -> Json.fromString(value)
      }
    }
}
