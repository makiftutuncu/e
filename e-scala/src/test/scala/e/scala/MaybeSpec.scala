package e.scala

import e.scala.implicits._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.util.{Failure => TryFailure, Success => TrySuccess}

class MaybeSpec extends AnyWordSpec with Matchers {
  "A Maybe" can {
    "have failure" in {
      val maybe1: Maybe[String] = Maybe.failure(E("test"))

      maybe1.isSuccess shouldBe false
      maybe1.eOpt      shouldBe Some(E("test"))
      maybe1.valueOpt  shouldBe None

      val maybe2: Maybe[String] = E("test").toMaybe[String]

      maybe2.isSuccess shouldBe false
      maybe2.eOpt      shouldBe Some(E("test"))
      maybe2.valueOpt  shouldBe None
    }

    "have value" in {
      val maybe1: Maybe[String] = Maybe.success("test")

      maybe1.isSuccess shouldBe true
      maybe1.eOpt      shouldBe None
      maybe1.valueOpt  shouldBe Some("test")

      val maybe2: Maybe[String] = "test".toMaybe

      maybe2.isSuccess shouldBe true
      maybe2.eOpt      shouldBe None
      maybe2.valueOpt  shouldBe Some("test")
    }

    "be mapped" in {
      val maybe1: Maybe[String] = E("test").toMaybe
      val maybe2: Maybe[String] = "test".toMaybe

      maybe1.map(_.toUpperCase) shouldBe E("test").toMaybe
      maybe2.map(_.toUpperCase) shouldBe "TEST".toMaybe
    }

    "be flat mapped" in {
      val maybe1: Maybe[String] = E("test-1").toMaybe
      val maybe2: Maybe[String] = "test".toMaybe

      maybe1.flatMap(s => s.toUpperCase.toMaybe) shouldBe E("test-1").toMaybe
      maybe2.flatMap(_ => E("test-2").toMaybe)          shouldBe E("test-2").toMaybe
      maybe2.flatMap(s => s.toUpperCase.toMaybe) shouldBe "TEST".toMaybe
    }

    "folded" in {
      val maybe1: Maybe[String] = E("test-name").toMaybe
      val maybe2: Maybe[String] = "test".toMaybe

      maybe1.fold(_.name, identity) shouldBe "test-name"
      maybe2.fold(_.name, identity) shouldBe "test"
    }

    "get with a default value" in {
      val maybe1: Maybe[String] = E("test").toMaybe
      val maybe2: Maybe[String] = "test".toMaybe

      maybe1.getOrElse("default") shouldBe "default"
      maybe2.getOrElse("default") shouldBe "test"
    }

    "be replaced with an alternative" in {
      val maybe1: Maybe[String] = E("test-1").toMaybe
      val maybe2: Maybe[String] = E("test-2").toMaybe
      val maybe3: Maybe[String] = "test".toMaybe

      (maybe1 orElse maybe2) shouldBe E("test-2").toMaybe
      (maybe1 orElse maybe3) shouldBe "test".toMaybe
    }

    "be compared for equality" in {
      val maybe1: Maybe[String] = E("test-1").toMaybe
      val maybe2: Maybe[String] = E("test-1").toMaybe
      val maybe3: Maybe[String] = E("test-2").toMaybe
      val maybe4: Maybe[String] = "test".toMaybe
      val maybe5: Maybe[String] = "test".toMaybe
      val maybe6: Maybe[String] = "TEST".toMaybe

      maybe1 shouldBe maybe2
      maybe1 should not be maybe3
      maybe1 should not be maybe4

      maybe4 shouldBe maybe5
      maybe4 should not be maybe6
      maybe4 should not be maybe1
    }

    "have hash code" in {
      val maybe1: Maybe[String] = E("test-1").toMaybe
      val maybe2: Maybe[String] = E("test-1").toMaybe
      val maybe3: Maybe[String] = E("test-2").toMaybe
      val maybe4: Maybe[String] = "test".toMaybe
      val maybe5: Maybe[String] = "test".toMaybe
      val maybe6: Maybe[String] = "TEST".toMaybe

      maybe1.hashCode shouldBe maybe2.hashCode
      maybe1.hashCode should not be maybe3.hashCode
      maybe1.hashCode should not be maybe4.hashCode

      maybe4.hashCode shouldBe maybe5.hashCode
      maybe4.hashCode should not be maybe6.hashCode
      maybe4.hashCode should not be maybe1.hashCode
    }

    "be converted to a String" in {
      val maybe1: Maybe[String] = E("test").toMaybe
      val maybe2: Maybe[String] = "test".toMaybe

      maybe1.toString shouldBe """{"name":"test"}"""
      maybe2.toString shouldBe "test"
    }
  }

  "Constructing a Maybe from an Option" should {
    "produce a failure Maybe when it is None" in {
      Maybe.fromOption(Option.empty[String], E("test")) shouldBe E("test").toMaybe[String]
      Option.empty[String].toMaybe(E("test"))           shouldBe E("test").toMaybe[String]
    }

    "produce a success Maybe when it is Some" in {
      Maybe.fromOption(Option[String]("test"), E("test")) shouldBe "test".toMaybe
      Option[String]("test").toMaybe(E("test"))           shouldBe "test".toMaybe
    }
  }

  "Constructing a Maybe from an Either" should {
    "produce a failure Maybe when it is Left" in {
      Maybe.fromEither[Int, String](Left(1), i => E(code = i)) shouldBe E(code = 1).toMaybe[String]
      Left[Int, String](1).toMaybe(i => E(code = i))           shouldBe E(code = 1).toMaybe[String]
    }

    "produce a success Maybe when it is Right" in {
      Maybe.fromEither[Int, String](Right("test"), i => E(code = i)) shouldBe "test".toMaybe
      Right[Int, String]("test").toMaybe(i => E(code = i))           shouldBe "test".toMaybe
    }

    "produce a Maybe when Left of it is already an E" in {
      val maybe1: Maybe[String] = Left[E, String](E("test"))
      val maybe2: Maybe[String] = Right[E, String]("test")

      maybe1 shouldBe E("test").toMaybe
      maybe2 shouldBe "test".toMaybe
    }
  }

  "Constructing a Maybe from a Try" should {
    "produce a failure Maybe when it is Failure" in {
      val cause = new Exception()

      Maybe.fromTry(TryFailure[String](cause), c => E().cause(c)) shouldBe E().cause(cause).toMaybe[String]
      TryFailure[String](cause).toMaybe(c => E().cause(c))        shouldBe E().cause(cause).toMaybe[String]
    }

    "produce a success Maybe when it is Success" in {
      Maybe.fromTry(TrySuccess[String]("test"), c => E().cause(c)) shouldBe "test".toMaybe
      TrySuccess[String]("test").toMaybe(c => E().cause(c))        shouldBe "test".toMaybe
    }
  }
}
