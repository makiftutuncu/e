package dev.akif.eplayexample.people

import dev.akif.eplayexample.common.{Errors, Service}
import e.zio._
import e._

trait PeopleService {
  val peopleService: PeopleService.Def
}

object PeopleService {
  trait Def extends Service {
    val peopleRepository: PeopleRepository.Def

    def getAll: EIO[List[Person]]

    def get(id: Long): EIO[Person]

    def create(create: CreatePerson): EIO[Person]

    def update(id: Long, update: UpdatePerson): EIO[Person]

    def delete(id: Long): EIO[Person]
  }

  trait Impl extends Def {
    override def getAll: EIO[List[Person]] =
      peopleRepository.getAll

    override def get(id: Long): EIO[Person] =
      peopleRepository.get(id).flatMap(_.orE((Errors.database.message("Cannot find person!").data("id" -> id))).toEIO)

    override def create(create: CreatePerson): EIO[Person] =
      peopleRepository.create(create)

    override def update(id: Long, update: UpdatePerson): EIO[Person] =
      get(id).flatMap { person =>
        peopleRepository.update(update.updated(person))
      }

    override def delete(id: Long): EIO[Person] =
      get(id).flatMap { person =>
        peopleRepository.delete(person)
      }
  }
}
