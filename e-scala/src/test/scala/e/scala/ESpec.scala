package e.scala

import e.AbstractE
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class ESpec extends AnyWordSpec with Matchers {
  val cause = new Exception("Test Exception")
  val data  = Map("test" -> "data")

  "An E" can {
    "be constructed with all the fields" in {
      val e = E("test-name", "Test Message", 1, Some(cause), data)

      e.hasName shouldBe true
      e.name    shouldBe "test-name"

      e.hasMessage shouldBe true
      e.message    shouldBe "Test Message"

      e.hasCode shouldBe true
      e.code    shouldBe 1

      e.hasCause                              shouldBe true
      e.cause.map(_.getMessage).getOrElse("") shouldBe "Test Exception"

      e.hasData          shouldBe true
      e.data.size        shouldBe 1
      e.data.get("test") shouldBe Some("data")
    }

    "be constructed with no fields" in {
      val e = E()

      e.hasName shouldBe false
      e.name    shouldBe ""

      e.hasMessage shouldBe false
      e.message    shouldBe ""

      e.hasCode shouldBe false
      e.code    shouldBe AbstractE.EMPTY_CODE

      e.hasCause shouldBe false
      e.cause    shouldBe None

      e.hasData shouldBe false
      e.data    shouldBe Map.empty
    }

    "be constructed as empty" in {
      E() shouldBe E.empty
    }
  }

  "Converting an E to an Exception" should {
    "include message" in {
      val e = E("test-name", "Test Message")

      val expected = new Exception(e.toString)
      val actual   = e.toException()

      actual.getMessage shouldBe expected.getMessage
      actual.getCause   shouldBe null
    }

    "include message and cause" in {
      val e = E("test-name", "Test Message").cause(cause)

      val expected = new Exception(e.toString, cause)
      val actual   = e.toException()

      actual.getMessage shouldBe expected.getMessage
      actual.getCause   shouldBe expected.getCause
    }
  }

  "Converting an E to a Maybe" should {
    "produce a failed Maybe" in {
      val e = E("test-name", "Test Message")

      val expected = Maybe.failure(e)
      val actual   = e.toMaybe

      actual shouldBe expected
    }
  }

  "Checking if E instances are equal" should {
    "return false if any field is different between E instances" in {
      val e = E("test-name", "Test Message", 1, Some(cause), data)

      E("test-name", "Test Message", 2, Some(cause),                data)                should not be e
      E("foo",       "Test Message", 1, Some(cause),                data)                should not be e
      E("test-name", "bar",          1, Some(cause),                data)                should not be e
      E("test-name", "Test Message", 1, Some(new Exception("baz")), data)                should not be e
      E("test-name", "Test Message", 1, Some(cause),                Map("foo" -> "bar")) should not be e
    }

    "return true for E instances whose fields are equal" in {
      val e = E("test-name", "Test Message", 1, Some(cause), data)

      E("test-name", "Test Message", 1, Some(cause), data) shouldBe e
    }
  }

  "Generating hash code of an E instance" should {
    "generate a different hash code if any field is different between E instances" in {
      val e = E("test-name", "Test Message", 1, Some(cause), data)

      E("test-name", "Test Message", 2, Some(cause),                data).hashCode                should not be e.hashCode
      E("foo",       "Test Message", 1, Some(cause),                data).hashCode                should not be e.hashCode
      E("test-name", "bar",          1, Some(cause),                data).hashCode                should not be e.hashCode
      E("test-name", "Test Message", 1, Some(new Exception("baz")), data).hashCode                should not be e.hashCode
      E("test-name", "Test Message", 1, Some(cause),                Map("foo" -> "bar")).hashCode should not be e.hashCode
    }

    "generate the same hash code for E instances whose fields are equal" in {
      val e = E("test-name", "Test Message", 1, Some(cause), data)

      E("test-name", "Test Message", 1, Some(cause), data).hashCode() shouldBe e.hashCode
    }
  }
}
