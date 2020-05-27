package e

import e.test.ESuite
import org.scalacheck.Prop.forAll

import scala.util.Try

class EOrTest extends ESuite {
  property("Constructing an EOr") {
    EOr.unit.assertValue(Some(()))

    forAll { e: E =>
      EOr[String](e).assertError(e)
      e.toEOr[String].assertError(e)
    }

    forAll { string: String =>
      EOr[String](string).assertValue(string)
      string.orE.assertValue(string)
    }

    forAll { option: Option[String] =>
      val none = E.name("None")

      val eor1 = EOr.fromOption(option)(none)
      option.fold(eor1.assertError(none))(s => eor1.assertValue(s))

      val eor2 = option.orE(none)
      assertEquals(eor1, eor2)
    }

    forAll { either: Either[Int, String] =>
      val toE = { i: Int => E.code(i) }

      val eor1 = EOr.fromEither(either)(toE)
      either.fold(i => eor1.assertError(toE(i)), s => eor1.assertValue(s))

      val eor2 = either.orE(toE)
      assertEquals(eor1, eor2)
    }

    forAll { `try`: Try[String] =>
      val toE = { t: Throwable => E.message(t.getMessage) }

      val eor1 = EOr.fromTry(`try`)(toE)
      `try`.fold(t => eor1.assertError(toE(t)), s => eor1.assertValue(s))

      val eor2 = `try`.orE(toE)
      assertEquals(eor1, eor2)
    }

    forAll { ex: Exception =>
      val toE = { t: Throwable => E.message(t.getMessage) }

      lazy val s: String = throw ex

      s.catching(toE).assertError(E.message(ex.getMessage))
    }
  }

  property("Mapping an EOr") {
    val failed = E.name("failed")

    failed.toEOr[Int].map(_.toString).assertError(failed)

    forAll { i: Int =>
      i.orE.map(_.toString).assertValue(i.toString)
    }
  }

  property("Flat mapping an EOr") {
    val failed1 = E.name("failed1")
    val failed2 = E.name("failed2")

    failed1.toEOr[Int].flatMap(_ => failed2.toEOr[String]).assertError(failed1)
    failed1.toEOr[Int].flatMap(_.toString.orE).assertError(failed1)

    forAll { i: Int =>
      i.orE.flatMap(_ => failed2.toEOr[String]).assertError(failed2)
      i.orE.flatMap(_.toString.orE).assertValue(i.toString)
    }
  }

  property("Mapping error of an EOr") {
    42.orE.mapError(_.code(1)).assertValue(42)

    forAll { e: E =>
      e.toEOr[Int].mapError(_.code(1)).assertError(e.code(1))
    }
  }

  property("Flat mapping error of an EOr") {
    42.orE.flatMapError(_.code(1).toEOr[Int]).assertValue(42)
    42.orE.flatMapError(_ => 43.orE).assertValue(42)

    forAll { e: E =>
      e.toEOr[Int].flatMapError(_ => 42.orE).assertValue(42)
      e.toEOr[Int].flatMapError(_.code(1).toEOr[Int]).assertError(e.code(1))
    }
  }

  property("Folding an EOr") {
    val e1 = E.empty
    val e2 = E.code(1)

    assertEquals(e1.toEOr[Int].fold[String](_.code.fold("")(_.toString), _.toString), "")
    assertEquals(e2.toEOr[Int].fold[String](_.code.fold("")(_.toString), _.toString), "1")

    forAll { i: Int =>
      assertEquals(i.orE.fold[String](_.code.fold("")(_.toString), _.toString), i.toString)
    }
  }

  property("Getting value of an EOr or a default value") {
    val e = E.code(1)

    assertEquals(e.toEOr[String].getOrElse(""), "")

    forAll { s: String =>
      assertEquals(s.orE.getOrElse(""), s)
    }
  }

  property("Getting an EOr or an alternative one on error") {
    val e1 = E.code(1)
    val e2 = E.code(2)

    (e1.toEOr[String] orElse e2.toEOr[String]).assertError(e2)

    forAll { s: String =>
      (s.orE orElse e2.toEOr[String]).assertValue(s)
      (e1.toEOr[String] orElse s.orE).assertValue(s)
    }
  }

  property("Getting an EOr or a next one on value") {
    val e1 = E.code(1)
    val e2 = E.code(2)

    (e1.toEOr[String] andThen e2.toEOr[String]).assertError(e1)

    forAll { (s: String, i: Int) =>
      (e1.toEOr[String] andThen s.orE).assertError(e1)
      (s.orE andThen e2.toEOr[String]).assertError(e2)
      (s.orE andThen i.orE).assertValue(i)
    }
  }

