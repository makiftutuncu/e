package e.scala

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class JsonStringEncoderSpec extends AnyWordSpec with Matchers {
  val cause = new Exception("Test Exception")
  val data  = Map("test" -> "data")

  "Encoding an E as Json String" should {
    "create correct result" when {
      "E has all the fields" in {
        val e = E(1, "test-name", "Test Message", Some(cause), data)

        val expected = """{"code":1,"name":"test-name","message":"Test Message","cause":"Test Exception","data":{"test":"data"}}"""
        val actual   = JsonStringEncoder.encode(e)

        actual shouldBe expected
      }

      "E has some fields" in {
        val e = E(name = "test-name", message = "Test Message")

        val expected = """{"name":"test-name","message":"Test Message"}"""
        val actual   = JsonStringEncoder.encode(e)

        actual shouldBe expected
      }

      "E has quotes" in {
        val e = E(name = "test-name", data = Map("test" -> "da\"ta", "f\"oo" -> "bar"))

        val expected = """{"name":"test-name","data":{"test":"da\"ta","f\"oo":"bar"}}"""
        val actual   = JsonStringEncoder.encode(e)

        actual shouldBe expected
      }

      "E has no fields" in {
        val e = E()

        val expected = """{}"""
        val actual   = JsonStringEncoder.encode(e)

        actual shouldBe expected
      }
    }
  }
}
