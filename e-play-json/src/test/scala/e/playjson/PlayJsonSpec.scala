package e.playjson

import e.scala.E
import e.scala.implicits._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.libs.json.{JsResultException, Json}

import scala.util.Try

class PlayJsonSpec extends AnyWordSpec with Matchers {
  "Decoding using Codec" should {
    "fail when input is not a JsObject" in {
      val json = Json.arr(1, 2)

      val expected = E(
        name    = "decoding-failure",
        message = "Input is not a Json object!",
        data    = Map("input" -> "[1,2]")
      )

      val actual = CodecForPlayJson.decodeEither(json)

      actual shouldBe Left(expected)
    }

    "succeed and decode input into an E" in {
      val json = Json.obj(
        "code"    -> 1,
        "name"    -> "test-name",
        "message" -> "Test Message",
        "cause"   -> "Test Exception",
        "data"    -> Map("test" -> "data")
      )

      val expected = E(
        code    = 1,
        name    = "test-name",
        message = "Test Message",
        cause   = None,
        data    = Map("test" -> "data")
      )

      val actual = CodecForPlayJson.decodeEither(json)

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
        "code"    -> 1,
        "name"    -> "test-name",
        "message" -> "Test Message",
        "cause"   -> "Test Exception",
        "data"    -> Map("test" -> "data")
      )

      val actual = CodecForPlayJson.encode(e)

      actual shouldBe expected
    }
  }

  "Reading using play-json" should {
    "fail when input is not a JsObject" in {
      val json = Json.arr(1, 2)

      val expected = E(
        name    = "decoding-failure",
        message = "Input is not a Json object!",
        data    = Map("input" -> "[1,2]")
      )

      val actual = Try(json.as[E]).failed.map {
        case jse: JsResultException => jse.errors.head._2.head.message
      }.getOrElse("")

      actual shouldBe expected.toString
    }

    "succeed and read input as an E" in {
      val json = Json.obj(
        "code"    -> 1,
        "name"    -> "test-name",
        "message" -> "Test Message",
        "cause"   -> "Test Exception",
        "data"    -> Map("test" -> "data")
      )

      val expected = E(
        code    = 1,
        name    = "test-name",
        message = "Test Message",
        cause   = None,
        data    = Map("test" -> "data")
      )

      val actual = json.as[E]

      actual shouldBe expected
    }
  }

  "Writing using play-json" should {
    "write an E as Json" in {
      val e = E(
        code    = 1,
        name    = "test-name",
        message = "Test Message",
        cause   = Some(new Exception("Test Exception")),
        data    = Map("test" -> "data")
      )

      val expected = Json.obj(
        "code"    -> 1,
        "name"    -> "test-name",
        "message" -> "Test Message",
        "cause"   -> "Test Exception",
        "data"    -> Map("test" -> "data")
      )

      val actual = Json.toJson(e)

      actual shouldBe expected
    }

    "write a failure Maybe as Json" in {
      val e = E(
        code    = 1,
        name    = "test-name",
        message = "Test Message",
        cause   = Some(new Exception("Test Exception")),
        data    = Map("test" -> "data")
      )

      val expected = Json.obj(
        "code"    -> 1,
        "name"    -> "test-name",
        "message" -> "Test Message",
        "cause"   -> "Test Exception",
        "data"    -> Map("test" -> "data")
      )

      val actual = Json.toJson(e.maybe[String])

      actual shouldBe expected
    }

    "write a success Maybe as Json" in {
      val expected = Json.obj("test" -> "data")

      val actual = Json.toJson(Map("test" -> "data").maybe)

      actual shouldBe expected
    }
  }

  "Reading as Maybe" should {
    "fail when input is not a JsObject" in {
      val json = Json.arr(1, 2)

      val expected = E(
        name    = "decoding-failure",
        message = "Input is not a Json object!",
        data    = Map("input" -> "[1,2]")
      )

      val actual = json.readOrE(jse => E(message = jse.errors.head._2.head.message)).e.map(_.toString).getOrElse("")

      actual shouldBe E(message = expected.toString).toString
    }

    "succeed and read input into an E" in {
      val json = Json.obj(
        "code"    -> 1,
        "name"    -> "test-name",
        "message" -> "Test Message",
        "cause"   -> "Test Exception",
        "data"    -> Map("test" -> "data")
      )

      val expected = E(
        code    = 1,
        name    = "test-name",
        message = "Test Message",
        cause   = None,
        data    = Map("test" -> "data")
      )

      val actual = json.readOrE(jse => E(message = jse.errors.head._2.head.message)).value.map(_.toString).getOrElse("")

      actual shouldBe expected.toString
    }
  }
}
