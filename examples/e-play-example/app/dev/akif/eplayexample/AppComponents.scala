package dev.akif.eplayexample

import dev.akif.eplayexample.AppComponents.Modules
import dev.akif.eplayexample.common.DB
import dev.akif.eplayexample.people.{PeopleController, PeopleRepository, PeopleService}
import play.api.ApplicationLoader.Context
import play.api.BuiltInComponentsFromContext
import play.api.db.evolutions.EvolutionsComponents
import play.api.db.{DBApi, DBComponents, Database, HikariCPComponents}
import play.api.http.HttpErrorHandler
import play.api.mvc.EssentialFilter
import play.api.routing.Router
import play.filters.HttpFiltersComponents
import play.filters.cors.{CORSConfig, CORSFilter}
import router.Routes
import zio.Runtime
import zio.internal.Platform

object AppComponents {
    type Modules = DB with PeopleRepository with PeopleService

    def modules(dbApi: DBApi): Modules =
        new DB with PeopleRepository with PeopleService { env =>
            override val db: DB.Def = new DB.Impl {
                override val playDB: Database = dbApi.database("default")
            }

            override val peopleRepository: PeopleRepository.Def = new PeopleRepository.Impl {
                override val db: DB.Def = env.db
            }

            override val peopleService: PeopleService.Def = new PeopleService.Impl {
                override val peopleRepository: PeopleRepository.Def = env.peopleRepository
            }
        }
}

class AppComponents(ctx: Context)
    extends BuiltInComponentsFromContext(ctx)
    with DBComponents
    with HikariCPComponents
    with EvolutionsComponents
    with HttpFiltersComponents {
    applicationEvolutions

    override lazy val httpErrorHandler: HttpErrorHandler = new ErrorHandler

    override def httpFilters: Seq[EssentialFilter] = Seq(
      new CORSFilter(CORSConfig.fromConfiguration(configuration), httpErrorHandler)
    )

    lazy val runtime: Runtime[Modules] = Runtime(AppComponents.modules(dbApi), Platform.default)

    lazy val pingController: PingController = new PingController(controllerComponents)
    lazy val peopleController: PeopleController = new PeopleController(runtime, controllerComponents)

    override def router: Router =
        new Routes(
          httpErrorHandler,
          pingController,
          peopleController
        )
}
