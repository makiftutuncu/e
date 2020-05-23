package e

import munit.FunSuite

class ZIOSpec extends FunSuite {
  /*
  type Divider = (Int, Int) => MaybeZ[Int]

  def divide(a: Int, b: Int): MaybeZ[Int] =
    if (b == 0) {
      E("divide-by-zero", "Cannot divide by 0!", data = Map("input" -> a.toString)).maybeZ
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
      test(divideWithEnvironment(3, 0), E("divide-by-zero", "Cannot divide by 0!", data = Map("input" -> "3")))
    }

    "succeed with a value" in {
      test(divideWithEnvironment(6, 2), 3)
    }
  }

  "MaybeZ" should {
    "fail with E" in {
      test(divide(3, 0), E("divide-by-zero", "Cannot divide by 0!", data = Map("input" -> "3")))
    }

    "succeed with a value" in {
      test(divide(6, 2), 3)
    }
  }
  */
}
