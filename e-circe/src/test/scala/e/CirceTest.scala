package e

import cats.syntax.all.*
import e.circe.{*, given}
import e.scala.E
import e.scala.codec.*
import e.scala.test.ESuite
import io.circe.{Json, Decoder => CirceDecoder, Encoder => CirceEncoder}
import io.circe.syntax.*

class CirceTest extends ESuite:
    case class TestData(s: String, i: Int)

    given testDataDecoder: CirceDecoder[TestData] =
        (CirceDecoder[String].at("s"), CirceDecoder[Int].at("i")).mapN:
            case (s, i) =>
                TestData(s, i)

    given testDataEncoder: CirceEncoder[TestData] =
        CirceEncoder.forProduct2("s", "i")(t => (t.s, t.i))

    private val eCodec = makeCodec[E]
    private val testDataCodec = makeCodec[TestData]
    private val error = Decoder.decodingError

    test("Failing to decode an E"):
        eCodec
            .decode(Json.arr())
            .assertError(error.causes(E.message("Expected: JsonObject")))

        eCodec
            .decode(Json.obj("code" := "foo"))
            .assertError(error.causes(E.name(".code").message("Expected: Int")))

        eCodec
            .decode(Json.obj("name" := 42))
            .assertError(
              error.causes(
                E.name(".name")
                    .message(
                      "Expected: Got value '42' with wrong type, expecting string"
                    )
              )
            )

        eCodec
            .decode(Json.obj("message" := 123))
            .assertError(
              error.causes(
                E.name(".message")
                    .message(
                      "Expected: Got value '123' with wrong type, expecting string"
                    )
              )
            )

        eCodec
            .decode(Json.obj("causes" := "foo"))
            .assertError(error.causes(E.name(".causes").message("Expected: List[E]")))

        eCodec
            .decode(Json.obj("data" := "foo"))
            .assertError(
              error.causes(E.name(".data").message("Expected: Map[String, String]"))
            )

        eCodec
            .decode(Json.obj("time" := "foo"))
            .assertError(error.causes(E.name(".time").message("Expected: Long")))

        eCodec
            .decode(Json.obj("code" := "foo", "name" := 42))
            .assertError(
              error.causes(
                E.name(".code").message("Expected: Int"),
                E.name(".name")
                    .message(
                      "Expected: Got value '42' with wrong type, expecting string"
                    )
              )
            )

    test("Decoding an E"):
        eCodec.decode(Json.obj()).assertValue(E.empty)

        val input1 = Json.obj(
          "code" := Json.Null,
          "name" := Json.Null,
          "message" := Json.Null,
          "causes" := Json.Null,
          "data" := Json.Null,
          "time" := Json.Null
        )
        eCodec.decode(input1).assertValue(E.empty)

        val input2 = Json.obj(
          "code" := 1,
          "name" := "test-name",
          "message" := "Test Message",
          "causes" := List(E.name("cause-1"), E.name("cause-2")),
          "data" := Map("foo" -> "bar"),
          "time" := 123456789L
        )
        val e = E(
          code = Some(1),
          name = Some("test-name"),
          message = Some("Test Message"),
          causes = List(E.name("cause-1"), E.name("cause-2")),
          data = Map("foo" -> "bar"),
          time = Some(123456789L)
        )
        eCodec.decode(input2).assertValue(e)

    test("Encoding an E"):
        assertEquals(eCodec.encode(E.empty), Json.obj())

        val e = E(
          code = Some(1),
          name = Some("test-name"),
          message = Some("Test Message"),
          causes = List(E.name("cause-1"), E.name("cause-2")),
          data = Map("foo" -> "bar"),
          time = Some(123456789L)
        )
        val output = Json.obj(
          "code" := 1,
          "name" := "test-name",
          "message" := "Test Message",
          "causes" := List(E.name("cause-1"), E.name("cause-2")),
          "data" := Map("foo" -> "bar"),
          "time" := 123456789L
        )
        assertEquals(eCodec.encode(e), output)

    test("Failing to decode TestData or E"):
        testDataCodec
            .decode(Json.arr())
            .assertError(
              error.causes(
                E.name(".s").message("Missing required field"),
                E.name(".i").message("Missing required field")
              )
            )

        testDataCodec
            .decode(Json.obj("s" := 42, "i" := "foo"))
            .assertError(
              error.causes(
                E.name(".s")
                    .message("Got value '42' with wrong type, expecting string"),
                E.name(".i").message("Int")
              )
            )

    test("Decoding a TestData or E"):
        testDataCodec
            .decode(Json.obj("s" := "foo", "i" := 42))
            .assertValue(TestData("foo", 42))

    test("Encoding a TestData or E"):
        assertEquals(
          testDataCodec.encode(TestData("foo", 42)),
          Json.obj("s" := "foo", "i" := 42)
        )
