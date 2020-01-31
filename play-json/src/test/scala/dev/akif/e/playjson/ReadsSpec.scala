package dev.akif.e.playjson

import dev.akif.e.E
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.Json

class ReadsSpec extends AnyWordSpec with Matchers {
  "Play Json Reads for E" should {
    "fail to read for invalid input" in {
      val json    = Json.arr(1, 2, 3)
      val eOnFail = E.of(1, "test")

      val expected = Left(eOnFail)
      val actual   = json.readOrE(_ => eOnFail)

      actual shouldBe expected
    }

    "decode a JsValue as E" in {
      val json    = Json.obj("code" -> 1, "name" -> "test1")
      val eOnFail = E.of(2, "test2")
      val e       = E.of(1, "test1")

      val expected = Right(e)
      val actual   = json.readOrE(_ => eOnFail)

      actual shouldBe expected
    }
  }
}
