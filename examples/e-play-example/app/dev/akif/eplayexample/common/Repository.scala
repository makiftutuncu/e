package dev.akif.eplayexample.common

import java.sql.Connection

import dev.akif.eplayexample.common.implicits._
import e.scala.Maybe
import e.zio.MaybeZ

trait Repository {
  val db: DB.Def

  protected def run[A](f: Connection => A): MaybeZ[A] =
    Maybe.catching(t => Errors.database.message("Database error!").cause(t)) {
      db.withConnection(f)
    }.toMaybeZ
}
