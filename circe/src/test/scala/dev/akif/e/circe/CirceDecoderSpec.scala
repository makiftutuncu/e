package dev.akif.e.circe

import dev.akif.e.E
import io.circe.Json
import org.scalatest.{Matchers, WordSpec}

class CirceDecoderSpec extends WordSpec with Matchers {
  "Circe Decoder for E" should {
    "fail to decode for invalid input" in {
      val json = Json.arr(Json.fromInt(1), Json.fromInt(2), Json.fromInt(3))

      val expected = Left(s"'$json' is not a Json object!")
      val actual   = decoderECirce.decodeJson(json).left.map(_.getMessage.trim)

      actual shouldBe expected
    }

    "decode a Json as E" in {
      val expected = Right(E.empty.code(1))
      val actual   = decoderECirce.decodeJson(Json.obj("code" -> Json.fromInt(1)))

      actual shouldBe expected
    }
  }
}
