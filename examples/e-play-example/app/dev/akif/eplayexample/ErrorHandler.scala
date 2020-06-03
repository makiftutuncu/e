package dev.akif.eplayexample

import dev.akif.eplayexample.common.Errors
import e.scala._
import e.playjson._
import play.api.http.HttpErrorHandler
import play.api.libs.json.Json
import play.api.mvc.Results.Status
import play.api.mvc.{RequestHeader, Result}
import play.api.http

import scala.concurrent.Future

class ErrorHandler extends HttpErrorHandler {
  override def onClientError(request: RequestHeader, statusCode: Int, message: String): Future[Result] = {
    val e = Errors.unexpected
                  .code(statusCode)
                  .message(message)
                  .data("method" -> request.method)
                  .data("uri" -> request.uri)

    Future.successful(result(e))
  }

  override def onServerError(request: RequestHeader, exception: Throwable): Future[Result] = {
    val e = Errors.unexpected
                  .cause(exception.toE())
                  .data("method" -> request.method)
                  .data("uri" -> request.uri)

    Future.successful(result(e))
  }

  private def result(e: E): Result = Status(e.code.getOrElse(http.Status.INTERNAL_SERVER_ERROR))(Json.toJson(e))
}
