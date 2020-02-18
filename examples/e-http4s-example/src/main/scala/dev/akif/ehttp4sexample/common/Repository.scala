package dev.akif.ehttp4sexample.common

import doobie.util.transactor.Transactor

trait Repository[F[_], M, C, U] {
  val db: Transactor[F]

  def getAll: F[List[M]]

  def get(id: Long): F[Option[M]]

  def create(create: C): F[M]

  def update(id: Long, update: U): F[M]

  def delete(id: Long): F[M]
}
