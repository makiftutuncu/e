package dev.akif.ehttp4sexample

import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits._
import dev.akif.ehttp4sexample.common.Controller
import dev.akif.ehttp4sexample.people.{PeopleController, PeopleRepository, PeopleService}
import doobie.util.transactor.Transactor
import org.flywaydb.core.Flyway
import org.http4s.Http
import org.http4s.implicits._
import org.http4s.server.Router
import org.http4s.server.blaze.BlazeServerBuilder

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

  val app: Http[IO, IO] = Router(controllers.map(c => c.path -> c.route):_*).orNotFound

  def run(args: List[String]): IO[ExitCode] =
    BlazeServerBuilder[IO].bindHttp(8080, "localhost")
                          .withHttpApp(app)
                          .resource
                          .use(_ => IO.never)
                          .as(ExitCode.Success)
}
