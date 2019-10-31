package dev.akif.e

import java.util.{HashMap => JHMap, Map => JMap}

import dev.akif.e.syntax._
import play.api.libs.json._

import scala.language.implicitConversions
import scala.util.Try

package object playjson {
  implicit val decoderEJsValue: DecoderE[JsValue] = {
    case obj: JsObject =>
      val code    = (obj \ "code").asOpt[Int].getOrElse(0)
      val name    = (obj \ "name").asOpt[String].getOrElse("")
      val message = (obj \ "message").asOpt[String].getOrElse("")
      val data    = (obj \ "data").asOpt[Map[String, String]].getOrElse(Map.empty)

      // Cannot know cause field because it isn't possible to construct the causing exception from just a serialized message string
      E.of(code, name, message, null, mapScala2Java(data))

    case json =>
      throw new DecodingFailure(s"'$json' is not a Json object!")
  }

  implicit val eReads: Reads[E] = Reads { json =>
    Try(decoderEJsValue.decodeOrThrow(json)).fold(
      t => JsError(JsPath(), t.getMessage),
      e => JsSuccess(e)
    )
  }

  implicit class JsValueExtensions(private val json: JsValue) {
    def readOrE[A](makeE: JsError => E)(implicit aReads: Reads[A]): Maybe[A] =
      aReads.reads(json) match {
        case e: JsError      => makeE(e).maybe[A]
        case JsSuccess(a, _) => a.maybe
      }
  }

  implicit val encoderEJsValue: EncoderE[JsValue] = { e: E =>
    val jsons = List(
      if (!e.hasCode)    Json.obj() else Json.obj("code"    -> e.code),
      if (!e.hasName)    Json.obj() else Json.obj("name"    -> e.name),
      if (!e.hasMessage) Json.obj() else Json.obj("message" -> e.message),
      if (!e.hasCause)   Json.obj() else Json.obj("cause"   -> e.cause.getMessage),
      if (!e.hasData)    Json.obj() else Json.obj("data"    -> mapJava2Scala(e.data))
    )

    jsons.foldLeft(Json.obj())(_ ++ _)
  }

  implicit val eWrites: Writes[E] = Writes(encoderEJsValue.encode)

  implicit def maybeWrites[A: Writes]: Writes[Maybe[A]] =
    Writes {
      case Left(e)  => encoderEJsValue.encode(e)
      case Right(a) => Json.toJson(a)
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
}
