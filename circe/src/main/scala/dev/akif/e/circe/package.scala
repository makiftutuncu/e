package dev.akif.e

import java.util.{HashMap => JHMap, Map => JMap}

import dev.akif.e.codec.{Decoder, DecodingError, Encoder}
import dev.akif.e.syntax._
import io.circe.syntax._
import io.circe.{DecodingFailure, Json, JsonObject, Decoder => CirceDecoder, Encoder => CirceEncoder}

import scala.language.implicitConversions

package object circe {
  implicit val decoderJson: Decoder[Json] =
    new Decoder[Json] {
      override def decodeOrThrow(json: Json): E =
        json.as[JsonObject] match {
          case Left(_) =>
            throw new DecodingError(s"'$json' is not a Json object!", null)

          case Right(obj) =>
            val code    = obj("code").flatMap(_.as[Int].toOption).getOrElse(0)
            val name    = obj("name").flatMap(_.asString).getOrElse("")
            val message = obj("message").flatMap(_.asString).getOrElse("")
            val data    = obj("data").flatMap(_.as[Map[String, String]].toOption).getOrElse(Map.empty)

            // Cannot know cause field because it isn't possible to construct the causing exception from just a serialized message string
            E.of(code, name, message, null, mapScala2Java(data))
        }
    }

  implicit val decoderCirce: CirceDecoder[E] = CirceDecoder.decodeJson.emap(json => decoderJson.decode(json).left.map(_.getMessage))

  implicit val encoderJson: Encoder[Json] =
    new Encoder[Json] {
      override def encode(e: E): Json = {
        val jsons = List(
          Some(Json.obj("code" := e.getCode)),
          if (!e.hasName)    None else Some(Json.obj("name"    := e.getName)),
          if (!e.hasMessage) None else Some(Json.obj("message" := e.getMessage)),
          if (!e.hasCause)   None else Some(Json.obj("cause"   := e.getCause.getMessage)),
          if (!e.hasData)    None else Some(Json.obj("data"    := map2Json(mapJava2Scala(e.getData))))
        )

        jsons.foldLeft(Json.obj()) {
          case (result, Some(json)) => result deepMerge json
          case (result, _)          => result
        }
      }
    }

  implicit val encoderCirce: CirceEncoder[E] = CirceEncoder.instance(e => encoderJson.encode(e))

  implicit def encoderMaybeCirce[A](implicit encoderACirce: CirceEncoder[A]): CirceEncoder[Maybe[A]] =
    CirceEncoder.instance {
      case Left(e)  => encoderCirce.apply(e)
      case Right(a) => encoderACirce.apply(a)
    }

  implicit class JsonExtensions(private val json: Json) {
    def decodeOrE[A](makeE: DecodingFailure => E)(implicit decoderACirce: CirceDecoder[A]): Maybe[A] =
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
