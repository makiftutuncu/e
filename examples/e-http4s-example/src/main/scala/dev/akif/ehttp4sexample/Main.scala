package dev.akif.ehttp4sexample

import cats.data.Kleisli
import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits._
import dev.akif.ehttp4sexample.common.{Controller, Errors}
import dev.akif.ehttp4sexample.people.{PeopleController, PeopleRepository, PeopleService}
import doobie.util.transactor.Transactor
import e.circe._
import e.scala._
import org.flywaydb.core.Flyway
import org.http4s.circe.jsonEncoderOf
import org.http4s.headers.`Content-Type`
import org.http4s.server.Router
import org.http4s.server.blaze.BlazeServerBuilder
import org.http4s._

object Main extends IOApp {
  val db: Transactor[IO] = {
    val url  = "jdbc:h2:mem:people;DB_CLOSE_DELAY=-1"
    val user = "test"
    val pass = "test"

    Flyway.configure()
          .dataSource(url, user, pass)
          .outOfOrder(false)
          .load()
          .migrate()

    Transactor.fromDriverManager[IO]("org.h2.Driver", url, user, pass)
  }

  val peopleRepository: PeopleRepository = new PeopleRepository(db)

  val peopleService: PeopleService = new PeopleService(peopleRepository)

  val controllers: List[Controller[IO]] =
    List(
      PingController,
      new PeopleController(peopleService)
    )

  val app: Http[IO, IO] =
    Kleisli { request =>
      val routes = Router(controllers.map(c => c.path -> c.route): _*)

      routes.run(request).getOrElse {
        val e = Errors.notFound
                      .message("Requested resource is not found!")
                      .data("method" -> request.method.name)
                      .data("uri"    -> request.uri)

        eToResponse(e)
      }.redeem(
        {
          case EException(e) => eToResponse(e)
          case t             => eToResponse(Errors.unexpected.message("An unexpected error occurred!").cause(t.toE()))
        },
        identity
      )
    }

  def run(args: List[String]): IO[ExitCode] =
    BlazeServerBuilder[IO].bindHttp(8080, "localhost")
                          .withHttpApp(app)
                          .resource
                          .use(_ => IO.never)
                          .as(ExitCode.Success)

  private def eToResponse(e: E): Response[IO] = {
    val status  = e.code.flatMap(c => Status.fromInt(c).toOption).getOrElse(Status.InternalServerError)
    val headers = Headers.of(`Content-Type`(MediaType.application.json))
    val entity  = jsonEncoderOf[IO, E].toEntity(e)

    Response(status, headers = headers, body = entity.body)
  }
}
