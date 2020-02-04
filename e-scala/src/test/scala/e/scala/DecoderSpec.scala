package e.scala

import e.AbstractDecoder.DecodingResult
import e.scala.implicits._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class DecoderSpec extends AnyWordSpec with Matchers {
  private def failingDecoder(decodingFailure: E): Decoder[String] = (_: String) => DecodingResult.fail(decodingFailure)
  private def succeedingDecoder(decoded: E): Decoder[String]      = (_: String) => DecodingResult.succeed(decoded)

  "Decoding as Either" should {
    "return decoding failure as Left when failed" in {
      val input           = "test"
      val decodingFailure = E(name = "decoding-failure")

      val maybe = failingDecoder(decodingFailure).decodeEither(input)

      maybe shouldBe Left(decodingFailure)
    }

    "return decoded E as Right when successful" in {
      val input   = "test"
      val decoded = E(name = "decoded-e")

      val maybe = succeedingDecoder(decoded).decodeEither(input)

      maybe shouldBe Right(decoded)
    }
  }
}
