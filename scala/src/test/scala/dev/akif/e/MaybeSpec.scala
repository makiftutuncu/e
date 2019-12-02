package dev.akif.e

import dev.akif.e.implicits._
import dev.akif.e.syntax._

import scala.util.{Failure, Success}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class MaybeSpec extends AnyWordSpec with Matchers {
  "A Maybe" can {
    "have an E" in {
      val maybe = E.of(4).maybe[String]

      maybe.isError  shouldBe true
      maybe.hasError shouldBe true
      maybe.isValue  shouldBe false
      maybe.hasValue shouldBe false
    }

    "have a value" in {
      val maybe = "foo".maybe

      maybe.isError  shouldBe false
      maybe.hasError shouldBe false
      maybe.isValue  shouldBe true
      maybe.hasValue shouldBe true
    }
  }

  "Using `orE` method, a Maybe" can {
    "be made from an empty Option" in {
      val e = E.of(3)

      val expected = Left(e)
      val actual   = Option.empty[String].orE(e)

      actual shouldBe expected
    }

    "be made from a non-empty Option" in {
      val expected = Right("foo")
      val actual   = Some("foo").orE(E.of(3))

      actual shouldBe expected
    }

    "be made from a Left Either" in {
      val expected = Left(E.empty.message("foo"))
      val actual   = Left[String, Int]("foo").orE(m => E.empty.message(m))

      actual shouldBe expected
    }

    "be made from a Right Either" in {
      val expected = Right(3)
      val actual   = Right[String, Int](3).orE(m => E.empty.message(m))

      actual shouldBe expected
    }

    "be made from a failed Try" in {
      val c = new RuntimeException("test")

      val expected = Left(E.empty.cause(c))
      val actual   = Failure(c).orE(e => E.empty.cause(e))

      actual shouldBe expected
    }

    "be made from a successful Try" in {
      val expected = Right("foo")
      val actual   = Success("foo").orE(e => E.empty.cause(e))

      actual shouldBe expected
    }
  }

  "Using `.maybe` syntax, a Maybe" can {
    "be made from an E" in {
      val e = E.of(4)

      val expected: Maybe[String] = Left(e)
      val actual: Maybe[String]   = e.maybe[String]

      actual shouldBe expected
    }

    "be made from a value" in {
      val expected: Maybe[String] = Right("foo")
      val actual: Maybe[String]   = "foo".maybe

      actual shouldBe expected
    }
  }
}
