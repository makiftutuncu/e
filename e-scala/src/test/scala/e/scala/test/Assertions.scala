package e.scala.test

import e.scala.EOr.{Failure, Success}
import e.scala.{E, EOr}

trait Assertions { self: munit.Assertions =>
  implicit class EAssertions(e: E) {
    def assertCode(code: Option[Int]): Unit = {
      assertEquals(e.hasCode, code.isDefined)
      assertEquals(e.code,    code)
    }

    def assertName(name: Option[String]): Unit = {
      assertEquals(e.hasName, name.isDefined)
      assertEquals(e.name,    name)
    }

    def assertMessage(message: Option[String]): Unit = {
      assertEquals(e.hasMessage, message.isDefined)
      assertEquals(e.message,    message)
    }

    def assertCauses(causes: List[E]): Unit = {
      assertEquals(e.hasCause, causes.nonEmpty)
      assertEquals(e.causes,   causes)
    }

    def assertData(data: Map[String, String]): Unit = {
      assertEquals(e.hasData, data.nonEmpty)
      assertEquals(e.data,    data)
    }

    def assertTime(time: Option[Long]): Unit = {
      assertEquals(e.hasTime, time.isDefined)
      assertEquals(e.time,    time)
    }
  }

  def assertAlmostSame(expected: Long, actual: Long, threshold: Long = 1000L): Unit = {
    assert(
      (actual - expected).abs < threshold,
      s"Times were apart mode than $threshold ms, expected: $expected, actual: $actual"
    )
  }

  implicit class EOrAssertions[A](eor: EOr[A]) {
    def assertError(thatE: E): Unit =
      eor match {
        case Success(a)     => fail(s"EOr did not contain error, it contained: $a")
        case Failure(thisE) => assertEquals(thisE, thatE, s"Expected: $thatE, Actual: $thisE")
      }

    def assertValue(thatA: A): Unit = eor match {
      case Failure(e)     => fail(s"EOr did not contain value, it contained: $e")
      case Success(thisA) => assertEquals(thisA, thatA)
    }
  }
}
