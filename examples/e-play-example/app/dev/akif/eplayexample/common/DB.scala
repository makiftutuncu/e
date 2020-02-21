package dev.akif.eplayexample.common

import java.sql.Connection

import play.api.db.Database

trait DB {
  val db: DB.Def
}

object DB {
  trait Def {
    def withConnection[A](f: Connection => A): A
  }

  trait Impl extends Def {
    val playDB: Database

    override def withConnection[A](f: Connection => A): A = playDB.withConnection(f)
  }
}

