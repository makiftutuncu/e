package dev.akif.eplayexample.people

import dev.akif.eplayexample.common.{Errors, Service}
import e.zio.MaybeZ

trait PeopleService {
  val peopleService: PeopleService.Def
}

object PeopleService {
  trait Def extends Service {
    val peopleRepository: PeopleRepository.Def

    def getAll: MaybeZ[List[Person]]

    def get(id: Long): MaybeZ[Person]

    def create(create: CreatePerson): MaybeZ[Person]

    def update(id: Long, update: UpdatePerson): MaybeZ[Person]

    def delete(id: Long): MaybeZ[Person]
  }

  trait Impl extends Def {
    override def getAll: MaybeZ[List[Person]] =
      peopleRepository.getAll

    override def get(id: Long): MaybeZ[Person] =
      peopleRepository.get(id).flatMap {
        case None =>
          MaybeZ.error(Errors.database.message("Cannot find person!").data("id" -> id))

        case Some(person) =>
          MaybeZ.value(person)
      }

    override def create(create: CreatePerson): MaybeZ[Person] =
      peopleRepository.create(create)

    override def update(id: Long, update: UpdatePerson): MaybeZ[Person] =
      get(id).flatMap { person =>
        peopleRepository.update(update.updated(person))
      }

    override def delete(id: Long): MaybeZ[Person] =
      get(id).flatMap { person =>
        peopleRepository.delete(person)
      }
  }
}
