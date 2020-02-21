package dev.akif.eplayexample

import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}

class PingController(private val cc: ControllerComponents) extends AbstractController(cc) {
  def ping: Action[AnyContent] =
    Action {
      Ok("pong")
    }
}
