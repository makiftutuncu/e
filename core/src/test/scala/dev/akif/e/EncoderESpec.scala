package dev.akif.e

import org.scalatest.{Matchers, WordSpec}

class EncoderESpec extends WordSpec with Matchers {
  private val fieldCountingEncoderE: EncoderE[String] = { e: E =>
    s"${if (e.code != 0) 1 else 0},${if (e.name.nonEmpty) 1 else 0},${if (e.message.nonEmpty) 1 else 0},${if (e.cause.nonEmpty) 1 else 0},${if (e.data.nonEmpty) 1 else 0}"
  }

  "An EncoderE" should {
    "encode an E by a direct call" in {
      val e = E(1, "test", "Test Message", None, Map("foo" -> "bar"))

      val expected = "1,1,1,0,1"
      val actual   = fieldCountingEncoderE.encode(e)

      actual shouldBe expected
    }

    "encode an E by summoning implicit via apply" in {
      implicit val fcee: EncoderE[String] = fieldCountingEncoderE

      val e = E(1, "test", "Test Message", None, Map("foo" -> "bar"))

      val expected = "1,1,1,0,1"
      val actual   = EncoderE[String].encode(e)

      actual shouldBe expected
    }

    "encode an E by using implicit instance" in {
      implicit val fcee: EncoderE[String] = fieldCountingEncoderE

      val e = E(1, "test", "Test Message", None, Map("foo" -> "bar"))

      val expected = "1,1,1,0,1"
      val actual   = EncoderE.encode[String](e)

      actual shouldBe expected
    }
  }
}
