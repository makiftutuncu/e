package dev.akif.eplayexample.people

import anorm.{SQL, SqlParser}
import dev.akif.eplayexample.common.Repository
import e.zio.MaybeZ

trait PeopleRepository {
  val peopleRepository: PeopleRepository.Def
}

object PeopleRepository {
  trait Def extends Repository {
    def getAll: MaybeZ[List[Person]]

    def get(id: Long): MaybeZ[Option[Person]]

    def create(create: CreatePerson): MaybeZ[Person]

    def update(person: Person): MaybeZ[Person]

    def delete(person: Person): MaybeZ[Person]
  }

  trait Impl extends Def {
    override def getAll: MaybeZ[List[Person]] =
      run[List[Person]] { implicit connection =>
        val sql =
          SQL(
            """
              |SELECT id, name, age
              |FROM people
              |ORDER BY name
            """.stripMargin
          )

        sql.executeQuery().as(Person.rowParser.*)
      }

    override def get(id: Long): MaybeZ[Option[Person]] =
      run[Option[Person]] { implicit connection =>
        val sql =
          SQL(
            """
              |SELECT id, name, age
              |FROM people
              |WHERE id = {id}
            """.stripMargin
          ).on(
            "id" -> id
          )

        sql.executeQuery().as(Person.rowParser.singleOpt)
      }

    override def create(create: CreatePerson): MaybeZ[Person] =
      run[Long] { implicit connection =>
        val sql =
          SQL(
            """
              |INSERT INTO people(name, age)
              |VALUES({name}, {age})
            """.stripMargin
          ).on(
            "name" -> create.name,
            "age"  -> create.age
          )

        sql.executeInsert[Long](SqlParser.scalar[Long].single)
      }.map { id =>
        create.toPerson(id)
      }

    override def update(person: Person): MaybeZ[Person] =
      run { implicit connection =>
        val sql =
          SQL(
            """
              |UPDATE people
              |SET name = {name}, age = {age}
              |WHERE id = {id}
            """.stripMargin
          ).on(
            "id" -> person.id,
            "name" -> person.name,
            "age" -> person.age
          )

        sql.executeUpdate()
      }.as {
        person
      }

    override def delete(person: Person): MaybeZ[Person] =
      run { implicit connection =>
        val sql =
          SQL(
            """
              |DELETE FROM people
              |WHERE id = {id}
            """.stripMargin
          ).on(
            "id" -> person.id
          )

        sql.executeUpdate()
      }.as {
        person
      }
  }
}
