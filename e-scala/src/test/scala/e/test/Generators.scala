package e.test

import e.E
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Arbitrary, Gen}

trait Generators {
  // Important: generated E doesn't have `causes` since its endless recursive loop.
  val genE: Gen[E] =
    for {
      code    <- arbitrary[Option[Int]]
      name    <- arbitrary[Option[String]]
      message <- arbitrary[Option[String]]
      data    <- arbitrary[Map[String, String]]
      time    <- arbitrary[Option[Long]]
    } yield {
      E(code, name, message, List.empty, data, time)
    }

  val genNow: Gen[Long] = Gen.const(System.currentTimeMillis)

  implicit val arbE: Arbitrary[E] = Arbitrary[E](genE)
}
