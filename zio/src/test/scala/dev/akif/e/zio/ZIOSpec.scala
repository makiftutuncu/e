package dev.akif.e.zio

import dev.akif.e.E
import dev.akif.e.zio.syntax._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import zio.Exit.{Failure, Success}
import zio.internal.PlatformLive
import zio.{Cause, ZIO, Runtime => ZIORuntime}

class ZIOSpec extends AnyWordSpec with Matchers {
  val divider: Divider = (a, b) => a / b

  implicit val runtime: ZIORuntime[Divider] = ZIORuntime(divider, PlatformLive.Default)

  "MaybeZR" should {
    "fail with E" in {
      test(divide1(3, 0), E.of("divide-by-zero", "Cannot divide 3 by 0!"))
    }

    "succeed with a value" in {
      test(divide1(6, 2), 3)
    }
  }

  "MaybeZ" should {
    "fail with E" in {
      test(divide2(3, 0), E.of("divide-by-zero", "Cannot divide 3 by 0!"))
    }

    "succeed with a value" in {
      test(divide2(6, 2), 3)
    }
  }


  type Divider = (Int, Int) => Int

  def divide1(a: Int, b: Int): MaybeZR[Divider, Int] =
    ZIO.accessM[Divider].apply[E, Int] { divider =>
      if (b == 0) {
        E.of("divide-by-zero", s"Cannot divide $a by 0!").maybeZR
      } else {
        divider(a, b).maybeZR
      }
    }

  def divide2(a: Int, b: Int): MaybeZ[Int] =
    if (b == 0) {
      E.of("divide-by-zero", s"Cannot divide $a by 0!").maybeZ
    } else {
      (a / b).maybeZ
    }

  def test[R, A](zio: ZIO[R, E, A], e: E)(implicit runtime: ZIORuntime[R]): Unit = {
    runtime.unsafeRunSync(zio) shouldBe Failure(Cause.fail(e))
  }

  def test[R, A](zio: ZIO[R, E, A], a: A)(implicit runtime: ZIORuntime[R]): Unit = {
    runtime.unsafeRunSync(zio) shouldBe Success(a)
  }
}
