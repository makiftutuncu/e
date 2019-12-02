package dev.akif.e


import scala.util.{Failure, Success, Try}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class DecoderESpec extends AnyWordSpec with Matchers {
  private val codeParsingDecoderE: DecoderE[String] = { s: String =>
    Try(s.toInt) match {
      case Failure(_) => throw new DecodingFailure(s"'$s' was not a valid code")
      case Success(c) => E.of(c)
    }
  }

  "A DecoderE" should {
    "fail to decode for invalid input without throwing exception" in {
      val expected = Left(new DecodingFailure("'a' was not a valid code"))
      val actual   = codeParsingDecoderE.decode("a")

      actual shouldBe expected
    }

    "decode an E" in {
      val expected = Right(E.of(1))
      val actual   = codeParsingDecoderE.decode("1")

      actual shouldBe expected
    }
  }
}