  property("Performing side-effect on an EOr") {
    val e = E.code(1)
    var counter = 0
    var previous = 0

    e.toEOr[String].foreach { _ => previous = counter; counter = previous + 1 }
    assertEquals(counter, 0)
    assertEquals(previous, 0)

    forAll { s: String =>
      s.orE.foreach { _ => previous = counter; counter = previous + 1 }
      assertEquals(counter, previous + 1)
    }
  }

  property("Filtering an EOr") {
    val e = E.code(1)
    val negative = E.name("negative")

    e.toEOr[Int].filter(_ > 0).assertError(e)
    e.toEOr[Int].filter(_ > 0, _ => negative).assertError(e)

    forAll { i: Int =>
      val eor1 = i.orE.filter(_ > 0)
      if (i > 0) {
        eor1.assertValue(i)
      } else {
        eor1.assertError(EOr.filteredError.data("value", i))
      }

      val eor2 = i.orE.filter(_ > 0, _ => negative)
      if (i > 0) {
        eor2.assertValue(i)
      } else {
        eor2.assertError(negative)
      }
    }
  }

  property("Filtering an EOr with default error") {
    val e = E.code(1)

    e.toEOr[Int].withFilter(_ > 0).assertError(e)

    forAll { i: Int =>
      val eor1 = i.orE.withFilter(_ > 0)
      if (i > 0) {
        eor1.assertValue(i)
      } else {
        eor1.assertError(EOr.filteredError.data("value", i))
      }
    }
  }

  property("Handling error in an EOr") {
    val e1 = E.code(1)
    val e2 = E.code(2)

    val handler: PartialFunction[E, String] = {
      case E(Some(1), _, _, _, _, _) => "handled"
    }

    e1.toEOr[String].handle(handler).assertValue("handled")
    e2.toEOr[String].handle(handler).assertError(e2)

    forAll { s: String =>
      s.orE.handle(handler).assertValue(s)
    }
  }

  property("Handling error in an EOr with another EOr") {
    val e1 = E.code(1)
    val e2 = E.code(2)
    val e3 = E.code(3)
    val e4 = E.code(4)

    val handler: PartialFunction[E, String or E] = {
      case E(Some(1), _, _, _, _, _) => "handled".orE
      case E(Some(2), _, _, _, _, _) => e3.toEOr[String]
    }

    e1.toEOr[String].handleWith(handler).assertValue("handled")
    e2.toEOr[String].handleWith(handler).assertError(e3)
    e4.toEOr[String].handleWith(handler).assertError(e4)

    forAll { s: String =>
      s.orE.handleWith(handler).assertValue(s)
    }
  }

  property("Equality and hash code of EOr") {
    val eor1 = "test1".orE
    val eor2 = "test2".orE

    assertEquals(eor1, "test1".orE)
    assertEquals(eor1.hashCode(), "test1".orE.hashCode())

    assertNotEquals(eor1, eor2)
    assertNotEquals(eor1.hashCode(), eor2.hashCode())

    forAll { (code: Int, name: String, message: String, causes: List[E], data: Map[String, String], time: Long) =>
      val e1 = E(Some(code), Some(name), Some(message), causes, data, Some(time))

      val differentEs = List(
        e1.copy(code = None),
        e1.code(code + 1),
        e1.copy(name = None),
        e1.name(name + "foo"),
        e1.copy(message = None),
        e1.message(message + "bar"),
        e1.copy(causes = List(E.empty)),
        e1.cause(E.empty),
        e1.copy(data = Map("foo" -> "bar")),
        e1.data("foo", "bar"),
        e1.copy(time = None),
        e1.now
      )

      differentEs.foreach { e2 =>
        val eor3 = e1.toEOr[String]
        val eor4 = e2.toEOr[String]

        assertNotEquals(eor3, eor1)
        assertNotEquals(eor3.hashCode(), eor1.hashCode())

        assertNotEquals(eor4, eor1)
        assertNotEquals(eor4.hashCode(), eor1.hashCode())

        assertNotEquals(eor3, eor4)
        assertNotEquals(eor3.hashCode(), eor4.hashCode())
      }
    }
  }

  property("Converting an EOr to String") {
    forAll { e: E =>
      assertEquals(e.toEOr[String].toString, e.toString)
    }

    forAll { i: Int =>
      assertEquals(i.orE.toString, i.toString)
    }
  }
}
