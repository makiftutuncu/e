package e

import e.test.ESuite
import org.scalacheck.Gen
import org.scalacheck.Prop.forAll

class ETest extends ESuite {
  property("Constructing an E") {
    val empty = E.empty
    empty.assertCode(None)
    empty.assertName(None)
    empty.assertMessage(None)
    empty.assertCauses(List.empty)
    empty.assertData(Map.empty)
    empty.assertTime(None)

    forAll { (code: Option[Int], name: Option[String], message: Option[String], causes: List[E], data: Map[String, String], time: Option[Long]) =>
      val e = E(code, name, message, causes, data, time)

      e.assertCode(code)
      E(code = code).assertCode(code)
      code.foreach(c => E.code(c).assertCode(Some(c)))

      e.assertName(name)
      E(name = name).assertName(name)
      name.foreach(n => E.name(n).assertName(Some(n)))

      e.assertMessage(message)
      E(message = message).assertMessage(message)
      message.foreach(m => E.message(m).assertMessage(Some(m)))

      e.assertCauses(causes)
      E(causes = causes).assertCauses(causes)
      E.causes(causes).assertCauses(causes)

      e.assertData(data)
      E(data = data).assertData(data)
      E.data(data).assertData(data)

      e.assertTime(time)
      E(time = time).assertTime(time)
      time.foreach(t => E.time(t).assertTime(Some(t)))
    }

    forAll { cause: E =>
      val e = E.cause(cause)
      assert(e.hasCause)
      assert(e.causes.contains(cause))
    }

    forAll { (k: String, v: String) =>
      val e = E.data(k, v)
      assert(e.hasData)
      assertEquals(e.data.get(k), Some(v))
    }

    forAll { t: (String, String) =>
      val e = E.data(t)
      val (k, v) = t
      assert(e.hasData)
      assertEquals(e.data.get(k), Some(v))
    }

    forAll(genNow) { generatedNow: Long =>
      val e = E.now
      assert(e.hasTime)
      assertAlmostSame(generatedNow, e.time.get)
    }

    forAll { (condition: Boolean, cause: E) =>
      val e = E.causeIf(condition, cause)

      if (condition) {
        assert(e.hasCause)
        assert(e.causes.contains(cause))
      } else {
        assert(!e.hasCause)
        assertEquals(e, E.empty)
      }
    }
  }

  property("Getting a modified copy an E") {
    forAll { (e: E, code: Int) =>
      e.code(code).assertCode(Some(code))
    }

    forAll { (e: E, name: String) =>
      e.name(name).assertName(Some(name))
    }

    forAll { (e: E, message: String) =>
      e.message(message).assertMessage(Some(message))
    }

    forAll { (e: E, causes: List[E]) =>
      val modified = e.causes(causes)
      assert(modified.hasCause)
      causes.foreach(c => assert(modified.causes.contains(c)))
    }

    forAll { (e: E, cause: E) =>
      val modified = e.cause(cause)
      assert(modified.hasCause)
      assert(modified.causes.contains(cause))
    }

    forAll { (e: E, data: Map[String, String]) =>
      val modified = e.data(data)
      assert(modified.hasData)
      data.foreach { case (k, v) => assertEquals(modified.data.get(k), Some(v)) }
    }

    forAll { (e: E, k: String, v: String) =>
      val modified = e.data(k, v)
      assert(modified.hasData)
      assertEquals(modified.data.get(k), Some(v))
    }

    forAll { (e: E, t: (String, String)) =>
      val modified = e.data(t)
      val (k, v) = t
      assert(modified.hasData)
      assertEquals(modified.data.get(k), Some(v))
    }

    forAll { (e: E, time: Long) =>
      e.time(time).assertTime(Some(time))
    }

    forAll(genE, genNow) { (e: E, generatedNow: Long) =>
      val modified = e.now
      assert(modified.hasTime)
      assertAlmostSame(generatedNow, modified.time.get)
    }

    forAll { (e: E, condition: Boolean, cause: E) =>
      val modified = e.causeIf(condition, cause)

      if (condition) {
        assert(modified.hasCause)
        assert(modified.causes.contains(cause))
      } else {
        assertEquals(modified, e)
      }
    }
  }

  property("Getting trace of an E") {
    forAll { (e1: E, e2: E, e3: E, e4: E, e5: E, e6: E) =>
      val e = e1.causes(e2, e3.causes(e4, e5), e6)

      val expectedTrace =
        s"""$e1
           |  $e2
           |  $e3
           |    $e4
           |    $e5
           |  $e6""".stripMargin

      assertNoDiff(e.trace, expectedTrace)
    }
  }

  property("Converting an E to an EOr") {
    forAll { e: E =>
      assertEquals(EOr[String](e), e.as[String])
    }
  }

  property("Converting an E to EException") {
    forAll { e: E =>
      assertEquals(EException(e), e.toException)
    }
  }

  property("Constructing an E from a Throwable") {
    forAll { message: String =>
      val ex = new Exception(message)
      val e = E.message(message)

      assertEquals(E.fromThrowable(ex), e)
      assertEquals(ex.toE, e)
    }

    forAll { e: E =>
      assertEquals(E.fromThrowable(EException(e)), e)
    }
  }

  property("Converting an E to String") {
    assertEquals(E.empty.toString, "[Empty E]")

    forAll(
      Gen.option(Gen.const(1)),
      Gen.option(Gen.const("test")),
      Gen.option(Gen.const("Test")),
      Gen.oneOf(Map.empty[String, String], Map("foo" -> "bar", "test" -> "42"))
    ) { (code: Option[Int], name: Option[String], message: Option[String], data: Map[String, String]) =>
      val e = E(code = code, name = name, message = message, data = data)

      val parts = List(
        code.fold("")(c => s"E$c"),
        name.fold("")(identity),
        message.fold("")(identity),
        if (data.isEmpty) "" else "[foo -> bar, test -> 42]"
      ).filter(_.nonEmpty)

      assertEquals(e.toString, if (parts.isEmpty) "[Empty E]" else parts.mkString(" | "))
    }
  }
}
