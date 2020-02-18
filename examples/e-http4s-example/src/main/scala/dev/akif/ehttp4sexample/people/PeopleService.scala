package dev.akif.ehttp4sexample.people

import cats.effect.IO
import dev.akif.ehttp4sexample.common.Service
import e.scala.{E, Maybe}
import e.scala.implicits._

class PeopleService(val peopleRepository: PeopleRepository) extends Service[IO, Person, CreatePerson, UpdatePerson] {
  override def getAll: IO[List[Person]] = peopleRepository.getAll

  override def get(id: Long): IO[Person] =
    for {
      personOpt <- peopleRepository.get(id)
      person    <- personOpt.toMaybe(E("db", "Person not found", 404)).toIO
    } yield {
      person
    }

  override def create(create: CreatePerson): IO[Person] = peopleRepository.create(create)

  override def update(id: Long, update: UpdatePerson): IO[Person] = peopleRepository.update(id, update)

  override def delete(id: Long): IO[Person] = peopleRepository.delete(id)

  implicit class MaybeIOExtensions[A](private val maybe: Maybe[A]) {
    def toIO: IO[A] =
      maybe.fold[IO[A]](
        e => IO.raiseError(e.toException()),
        a => IO.pure(a)
      )
  }
}
