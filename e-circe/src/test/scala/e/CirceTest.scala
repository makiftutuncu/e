package e

import munit.FunSuite

class CirceTest extends FunSuite {
  /*
  "Decoding using Codec" should {
    "fail when input is not a JsonObject" in {
      val json = Json.arr(1.asJson, 2.asJson)

      val expected = E("decoding-failure", "Input is not a Json object!").data("input" -> "[1,2]")

      val actual = CodecForCirceJson.decode(json).get().cause(None)

      actual shouldBe expected
    }

    "succeed and decode input into an E" in {
      val json = Json.obj(
        "name"    := "test-name",
        "message" := "Test Message",
        "code"    := 1,
        "cause"   := "Test Exception",
        "data"    := Map("test" -> "data")
      )

      val expected = E("test-name", "Test Message", 1, None, Map("test" -> "data")
      )

      val actual = CodecForCirceJson.decode(json).get()

      actual shouldBe expected
    }
  }

  "Encoding using Codec" should {
    "encode an E as Json" in {
      val e = E("test-name", "Test Message", 1, Some(new Exception("Test Exception")), Map("test" -> "data"))

      val expected = Json.obj(
        "name"    := "test-name",
        "message" := "Test Message",
        "code"    := 1,
        "cause"   := "Test Exception",
        "data"    := Map("test" -> "data")
      )

      val actual = CodecForCirceJson.encode(e)

      actual shouldBe expected
    }
  }

  "Decoding using circe" should {
    "fail when input is not a JsonObject" in {
      val json = Json.arr(1.asJson, 2.asJson)

      val expected = E("decoding-failure", "Input is not a Json object!").cause(new Exception("JsonObject")).data("input" -> "[1,2]")

      val actual = json.as[E].left.map(_.getMessage())

      actual shouldBe Left(expected.toString)
    }

    "succeed and decode input into an E" in {
      val json = Json.obj(
        "name"    := "test-name",
        "message" := "Test Message",
        "code"    := 1,
        "cause"   := "Test Exception",
        "data"    := Map("test" -> "data")
      )

      val expected = E("test-name", "Test Message", 1, None, Map("test" -> "data"))

      val actual = json.as[E]

      actual shouldBe Right(expected)
    }
  }

  "Encoding using circe" should {
    "encode an E as Json" in {
      val e = E("test-name", "Test Message", 1, Some(new Exception("Test Exception")), Map("test" -> "data"))

      val expected = Json.obj(
        "name"    := "test-name",
        "message" := "Test Message",
        "code"    := 1,
        "cause"   := "Test Exception",
        "data"    := Map("test" -> "data")
      )

      val actual = e.asJson

      actual shouldBe expected
    }

    "encode a failure Maybe as Json" in {
      val e = E("test-name", "Test Message", 1, Some(new Exception("Test Exception")), Map("test" -> "data"))

      val expected = Json.obj(
        "name"    := "test-name",
        "message" := "Test Message",
        "code"    := 1,
        "cause"   := "Test Exception",
        "data"    := Map("test" -> "data")
      )

      val actual = e.toMaybe[String].asJson

      actual shouldBe expected
    }

    "encode a success Maybe as Json" in {
      val expected = Json.obj("test" := "data")

      val actual = Map("test" -> "data").toMaybe.asJson

      actual shouldBe expected
    }
  }

  "Decoding as Maybe" should {
    "fail when input is not a JsonObject" in {
      val json = Json.arr(1.asJson, 2.asJson)

      val expected = E("decoding-failure", "Input is not a Json object!").cause(new Exception("JsonObject")).data("input" -> "[1,2]")

      val actual = json.decodeMaybe(df => E(message = df.getMessage)).eOpt.map(_.toString).getOrElse("")

      actual shouldBe E(message = expected.toString).toString
    }

    "succeed and decode input into an E" in {
      val json = Json.obj(
        "name"    := "test-name",
        "message" := "Test Message",
        "code"    := 1,
        "cause"   := "Test Exception",
        "data"    := Map("test" -> "data")
      )

      val expected = E("test-name", "Test Message", 1, None, Map("test" -> "data"))

      val actual = json.decodeMaybe(df => E(message = df.getMessage)).valueOpt.map(_.toString).getOrElse("")

      actual shouldBe expected.toString
    }
  }
  */
}
