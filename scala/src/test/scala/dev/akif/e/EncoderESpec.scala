package dev.akif.e

import java.util.{HashMap => JMap}

import org.scalatest.{Matchers, WordSpec}

class EncoderESpec extends WordSpec with Matchers {
  private val fieldCountingEncoderE: EncoderE[String] = { e: E =>
    s"${if (e.hasCode) 1 else 0},${if (e.hasName) 1 else 0},${if (e.hasMessage) 1 else 0},${if (e.hasCause) 1 else 0},${if (e.hasData) 1 else 0}"
  }

  "An EncoderE" should {
    "encode an E" in {
      val d = new JMap[String, String]
      d.put("foo", "bar")
      val e = new E(1, "test", "Test Message", null, d)

      val expected = "1,1,1,0,1"
      val actual   = fieldCountingEncoderE.encode(e)

      actual shouldBe expected
    }
  }
}
