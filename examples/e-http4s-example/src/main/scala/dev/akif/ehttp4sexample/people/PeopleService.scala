package dev.akif.ehttp4sexample.people

import cats.effect.IO
import dev.akif.ehttp4sexample.common.{Errors, Service}
import dev.akif.ehttp4sexample.common.implicits._
import e.scala._

class PeopleService(val peopleRepository: PeopleRepository) extends Service[IO, Person, CreatePerson, UpdatePerson] {
    override def getAll: IO[List[Person]] = peopleRepository.getAll

    override def get(id: Long): IO[Person] =
        for {
            personOpt <- peopleRepository.get(id)
            person <- personOpt.toEOr(Errors.notFound.message("Person is not found!")).toIO
        } yield {
            person
        }

    override def create(create: CreatePerson): IO[Person] = peopleRepository.create(create)

    override def update(id: Long, update: UpdatePerson): IO[Person] = peopleRepository.update(id, update)

    override def delete(id: Long): IO[Person] = peopleRepository.delete(id)
}
