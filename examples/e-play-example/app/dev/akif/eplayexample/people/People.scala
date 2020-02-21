package dev.akif.eplayexample.people

import anorm.{Macro, RowParser}
import play.api.libs.json.{Format, Json}

case class Person(id: Long, name: String, age: Int)

object Person {
  implicit val personFormat: Format[Person] = Json.format[Person]

  val rowParser: RowParser[Person] = Macro.namedParser[Person]
}

case class CreatePerson(name: String, age: Int) {
  def toPerson(id: Long): Person = Person(id, name, age)
}

object CreatePerson {
  implicit val createPersonFormat: Format[CreatePerson] = Json.format[CreatePerson]
}

case class UpdatePerson(name: Option[String], age: Option[Int]) {
  def updated(person: Person): Person =
    person.copy(
      name = name.getOrElse(person.name),
      age  = age.getOrElse(person.age)
    )
}

object UpdatePerson {
  implicit val updatePersonFormat: Format[UpdatePerson] = Json.format[UpdatePerson]
}
