package dev.akif.e.circe

import java.util.{HashMap => JMap}

import dev.akif.e.E
import io.circe.Json
import org.scalatest.{Matchers, WordSpec}

class CirceEncoderSpec extends WordSpec with Matchers {
  "Circe Encoder for E" should {
    "encode an E as Json" in {
      val d = new JMap[String, String]
      d.put("foo", "bar")
      val e = new E(1, "test", "Test Message", null, d)

      val expected = Json.obj("code" -> Json.fromInt(1), "name" -> Json.fromString("test"), "message" -> Json.fromString("Test Message"), "data" -> Json.obj("foo" -> Json.fromString("bar")))
      val actual   = encoderECirce(e)

      actual shouldBe expected
    }
  }
}
