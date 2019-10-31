package dev.akif.e.circe

import java.util.{HashMap => JMap}

import dev.akif.e.E
import dev.akif.e.syntax._
import io.circe.Json
import io.circe.syntax._
import org.scalatest.{Matchers, WordSpec}

class CirceEncoderSpec extends WordSpec with Matchers {
  "Circe Encoder for E" should {
    "encode an E as Json" in {
      val d = new JMap[String, String]
      d.put("foo", "bar")
      val e = E.of(1, "test", "Test Message", null, d)

      val expected = Json.obj("code" := 1, "name" := "test", "message" := "Test Message", "data" -> Json.obj("foo" := "bar"))
      val actual   = e.asJson

      actual shouldBe expected
    }

    "encode a Maybe with E in it as Json" in {
      val d = new JMap[String, String]
      d.put("foo", "bar")
      val e = E.of(1, "test", "Test Message", null, d)

      val expected = Json.obj("code" := 1, "name" := "test", "message" := "Test Message", "data" -> Json.obj("foo" := "bar"))
      val actual   = e.maybe[String].asJson

      actual shouldBe expected
    }

    "encode a Maybe with a value in it as Json" in {
      val a = Map("foo" -> "bar")

      val expected = Json.obj("foo" := "bar")
      val actual   = a.maybe.asJson

      actual shouldBe expected
    }
  }
}
