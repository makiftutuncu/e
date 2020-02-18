package dev.akif.ehttp4sexample.common

import cats.effect.IO
import io.circe.{Decoder, Encoder}
import org.http4s.{EntityDecoder, EntityEncoder, HttpRoutes}
import org.http4s.circe.{jsonOf, jsonEncoderOf}

abstract class Controller[F[_]](val path: String) {
  val route: HttpRoutes[F]

  implicit def entityDecoder[A: Decoder]: EntityDecoder[IO, A] = jsonOf[IO, A]
  implicit def entityEncoder[A: Encoder]: EntityEncoder[IO, A] = jsonEncoderOf[IO, A]
}
