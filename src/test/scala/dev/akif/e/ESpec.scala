package dev.akif.e

import org.scalatest.{Matchers, WordSpec}

class ESpec extends WordSpec with Matchers {
  "An empty E" should {
    "have no code, name, message, cause or data" in {
      val e = E.empty

      e.code    shouldBe 0
      e.name    shouldBe ""
      e.message shouldBe ""
      e.cause   shouldBe None
      e.data    shouldBe Map.empty
    }
  }

  "An E" can {
    "have a code" in {
      val e = E.code(42)

      val expected = 42
      val actual   = e.code

      actual shouldBe expected
    }

    "have a name" in {
      val e = E.name("test")

      val expected = "test"
      val actual   = e.name

      actual shouldBe expected
    }

    "have a message" in {
      val e = E.name("Test Error")

      val expected = "Test Error"
      val actual   = e.name

      actual shouldBe expected
    }

    "have a cause" in {
      val c = new RuntimeException("test")
      val e = E.cause(c)

      val expected = Some(c)
      val actual   = e.cause

      actual shouldBe expected
    }

    "have data" in {
      val d = Map("foo" -> "bar")
      val e = E.data(d)

      val expected = d
      val actual   = e.data

      actual shouldBe expected
    }

    "a mixture of code, name, message, cause and data" in {
      val e = E(1, "test", "Test Message", None, Map("foo" -> "bar"))

      e.code    shouldBe 1
      e.name    shouldBe "test"
      e.message shouldBe "Test Message"
      e.cause   shouldBe None
      e.data    shouldBe Map("foo" -> "bar")
    }
  }

  "An E" should {
    "have same toString() output as its DefaultEncoderE.encode output" in {
      val e = E(1, "test", "Test Message", None, Map("foo" -> "bar"))

      e.toString shouldBe DefaultEncoderE.encode(e)
    }
  }
}
