package dev.akif.e

import org.scalatest.{Matchers, WordSpec}

import scala.util.{Failure, Success, Try}

class DecoderESpec extends WordSpec with Matchers {
  private val codeParsingDecoderE: DecoderE[String] = { s: String =>
    Try(s.toInt) match {
      case Failure(_) => throw new DecodingFailure(s"'$s' was not a valid code")
      case Success(c) => E.empty.code(c)
    }
  }

  "A DecoderE" should {
    "fail to decode for invalid input" in {
      val expected = Left(new DecodingFailure("'a' was not a valid code"))
      val actual   = codeParsingDecoderE.decode("a")

      actual shouldBe expected
    }

    "decode an E" in {
      val expected = Right(E.empty.code(1))
      val actual   = codeParsingDecoderE.decode("1")

      actual shouldBe expected
    }
  }
}
