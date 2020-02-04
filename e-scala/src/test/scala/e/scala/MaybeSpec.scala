package e.scala

import e.scala.implicits._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.util.{Failure, Success}

class MaybeSpec extends AnyWordSpec with Matchers {
  "A failure Maybe" should {
    "have failure" in {
      val maybe1: Maybe[String] = Left(E(1))

      maybe1.isFailure shouldBe true
      maybe1.isLeft    shouldBe true
      maybe1.isSuccess shouldBe false
      maybe1.isRight   shouldBe false
      maybe1.e         shouldBe Some(E(1))
      maybe1.value     shouldBe None

      val maybe2: Maybe[String] = E(2).maybe[String]

      maybe2.isFailure shouldBe true
      maybe2.isLeft    shouldBe true
      maybe2.isSuccess shouldBe false
      maybe2.isRight   shouldBe false
      maybe2.e         shouldBe Some(E(2))
      maybe2.value     shouldBe None
    }
  }

  "A success Maybe" should {
    "have value" in {
      val maybe1: Maybe[String] = Right("test")

      maybe1.isFailure shouldBe false
      maybe1.isLeft    shouldBe false
      maybe1.isSuccess shouldBe true
      maybe1.isRight   shouldBe true
      maybe1.e         shouldBe None
      maybe1.value     shouldBe Some("test")

      val maybe2: Maybe[String] = "test".maybe

      maybe2.isFailure shouldBe false
      maybe2.isLeft    shouldBe false
      maybe2.isSuccess shouldBe true
      maybe2.isRight   shouldBe true
      maybe2.e         shouldBe None
      maybe2.value     shouldBe Some("test")
    }
  }

  "Constructing a Maybe from an Option" should {
    "produce a failure Maybe when it is None" in {
      Option.empty[String].orE(E(1)) shouldBe E(1).maybe[String]
    }

    "produce a success Maybe when it is Some" in {
      Option[String]("test").orE(E(1)) shouldBe "test".maybe
    }
  }

  "Constructing a Maybe from an Either" should {
    "produce a failure Maybe when it is Left" in {
      Left[Int, String](1).orE(i => E(i)) shouldBe E(1).maybe[String]
    }

    "produce a success Maybe when it is Right" in {
      Right[Int, String]("test").orE(i => E(i)) shouldBe "test".maybe
    }
  }

  "Constructing a Maybe from a Try" should {
    "produce a failure Maybe when it is Failure" in {
      val cause = new Exception()
      Failure[String](cause).orE(c => E().cause(c)) shouldBe E().cause(cause).maybe[String]
    }

    "produce a success Maybe when it is Success" in {
      Success[String]("test").orE(c => E().cause(c)) shouldBe "test".maybe
    }
  }
}
