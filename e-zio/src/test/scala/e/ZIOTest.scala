package e

import e.ezio._
import e.scala._
import zio.{Scope, ZLayer}
import zio.test.Assertion._
import zio.test._

object ZIOTest extends ZIOSpecDefault {
  private val divideByZero: E = E.name("divideError").message("Cannot divide by 0!")

  class Divider {
    def divide(a: Int, b: Int): EIO[Int] =
      if (b == 0) {
        divideByZero.data("input", a.toString).toEIO[Int]
      } else {
        (a / b).toEIO
      }
  }

  private val divider: Divider = new Divider

  private def divide(a: Int, b: Int): REIO[Divider, Int] = REIO.serviceWithZIO[Divider](_.divide(a, b))

  override def spec: Spec[TestEnvironment with Scope, Any] =
    suite("e-zio")(
      test("failed EIO") {
        assertZIO(divider.divide(2, 0).exit) {
          fails {
            equalTo {
              divideByZero.data("input", 2)
            }
          }
        }
      },
      test("successful EIO") {
        assertZIO(divider.divide(4, 2)) {
          equalTo {
            2
          }
        }
      },
      test("failed REIO") {
        assertZIO(divide(3, 0).exit) {
          fails {
            equalTo {
              divideByZero.data("input", 3)
            }
          }
        }
      },
      test("successful REIO") {
        assertZIO(divide(6, 2)) {
          equalTo {
            3
          }
        }
      }
    ).provide(ZLayer.succeed(divider))
}
