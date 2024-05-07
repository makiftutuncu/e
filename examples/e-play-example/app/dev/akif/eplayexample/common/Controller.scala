package dev.akif.eplayexample.common

import dev.akif.eplayexample.AppComponents.Modules
import e.scala._
import e.playjson._
import e.ezio._
import play.api.http.ContentTypes
import play.api.libs.json.{Json, Reads, Writes}
import play.api.mvc._
import zio.Runtime
import play.api.http

import scala.concurrent.Promise

abstract class Controller(private val runtime: Runtime[Modules], private val cc: ControllerComponents)
    extends AbstractController(cc) {
    protected def zioAction(f: Request[AnyContent] => REIO[Modules, Result]): Action[AnyContent] =
        Action.async { request =>
            val promise: Promise[Result] = Promise()

            runtime.unsafeRunAsync(f(request)) { exit =>
                exit.fold(
                  cause => {
                      val e = cause.fold[E](
                        Errors.unexpected,
                        identity,
                        t => Errors.unexpected.cause(t.toE()),
                        id => Errors.unexpected.message(s"Fiber $id is interrupted!")
                      )(
                        (e1, _) => e1,
                        (e1, _) => e1,
                        (e, _) => e
                      )

                      promise.success(result(e, Status(e.code.getOrElse(http.Status.INTERNAL_SERVER_ERROR))))
                  },
                  result => promise.success(result)
                )
            }

            promise.future
        }

    protected def zioAction[A: Writes](f: Request[AnyContent] => REIO[Modules, A]): Action[AnyContent] =
        zioAction(r => f(r).map(a => result(a)))

    protected def json[A: Reads](request: Request[AnyContent]): EIO[A] =
        request.body.asJson
            .toEOr(
              Errors.invalidData
                  .message("Request body is not Json!")
                  .data("method" -> request.method)
                  .data("uri" -> request.uri)
            )
            .flatMap(decode[A])
            .toEIO

    protected def result[A: Writes](a: A, status: Status = Ok): Result = status(Json.toJson(a)).as(ContentTypes.JSON)
}
