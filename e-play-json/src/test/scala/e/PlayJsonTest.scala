package e

import e.playjson._
import e.scala.E
import e.scala.codec.Decoder
import e.scala.test.ESuite
import play.api.libs.json._

class PlayJsonTest extends ESuite {
  case class TestData(s: String, i: Int)

  implicit val testDataReads: Reads[TestData] =
    Reads[TestData] {
      case j: JsObject => JsSuccess(TestData((j \ "s").asOpt[String].getOrElse(""), (j \ "i").asOpt[Int].getOrElse(0)))
      case _           => JsError("Expected an object")
    }

  implicit val testDataWrites: Writes[TestData] =
    Writes[TestData] { testData =>
      Json.obj(
        "s" -> testData.s,
        "i" -> testData.i
      )
    }

  private val eCodec        = makeCodec[E]
  private val testDataCodec = makeCodec[TestData]
  private val error         = Decoder.decodingError

  test("Failing to decode an E") {
    eCodec.decode(Json.arr()).assertError(error.causes(E.name("obj").message("error.expected.jsobject")))

    eCodec.decode(Json.obj("code" -> "foo")).assertError(error.causes(E.name("obj.code").message("error.expected.jsnumber")))

    eCodec.decode(Json.obj("name" -> 42)).assertError(error.causes(E.name("obj.name").message("error.expected.jsstring")))

    eCodec.decode(Json.obj("message" -> 123)).assertError(error.causes(E.name("obj.message").message("error.expected.jsstring")))

    eCodec.decode(Json.obj("causes" -> "foo")).assertError(error.causes(E.name("obj.causes").message("error.expected.jsarray")))

    eCodec.decode(Json.obj("data" -> "foo")).assertError(error.causes(E.name("obj.data").message("error.expected.jsobject")))

    eCodec.decode(Json.obj("time" -> "foo")).assertError(error.causes(E.name("obj.time").message("error.expected.jsnumber")))

    eCodec.decode(Json.obj("code" -> "foo", "name" -> 42)).assertError(error.causes(E.name("obj.code").message("error.expected.jsnumber"), E.name("obj.name").message("error.expected.jsstring")))
  }

  test("Decoding an E") {
    eCodec.decode(Json.obj()).assertValue(E.empty)

    val input1 = Json.obj(
      "code"    -> JsNull,
      "name"    -> JsNull,
      "message" -> JsNull,
      "causes"  -> JsNull,
      "data"    -> JsNull,
      "time"    -> JsNull
    )
    eCodec.decode(input1).assertValue(E.empty)

    val input2 = Json.obj(
      "code"    -> 1,
      "name"    -> "test-name",
      "message" -> "Test Message",
      "causes"  -> List(E.name("cause-1"), E.name("cause-2")),
      "data"    -> Map("foo" -> "bar"),
      "time"    -> 123456789L
    )
    val e = E(
      code    = Some(1),
      name    = Some("test-name"),
      message = Some("Test Message"),
      causes  = List(E.name("cause-1"), E.name("cause-2")),
      data    = Map("foo" -> "bar"),
      time    = Some(123456789L)
    )
    eCodec.decode(input2).assertValue(e)
  }

  test("Encoding an E") {
    assertEquals(eCodec.encode(E.empty), Json.obj())

    val e = E(
      code    = Some(1),
      name    = Some("test-name"),
      message = Some("Test Message"),
      causes  = List(E.name("cause-1"), E.name("cause-2")),
      data    = Map("foo" -> "bar"),
      time    = Some(123456789L)
    )
    val output = Json.obj(
      "code"    -> 1,
      "name"    -> "test-name",
      "message" -> "Test Message",
      "causes"  -> List(E.name("cause-1"), E.name("cause-2")),
      "data"    -> Map("foo" -> "bar"),
      "time"    -> 123456789L
    )
    assertEquals(eCodec.encode(e), output)
  }

  test("Failing to decode TestData or E") {
    testDataCodec.decode(Json.arr()).assertError(error.causes(E.name("obj").message("Expected an object")))
  }

  test("Decoding a TestData or E") {
    testDataCodec.decode(Json.obj("foo" -> "bar")).assertValue(TestData("", 0))

    testDataCodec.decode(Json.obj("s" -> "foo", "i" -> 42)).assertValue(TestData("foo", 42))
  }

  test("Encoding a TestData or E") {
    assertEquals(testDataCodec.encode(TestData("foo", 42)), Json.obj("s" -> "foo", "i" -> 42))
  }
}
