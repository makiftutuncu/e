package dev.akif.ehttp4sexample.people

import cats.effect.IO
import dev.akif.ehttp4sexample.common.Repository
import doobie.util.transactor.Transactor
import doobie.implicits._

class PeopleRepository(override val db: Transactor[IO]) extends Repository[IO, Person, CreatePerson, UpdatePerson] {
  override def getAll: IO[List[Person]] =
    sql"""SELECT id, name, age FROM people ORDER BY name"""
      .query[Person]
      .to[List]
      .transact(db)

  override def get(id: Long): IO[Option[Person]] =
    sql"""SELECT id, name, age FROM people WHERE id = $id"""
      .query[Person]
      .option
      .transact(db)

  override def create(create: CreatePerson): IO[Person] =
    sql"""INSERT INTO people(name, age) VALUES (${create.name}, ${create.age})"""
      .update
      .withUniqueGeneratedKeys[Long]("id")
      .transact(db)
      .map(create.toPerson)

  override def update(id: Long, update: UpdatePerson): IO[Person] =
    (for {
      person  <- sql"""SELECT id, name, age FROM people WHERE id = $id""".query[Person].unique
      updated  = update.updated(person)
      _       <- sql"""UPDATE people SET name = ${updated.name}, age = ${updated.age} WHERE id = $id""".update.run
    } yield {
      updated
    }).transact(db)

  override def delete(id: Long): IO[Person] =
    (for {
      person  <- sql"""SELECT id, name, age FROM people WHERE id = $id""".query[Person].unique
      _       <- sql"""DELETE FROM people WHERE id = $id""".update.run
    } yield {
      person
    }).transact(db)
}
