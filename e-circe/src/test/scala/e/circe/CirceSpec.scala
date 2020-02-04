package e.circe

import e.scala.E
import e.scala.implicits._
import io.circe._
import io.circe.syntax._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class CirceSpec extends AnyWordSpec with Matchers {
  "Decoding using Codec" should {
    "fail when input is not a Json object" in {
      val json = Json.arr(1.asJson, 2.asJson)

      val expected = E(
        name    = "decoding-failure",
        message = "Input is not a Json object!",
        data    = Map("input" -> "[1,2]")
      )

      val actual = CodecForCirceJson.decodeEither(json).left.map(_.cause(None))

      actual shouldBe Left(expected)
    }

    "succeed and decode input into an E" in {
      val json = Json.obj(
        "code"    := 1,
        "name"    := "test-name",
        "message" := "Test Message",
        "cause"   := "Test Exception",
        "data"    := Map("test" -> "data")
      )

      val expected = E(
        code    = 1,
        name    = "test-name",
        message = "Test Message",
        cause   = None,
        data    = Map("test" -> "data")
      )

      val actual = CodecForCirceJson.decodeEither(json)

      actual shouldBe Right(expected)
    }
  }

  "Encoding using Codec" should {
    "encode an E as Json" in {
      val e = E(
        code    = 1,
        name    = "test-name",
        message = "Test Message",
        cause   = Some(new Exception("Test Exception")),
        data    = Map("test" -> "data")
      )

      val expected = Json.obj(
        "code"    := 1,
        "name"    := "test-name",
        "message" := "Test Message",
        "cause"   := "Test Exception",
        "data"    := Map("test" -> "data")
      )

      val actual = CodecForCirceJson.encode(e)

      actual shouldBe expected
    }
  }

  "Decoding using circe" should {
    "fail when input is not a Json object" in {
      val json = Json.arr(1.asJson, 2.asJson)

      val expected = E(
        name    = "decoding-failure",
        message = "Input is not a Json object!",
        cause   = Some(new Exception("JsonObject")),
        data    = Map("input" -> "[1,2]")
      )

      val actual = json.as[E].left.map(_.getMessage())

      actual shouldBe Left(expected.toString)
    }

    "succeed and decode input into an E" in {
      val json = Json.obj(
        "code"    := 1,
        "name"    := "test-name",
        "message" := "Test Message",
        "cause"   := "Test Exception",
        "data"    := Map("test" -> "data")
      )

      val expected = E(
        code    = 1,
        name    = "test-name",
        message = "Test Message",
        cause   = None,
        data    = Map("test" -> "data")
      )

      val actual = json.as[E]

      actual shouldBe Right(expected)
    }
  }

  "Encoding using circe" should {
    "encode an E as Json" in {
      val e = E(
        code    = 1,
        name    = "test-name",
        message = "Test Message",
        cause   = Some(new Exception("Test Exception")),
        data    = Map("test" -> "data")
      )

      val expected = Json.obj(
        "code"    := 1,
        "name"    := "test-name",
        "message" := "Test Message",
        "cause"   := "Test Exception",
        "data"    := Map("test" -> "data")
      )

      val actual = e.asJson

      actual shouldBe expected
    }

    "encode a failure Maybe as Json" in {
      val e = E(
        code    = 1,
        name    = "test-name",
        message = "Test Message",
        cause   = Some(new Exception("Test Exception")),
        data    = Map("test" -> "data")
      )

      val expected = Json.obj(
        "code"    := 1,
        "name"    := "test-name",
        "message" := "Test Message",
        "cause"   := "Test Exception",
        "data"    := Map("test" -> "data")
      )

      val actual = e.maybe[String].asJson

      actual shouldBe expected
    }

    "encode a success Maybe as Json" in {
      val expected = Json.obj("test" := "data")

      val actual = Map("test" -> "data").maybe.asJson

      actual shouldBe expected
    }
  }
}
