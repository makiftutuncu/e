package dev.akif.e.circe

import dev.akif.e.E
import io.circe.Json
import io.circe.syntax._
import org.scalatest.{Matchers, WordSpec}

class CirceDecoderSpec extends WordSpec with Matchers {
  "Circe Decoder for E" should {
    "fail to decode for invalid input" in {
      val json = Json.arr(1.asJson, 2.asJson, 3.asJson)
      val eOnFail = E.of(1, "test")

      val expected = Left(eOnFail)
      val actual   = json.decodeOrE(_ => eOnFail)

      actual shouldBe expected
    }

    "decode a Json as E" in {
      val json    = Json.obj("code" := 1, "name" := "test1")
      val eOnFail = E.of(2, "test2")
      val e       = E.of(1, "test1")

      val expected = Right(e)
      val actual   = json.decodeOrE(_ => eOnFail)

      actual shouldBe expected
    }
  }
}
