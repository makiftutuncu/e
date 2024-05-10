package dev.akif.eplayexample.common

import java.sql.Connection

import e.ezio._
import e.scala._

trait Repository {
    val db: DB.Def

    protected def run[A](f: Connection => A): EIO[A] =
        db.withConnection(f)
            .catching(t => Errors.database.message("Database error!").cause(t.toE()))
            .toEIO
}
