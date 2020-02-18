package dev.akif.ehttp4sexample

import cats.effect.IO
import dev.akif.ehttp4sexample.common.Controller
import org.http4s.HttpRoutes
import org.http4s.dsl.io._

object PingController extends Controller[IO]("/") {
  override val route: HttpRoutes[IO] =
    HttpRoutes.of[IO] {
      case GET -> Root / "ping" => Ok("pong")
    }
}
