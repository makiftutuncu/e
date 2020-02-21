package dev.akif.eplayexample.people

import dev.akif.eplayexample.AppComponents.Modules
import dev.akif.eplayexample.common.Controller
import play.api.mvc._
import zio.{Runtime, ZIO}

class PeopleController(private val runtime: Runtime[Modules], private val cc: ControllerComponents) extends Controller(runtime, cc) {
  def getAll: Action[AnyContent] =
    zioAction { _ =>
      for {
        service <- ZIO.environment[PeopleService]
        people  <- service.peopleService.getAll
      } yield {
        people
      }
    }

  def get(id: Long): Action[AnyContent] =
    zioAction { _ =>
      for {
        service <- ZIO.environment[PeopleService]
        person  <- service.peopleService.get(id)
      } yield {
        person
      }
    }

  def create: Action[AnyContent] =
    zioAction { request: Request[AnyContent] =>
      for {
        create <- json[CreatePerson](request)
        person <- ZIO.accessM[PeopleService](_.peopleService.create(create))
      } yield {
        result(person, Created)
      }
    }

  def update(id: Long): Action[AnyContent] =
    zioAction { request: Request[AnyContent] =>
      for {
        update  <- json[UpdatePerson](request)
        service <- ZIO.environment[PeopleService]
        person  <- service.peopleService.update(id, update)
      } yield {
        person
      }
    }

  def delete(id: Long): Action[AnyContent] =
    zioAction { _ =>
      for {
        service <- ZIO.environment[PeopleService]
        person  <- service.peopleService.delete(id)
      } yield {
        person
      }
    }
}
