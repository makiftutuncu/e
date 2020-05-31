package e

import e.scala._
import zio.test._
import zio.test.environment.TestEnvironment
import zio.test.Assertion._
import e.ezio._

object ZIOTest extends DefaultRunnableSpec {
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

  private def divide(a: Int, b: Int): REIO[Divider, Int] = REIO.accessM[Divider](_.divide(a, b))

  private val allTests: ZSpec[Divider, E] = suite("e-zio")(
    testM("failed EIO") {
      assertM(divider.divide(2, 0).run) {
        fails {
          equalTo {
            divideByZero.data("input", 2)
          }
        }
      }
    },

    testM("successful EIO") {
      assertM(divider.divide(4, 2)) {
        equalTo {
          2
        }
      }
    },

    testM("failed REIO") {
      assertM(divide(3, 0).run) {
        fails {
          equalTo {
            divideByZero.data("input", 3)
          }
        }
      }
    },

    testM("successful REIO") {
      assertM(divide(6, 2)) {
        equalTo {
          3
        }
      }
    }
  )

  override def spec: ZSpec[TestEnvironment, E] = allTests.provide(divider)
}
