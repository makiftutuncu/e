package dev.akif.eplayexample

import dev.akif.eplayexample.common.Errors
import e.playjson.implicits._
import e.scala.E
import play.api.http.HttpErrorHandler
import play.api.libs.json.Json
import play.api.mvc.Results.Status
import play.api.mvc.{RequestHeader, Result}

import scala.concurrent.Future

class ErrorHandler extends HttpErrorHandler {
  override def onClientError(request: RequestHeader, statusCode: Int, message: String): Future[Result] = {
    val e = Errors.unexpected
                  .code(statusCode)
                  .message(message)
                  .data("method" -> request.method)
                  .data("uri" -> request.uri)

    Future.successful(result(request, e))
  }

  override def onServerError(request: RequestHeader, exception: Throwable): Future[Result] = {
    val e = Errors.unexpected
                  .cause(exception)
                  .data("method" -> request.method)
                  .data("uri" -> request.uri)

    Future.successful(result(request, e))
  }

  private def result(request: RequestHeader, e: E): Result = Status(e.code)(Json.toJson(e))
}
