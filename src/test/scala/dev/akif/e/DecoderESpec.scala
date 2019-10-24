package dev.akif.e

import org.scalatest.{Matchers, WordSpec}

import scala.util.{Failure, Success, Try}

class DecoderESpec extends WordSpec with Matchers {
  private val codeParsingDecoderE: DecoderE[String] = { s: String =>
    Try(s.toInt) match {
      case Failure(_) => Left(DecodingFailure(s"'$s' was not a valid code"))
      case Success(c) => Right(E.code(c))
    }
  }

  "A DecoderE" should {
    "fail to decode for invalid input" in {
      val expected = Left(DecodingFailure("'a' was not a valid code"))
      val actual   = codeParsingDecoderE.decode("a")

      actual shouldBe expected
    }

    "decode an E by a direct call" in {
      val expected = Right(E.code(1))
      val actual   = codeParsingDecoderE.decode("1")

      actual shouldBe expected
    }

    "decode an E by summoning implicit via apply" in {
      implicit val cpde: DecoderE[String] = codeParsingDecoderE

      val expected = Right(E.code(1))
      val actual   = DecoderE[String].decode("1")

      actual shouldBe expected
    }

    "decode an E by using implicit instance" in {
      implicit val cpde: DecoderE[String] = codeParsingDecoderE

      val expected = Right(E.code(1))
      val actual   = DecoderE.decode[String]("1")

      actual shouldBe expected
    }
  }
}
