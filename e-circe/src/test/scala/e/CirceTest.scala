package e

import e.circe.{jsonCodec, _}
import e.codec._
import e.test.ESuite
import io.circe.Json
import io.circe.syntax._

class CirceTest extends ESuite {
  val codec: Codec[E, Json] = jsonCodec[E]
  val error: E = Decoder.decodingError

  test("Failing to decode an E") {
    codec.decode(Json.arr()).assertError(error.causes(E.message("Expected: JsonObject")))

    codec.decode(Json.obj("code" := "foo")).assertError(error.causes(E.name(".code").message("Expected: Int")))

    codec.decode(Json.obj("name" := 42)).assertError(error.causes(E.name(".name").message("Expected: String")))

    codec.decode(Json.obj("message" := 123)).assertError(error.causes(E.name(".message").message("Expected: String")))

    codec.decode(Json.obj("causes" := "foo")).assertError(error.causes(E.name(".causes").message("Expected: List[E]")))

    codec.decode(Json.obj("data" := "foo")).assertError(error.causes(E.name(".data").message("Expected: Map[String, String]")))

    codec.decode(Json.obj("time" := "foo")).assertError(error.causes(E.name(".time").message("Expected: Long")))
  }

  test("Decoding an E") {
    codec.decode(Json.obj()).assertValue(E.empty)

    val input1 = Json.obj(
      "code"    := Json.Null,
      "name"    := Json.Null,
      "message" := Json.Null,
      "causes"  := Json.Null,
      "data"    := Json.Null,
      "time"    := Json.Null
    )
    codec.decode(input1).assertValue(E.empty)

    val input2 = Json.obj(
      "code"    := 1,
      "name"    := "test-name",
      "message" := "Test Message",
      "causes"  := List(E.name("cause-1"), E.name("cause-2")),
      "data"    := Map("foo" -> "bar"),
      "time"    := 123456789L
    )
    val e = E(
      code    = Some(1),
      name    = Some("test-name"),
      message = Some("Test Message"),
      causes  = List(E.name("cause-1"), E.name("cause-2")),
      data    = Map("foo" -> "bar"),
      time    = Some(123456789L)
    )
    codec.decode(input2).assertValue(e)
  }

  test("Encoding an E") {
    assertEquals(codec.encode(E.empty), Json.obj())

    val e = E(
      code    = Some(1),
      name    = Some("test-name"),
      message = Some("Test Message"),
      causes  = List(E.name("cause-1"), E.name("cause-2")),
      data    = Map("foo" -> "bar"),
      time    = Some(123456789L)
    )
    val output = Json.obj(
      "code"    := 1,
      "name"    := "test-name",
      "message" := "Test Message",
      "causes"  := List(E.name("cause-1"), E.name("cause-2")),
      "data"    := Map("foo" -> "bar"),
      "time"    := 123456789L
    )
    assertEquals(codec.encode(e), output)
  }
}
