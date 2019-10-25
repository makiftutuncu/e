package dev.akif.e

import java.util.{HashMap => JMap}
import org.scalatest.{Matchers, WordSpec}

class ESpec extends WordSpec with Matchers {
  "An empty E" should {
    "have no code, name, message, cause or data" in {
      val e = E.empty

      e.code    shouldBe 0
      e.name    shouldBe ""
      e.message shouldBe ""
      e.cause   shouldBe null
      e.data    shouldBe new JMap[String, String]

      e.hasCode    shouldBe false
      e.hasName    shouldBe false
      e.hasMessage shouldBe false
      e.hasCause   shouldBe false
      e.hasData    shouldBe false
    }
  }

  "An E" can {
    "have a code" in {
      val e = E.empty.code(42)

      val expected = 42
      val actual   = e.code

      actual shouldBe expected
    }

    "have a name" in {
      val e = E.empty.name("test")

      val expected = "test"
      val actual   = e.name

      actual shouldBe expected
    }

    "have a message" in {
      val e = E.empty.message("Test Error")

      val expected = "Test Error"
      val actual   = e.message

      actual shouldBe expected
    }

    "have a cause" in {
      val c = new RuntimeException("test")
      val e = E.empty.cause(c)

      val expected = c
      val actual   = e.cause

      actual shouldBe expected
    }

    "have data" in {
      val d = new JMap[String, String]
      d.put("foo", "bar")
      val e = E.empty.data(d)

      val expected = d
      val actual   = e.data

      actual shouldBe expected
    }

    "a mixture of code, name, message, cause and data" in {
      val d = new JMap[String, String]
      d.put("foo", "bar")
      val e = new E(1, "test", "Test Message", null, d)

      e.code    shouldBe 1
      e.name    shouldBe "test"
      e.message shouldBe "Test Message"
      e.cause   shouldBe null
      e.data    shouldBe d
    }
  }

  "An E" should {
    "have same toString() output as its DefaultEncoderE.encode output" in {
      val d = new JMap[String, String]
      d.put("foo", "bar")
      val e = new E(1, "test", "Test Message", null, d)

      e.toString shouldBe DefaultEncoderE.encode(e)
    }
  }
}
