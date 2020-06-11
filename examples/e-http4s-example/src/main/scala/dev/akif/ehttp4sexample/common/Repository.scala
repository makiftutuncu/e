package dev.akif.ehttp4sexample.common

import cats.free.Free
import doobie.free.connection.ConnectionOp
import doobie.util.transactor.Transactor
import e.scala.E

trait Repository[F[_], M, C, U] {
  val db: Transactor[F]

  def getAll: F[List[M]]

  def get(id: Long): F[Option[M]]

  def create(create: C): F[M]

  def update(id: Long, update: U): F[M]

  def delete(id: Long): F[M]

  protected def handleOption[A](option: Option[A], ifNone: => E): Free[ConnectionOp, A] =
    Free.liftF {
      option match {
        case None    => ConnectionOp.RaiseError[A](ifNone.toException)
        case Some(a) => ConnectionOp.Raw[A](_ => a)
      }
    }
}
