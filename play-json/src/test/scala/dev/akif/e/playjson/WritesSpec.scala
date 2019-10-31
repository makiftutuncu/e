package dev.akif.e.playjson

import java.util.{HashMap => JMap}

import dev.akif.e.E
import dev.akif.e.syntax._
import org.scalatest.{Matchers, WordSpec}
import play.api.libs.json.Json

class WritesSpec extends WordSpec with Matchers {
  "Play Json Writes for E" should {
    "write an E as JsValue" in {
      val d = new JMap[String, String]
      d.put("foo", "bar")
      val e = E.of(1, "test", "Test Message", null, d)

      val expected = Json.obj("code" -> 1, "name" -> "test", "message" -> "Test Message", "data" -> Json.obj("foo" -> "bar"))
      val actual   = Json.toJson(e)

      actual shouldBe expected
    }

    "write a Maybe with E in it as JsValue" in {
      val d = new JMap[String, String]
      d.put("foo", "bar")
      val e = E.of(1, "test", "Test Message", null, d)

      val expected = Json.obj("code" -> 1, "name" -> "test", "message" -> "Test Message", "data" -> Json.obj("foo" -> "bar"))
      val actual   = Json.toJson(e.maybe[String])

      actual shouldBe expected
    }

    "write a Maybe with a value in it as JsValue" in {
      val a = Map("foo" -> "bar")

      val expected = Json.obj("foo" -> "bar")
      val actual   = Json.toJson(a.maybe)

      actual shouldBe expected
    }
  }
}
