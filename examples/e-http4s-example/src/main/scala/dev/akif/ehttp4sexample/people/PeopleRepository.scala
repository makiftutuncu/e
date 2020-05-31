package dev.akif.ehttp4sexample.people
import e._
import cats.effect.IO
import dev.akif.ehttp4sexample.common.implicits._
import dev.akif.ehttp4sexample.common.{Errors, Repository}
import doobie.implicits._
import doobie.util.transactor.Transactor

class PeopleRepository(override val db: Transactor[IO]) extends Repository[IO, Person, CreatePerson, UpdatePerson] {
  override def getAll: IO[List[Person]] = {
    val operation =
      sql"""SELECT id, name, age FROM people ORDER BY name""".query[Person].to[List]

    operation.transact(db).handleErrorWith { t =>
      Errors.database
            .message("Cannot get people!")
            .cause(t.toE())
            .toIO
    }
  }

  override def get(id: Long): IO[Option[Person]] = {
    val operation =
      sql"""SELECT id, name, age FROM people WHERE id = $id""".query[Person].option

    operation.transact(db).handleErrorWith { t =>
      Errors.database
            .message("Cannot get person!")
            .data("id" -> id)
            .cause(t.toE())
            .toIO
    }
  }

  override def create(create: CreatePerson): IO[Person] = {
    val operation =
      sql"""INSERT INTO people(name, age) VALUES (${create.name}, ${create.age})""".update.withUniqueGeneratedKeys[Long]("id")

    operation.transact(db).map(create.toPerson).handleErrorWith { t =>
      Errors.database
            .message("Cannot create person!")
            .data("name" -> create.name)
            .data("age" -> create.age)
            .cause(t.toE())
            .toIO
    }
  }

  override def update(id: Long, update: UpdatePerson): IO[Person] = {
    val operation =
      for {
        personOpt  <- sql"""SELECT id, name, age FROM people WHERE id = $id""".query[Person].option
        person     <- handleOption(personOpt, Errors.notFound.message("Cannot find person to update!").data("id" -> id))
        updated     = update.updated(person)
        _          <- sql"""UPDATE people SET name = ${updated.name}, age = ${updated.age} WHERE id = $id""".update.run
      } yield {
        updated
      }

    operation.transact(db).handleErrorWith { t =>
        Errors.database
          .message("Cannot update person!")
          .data("name" -> update.name)
          .data("age" -> update.age)
          .cause(t.toE())
          .toIO
      }
  }

  override def delete(id: Long): IO[Person] = {
    val operation =
      for {
        personOpt  <- sql"""SELECT id, name, age FROM people WHERE id = $id""".query[Person].option
        person     <- handleOption(personOpt, Errors.notFound.message("Cannot find person to delete!").data("id" -> id))
        _          <- sql"""DELETE FROM people WHERE id = $id""".update.run
      } yield {
        person
      }

    operation.transact(db).handleErrorWith { t =>
      Errors.database
        .message("Cannot delete person!")
        .data("id" -> id)
        .cause(t.toE())
        .toIO
    }
  }
}
