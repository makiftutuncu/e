package dev.akif.eplayexample.common

import dev.akif.eplayexample.AppComponents.Modules
import dev.akif.eplayexample.common.implicits._
import e.playjson.implicits._
import e.scala.E
import e.scala.implicits._
import e.zio.{MaybeZ, MaybeZR}
import play.api.http.ContentTypes
import play.api.libs.json.{Json, Reads, Writes}
import play.api.mvc._
import zio.Runtime

import scala.concurrent.Promise

abstract class Controller(private val runtime: Runtime[Modules], private val cc: ControllerComponents) extends AbstractController(cc) {
  protected def zioAction(f: Request[AnyContent] => MaybeZR[Modules, Result]): Action[AnyContent] =
    Action.async { request =>
      val promise: Promise[Result] = Promise()

      runtime.unsafeRunAsync(f(request)) { exit =>
        exit.fold(
          cause => {
            val e = cause.fold[E](
              Errors.unexpected,
              identity,
              t => Errors.unexpected.cause(t),
              id => Errors.unexpected.message(s"Fiber $id is interrupted!")
            )(
              (e1, _) => e1,
              (e1, _) => e1,
              (e, _)  => e
            )

            promise.success(result(e, Status(e.code)))
          },
          result => promise.success(result)
        )
      }

      promise.future
    }

  protected def zioAction[A: Writes](f: Request[AnyContent] => MaybeZR[Modules, A]): Action[AnyContent] =
    zioAction(r => f(r).map(a => result(a)))

  protected def json[A: Reads](request: Request[AnyContent]): MaybeZ[A] =
    request.body.asJson.toMaybe(
      Errors.invalidData
            .message("Request body is not Json!")
            .data("method" -> request.method)
            .data("uri" -> request.uri)
    ).flatMap { json =>
      json.validate[A].asEither.toMaybe { errors =>
        val errorData: Map[String, String] =
          errors.foldLeft(Map.empty[String, List[String]]) {
            case (errorToPathsMap, (path, errors)) =>
              errors.flatMap(_.messages).foldLeft(errorToPathsMap) {
                case (map, error) =>
                  val currentPath = path.toJsonString
                  val newPaths = map.getOrElse(error, List.empty[String]) :+ currentPath
                  map + (error -> newPaths)
              }
          }.foldLeft(Map.empty[String, String]) {
            case (map, (error, paths)) =>
              map + (error -> paths.mkString(", "))
          }

        Errors.invalidData
              .message("Request body is not a valid Json!")
              .data(errorData)
              .data("method" -> request.method)
              .data("uri"    -> request.uri)
      }
    }.toMaybeZ

  protected def result[A: Writes](a: A, status: Status = Ok): Result = status(Json.toJson(a)).as(ContentTypes.JSON)
}
