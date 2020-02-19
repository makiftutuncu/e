package dev.akif.ehttp4sexample.common

trait Service[F[_], M, C, U] {
  def getAll: F[List[M]]

  def get(id: Long): F[M]

  def create(create: C): F[M]

  def update(id: Long, update: U): F[M]

  def delete(id: Long): F[M]
}
