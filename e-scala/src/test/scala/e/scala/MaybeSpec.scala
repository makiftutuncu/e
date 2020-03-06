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

    "be unit" in {
      val maybe: Maybe[Unit] = Maybe.unit

      maybe.isSuccess shouldBe true
      maybe.eOpt      shouldBe None
      maybe.valueOpt  shouldBe Some(())
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
      val maybe3: Maybe[String] = "test-1".toMaybe
      val maybe4: Maybe[String] = "test-2".toMaybe

      (maybe1 orElse maybe2) shouldBe E("test-2").toMaybe
      (maybe1 orElse maybe3) shouldBe "test-1".toMaybe
      (maybe3 orElse maybe1) shouldBe "test-1".toMaybe
      (maybe3 orElse maybe4) shouldBe "test-1".toMaybe
    }

    "be moved to another Maybe, ignoring its value" in {
      val maybe1: Maybe[String] = E("test").toMaybe
      val maybe2: Maybe[String] = "test-1".toMaybe
      val maybe3: Maybe[String] = "test-2".toMaybe

      (maybe1 andThen E("test-2").toMaybe) shouldBe maybe1
      (maybe1 andThen "test".toMaybe)      shouldBe maybe1
      (maybe2 andThen maybe1)              shouldBe maybe1
      (maybe2 andThen maybe3)              shouldBe maybe3
    }

    "be filtered" in {
      val maybe1 = E("error").toMaybe[Int]
      val maybe2 = 5.toMaybe

      maybe1.filter(_ < 4)                                       shouldBe maybe1
      maybe1.filter(_ < 4, i => E("error-2").data("value" -> i)) shouldBe maybe1
      maybe2.filter(_ < 4)                                       shouldBe E("predicate-failed", "Value did not satisfy predicate!").data("value" -> 5).toMaybe[Int]
      maybe2.filter(_ < 4, i => E("error-2").data("value" -> i)) shouldBe E("error-2").data("value" -> 5).toMaybe[Int]
      maybe2.filter(_ > 4)                                       shouldBe maybe2
      maybe2.filter(_ > 4, i => E("error-2").data("value" -> i)) shouldBe maybe2
    }

    "be used to perform side effect" in {
      val sb1 = new StringBuilder
      val sb2 = new StringBuilder

      E("error").toMaybe[String].foreach(s => sb1.append(s))
      "test".toMaybe.foreach(s => sb2.append(s))

      sb1.toString() shouldBe ""
      sb2.toString() shouldBe "test"
    }

    "be handled with another Maybe" in {
      val maybe1 = E("error-1").toMaybe[Int]
      val maybe2 = E().toMaybe[Int]
      val maybe3 = 5.toMaybe

      maybe1.handleErrorWith { case _ => maybe2 } shouldBe maybe2
      maybe1.handleErrorWith { case _ => maybe3 } shouldBe maybe3

      maybe1.handleErrorWith { case e if e.hasName => 0.toMaybe } shouldBe 0.toMaybe
      maybe1.handleErrorWith { case e if e.hasName => maybe2 }    shouldBe maybe2
      maybe2.handleErrorWith { case e if e.hasName => 0.toMaybe } shouldBe maybe2
      maybe2.handleErrorWith { case e if e.hasName => maybe1 }    shouldBe maybe2

      maybe3.handleErrorWith { case _ => maybe2 }                 shouldBe maybe3
      maybe3.handleErrorWith { case _ => 0.toMaybe }              shouldBe maybe3
      maybe3.handleErrorWith { case e if e.hasName => maybe2 }    shouldBe maybe3
      maybe3.handleErrorWith { case e if e.hasName => 0.toMaybe } shouldBe maybe3
    }

    "be handled" in {
      val maybe1 = E("error-1").toMaybe[Int]
      val maybe2 = E().toMaybe[Int]
      val maybe3 = 5.toMaybe

      maybe1.handleError { case _ => 0 } shouldBe 0.toMaybe

      maybe1.handleError { case e if e.hasName => 0 } shouldBe 0.toMaybe
      maybe2.handleError { case e if e.hasName => 0 } shouldBe maybe2

      maybe3.handleError { case _ => 0 }              shouldBe maybe3
      maybe3.handleError { case e if e.hasName => 0 } shouldBe maybe3
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

    "produce a failure Maybe when it is Failure with an EException in it" in {
      val e = E("test")

      Maybe.fromTry(TryFailure[String](EException(e)), c => E().cause(c)) shouldBe e.toMaybe[String]
      TryFailure[String](EException(e)).toMaybe(c => E().cause(c))        shouldBe e.toMaybe[String]
    }

    "produce a success Maybe when it is Success" in {
      Maybe.fromTry(TrySuccess[String]("test"), c => E().cause(c)) shouldBe "test".toMaybe
      TrySuccess[String]("test").toMaybe(c => E().cause(c))        shouldBe "test".toMaybe
    }
  }

  "Constructing a Maybe by catching lambda" should {
    "produce a failure Maybe when an EException is thrown" in {
      val e = E("test")

      Maybe.catching(c => E().cause(c)) { throw EException(e) } shouldBe e.toMaybe[String]
    }

    "produce a failure Maybe when an exception is thrown" in {
      val cause = new Exception()

      Maybe.catching(c => E().cause(c)) { throw cause } shouldBe E().cause(cause).toMaybe[String]
    }

    "produce a success Maybe when a value is produced" in {
      Maybe.catching(c => E().cause(c)) { "test" } shouldBe "test".toMaybe
    }
  }

  "Constructing a Maybe by catching Maybe lambda" should {
    "produce a failure Maybe when an EException is thrown" in {
      val e = E("test")

      Maybe.catchingMaybe(c => E().cause(c)) { throw EException(e) } shouldBe e.toMaybe[String]
    }

    "produce a failure Maybe when an exception is thrown" in {
      val cause = new Exception()

      Maybe.catchingMaybe(c => E().cause(c)) { throw cause } shouldBe E().cause(cause).toMaybe[String]
    }

    "produce a success Maybe when a failure Maybe is produced" in {
      Maybe.catchingMaybe(c => E().cause(c)) { E().toMaybe[String] } shouldBe E().toMaybe[String]
    }

    "produce a success Maybe when a success Maybe is produced" in {
      Maybe.catchingMaybe(c => E().cause(c)) { "test".toMaybe } shouldBe "test".toMaybe
    }
  }
}
