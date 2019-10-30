package dev.akif.e

import java.util.{HashMap => JHMap, Map => JMap}

import dev.akif.e.syntax._
import io.circe.syntax._
import io.circe.{Decoder, Encoder, Json, JsonObject}

import scala.language.implicitConversions
import scala.util.Try

package object circe {
  implicit val encoderEJson: EncoderE[Json] = { e: E =>
    val jsons = List(
      if (!e.hasCode)    None else Some(Json.obj("code"    := e.code)),
      if (!e.hasName)    None else Some(Json.obj("name"    := e.name)),
      if (!e.hasMessage) None else Some(Json.obj("message" := e.message)),
      if (!e.hasCause)   None else Some(Json.obj("cause"   := e.cause.getMessage)),
      if (!e.hasData)    None else Some(Json.obj("data"    := map2Json(mapJava2Scala(e.data))))
    )

    jsons.foldLeft(Json.obj()) {
      case (result, Some(json)) => result deepMerge json
      case (result, _)          => result
    }
  }

  implicit val encoderECirce: Encoder[E] = Encoder.instance(encoderEJson.encode)

  implicit def encoderMaybeCirce[A](implicit encoderACirce: Encoder[A]): Encoder[Maybe[A]] =
    Encoder.instance {
      case Left(e)  => encoderECirce.apply(e)
      case Right(a) => encoderACirce.apply(a)
    }

  implicit val decoderEJson: DecoderE[Json] = { json: Json =>
    json.as[JsonObject] match {
      case Left(_) =>
        throw new DecodingFailure(s"'$json' is not a Json object!")

      case Right(obj) =>
        val code    = obj("code").flatMap(_.as[Int].toOption).getOrElse(0)
        val name    = obj("name").flatMap(_.asString).getOrElse("")
        val message = obj("message").flatMap(_.asString).getOrElse("")
        val data    = obj("data").flatMap(_.as[Map[String, String]].toOption).getOrElse(Map.empty)

        // Cannot know cause field because it isn't possible to construct the causing exception from just a serialized message string
        new E(code, name, message, null, mapScala2Java(data))
    }
  }

  implicit val decoderECirce: Decoder[E] = Decoder.decodeJson.emapTry(json => Try(decoderEJson.decodeOrThrow(json)))

  implicit class JsonExtensions(private val json: Json) {
    def decodeOrE[A](makeE: io.circe.DecodingFailure => E)(implicit decoderACirce: Decoder[A]): Maybe[A] =
      decoderACirce.decodeJson(json).fold(
        df => makeE(df).maybe[A],
        a  => a.maybe
      )
  }

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
