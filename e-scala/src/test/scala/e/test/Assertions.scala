package e.test

import e.{E, or}

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

  def assertAlmostSame(generated: Long, created: Long, threshold: Long = 1000L): Unit = {
    assert(
      (created - generated).abs < threshold,
      s"Times were apart mode than $threshold ms, generated: $generated, created: $created"
    )
  }

  implicit class EOrAssertions[A](eor: A or E) {
    def assertError(e: E): Unit = {
      assert(eor.isFailure)
      assert(!eor.isSuccess)
      assertEquals(eor.error, Some(e))
      assertEquals(eor.value, None)
    }

    def assertValue(a: A): Unit = {
      assert(!eor.isFailure)
      assert(eor.isSuccess)
      assertEquals(eor.error, None)
      assertEquals(eor.value, Some(a))
    }
  }
}
