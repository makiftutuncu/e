package dev.akif.ehttp4sexample.common

import org.http4s.HttpRoutes

abstract class Controller[F[_]](val path: String) {
    val route: HttpRoutes[F]
}
