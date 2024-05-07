package dev.akif.ehttp4sexample.people

import cats.effect.IO
import dev.akif.ehttp4sexample.common.Controller
import io.circe.{Decoder, Encoder}
import org.http4s.dsl.io._
import org.http4s.{EntityDecoder, EntityEncoder, HttpRoutes, Response}
import org.http4s.circe.{jsonEncoderOf, jsonOf}

class PeopleController(val peopleService: PeopleService) extends Controller[IO]("/people") {
    implicit def entityDecoder[A: Decoder]: EntityDecoder[IO, A] = jsonOf[IO, A]
    implicit def entityEncoder[A: Encoder]: EntityEncoder[IO, A] = jsonEncoderOf[IO, A]

    override val route: HttpRoutes[IO] =
        HttpRoutes.of[IO] {
            case GET -> Root                         => getAll
            case GET -> Root / LongVar(id)           => get(id)
            case request @ POST -> Root              => request.as[CreatePerson].flatMap(c => create(c))
            case request @ PUT -> Root / LongVar(id) => request.as[UpdatePerson].flatMap(u => update(id, u))
            case DELETE -> Root / LongVar(id)        => delete(id)
        }

    def getAll: IO[Response[IO]] = peopleService.getAll.flatMap(p => Ok(p))

    def get(id: Long): IO[Response[IO]] = peopleService.get(id).flatMap(p => Ok(p))

    def create(create: CreatePerson): IO[Response[IO]] = peopleService.create(create).flatMap(p => Created(p))

    def update(id: Long, update: UpdatePerson): IO[Response[IO]] = peopleService.update(id, update).flatMap(p => Ok(p))

    def delete(id: Long): IO[Response[IO]] = peopleService.delete(id).flatMap(p => Ok(p))
}
