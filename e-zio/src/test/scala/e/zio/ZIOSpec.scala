package e.zio

import e.scala.E
import e.zio.implicits._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import zio.Exit.{Failure, Success}
import zio.internal.PlatformLive
import zio.{Cause, ZIO, Runtime => ZIORuntime}

class ZIOSpec extends AnyWordSpec with Matchers {
  type Divider = (Int, Int) => MaybeZ[Int]

  def divide(a: Int, b: Int): MaybeZ[Int] =
    if (b == 0) {
      E(name = "divide-by-zero", message = s"Cannot divide by 0!", data = Map("input" -> a.toString)).maybeZ
    } else {
      (a / b).maybeZ
    }

  val divider: Divider = (a: Int, b: Int) => divide(a, b)

  implicit val runtime: ZIORuntime[Divider] = ZIORuntime(divider, PlatformLive.Default)

  def divideWithEnvironment(a: Int, b: Int): MaybeZR[Divider, Int] =
    for {
      divider <- ZIO.environment[Divider]
      result  <- divider(a, b)
    } yield {
      result
    }

  def test[R, A](zio: ZIO[R, E, A], e: E)(implicit runtime: ZIORuntime[R]): Unit = {
    runtime.unsafeRunSync(zio) shouldBe Failure(Cause.fail(e))
  }

  def test[R, A](zio: ZIO[R, E, A], a: A)(implicit runtime: ZIORuntime[R]): Unit = {
    runtime.unsafeRunSync(zio) shouldBe Success(a)
  }

  "MaybeZR" should {
    "fail with E" in {
      test(divideWithEnvironment(3, 0), E(name = "divide-by-zero", message = s"Cannot divide by 0!", data = Map("input" -> "3")))
    }

    "succeed with a value" in {
      test(divideWithEnvironment(6, 2), 3)
    }
  }

  "MaybeZ" should {
    "fail with E" in {
      test(divide(3, 0), E(name = "divide-by-zero", message = s"Cannot divide by 0!", data = Map("input" -> "3")))
    }

    "succeed with a value" in {
      test(divide(6, 2), 3)
    }
  }
}
