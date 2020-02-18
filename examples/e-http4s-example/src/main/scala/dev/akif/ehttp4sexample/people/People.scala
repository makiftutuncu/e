package dev.akif.ehttp4sexample.people

import io.circe.{Decoder, Encoder}

case class Person(id: Long, name: String, age: Int)

object Person {
  implicit val personDecoder: Decoder[Person] = Decoder.forProduct3("id", "name", "age")(Person.apply)
  implicit val personEncoder: Encoder[Person] = Encoder.forProduct3("id", "name", "age")(p => (p.id, p.name, p.age))
}

case class CreatePerson(name: String, age: Int) {
  def toPerson(id: Long): Person = Person(id, name, age)
}

object CreatePerson {
  implicit val createPersonDecoder: Decoder[CreatePerson] = Decoder.forProduct2("name", "age")(CreatePerson.apply)
  implicit val createPersonEncoder: Encoder[CreatePerson] = Encoder.forProduct2("name", "age")(c => (c.name, c.age))
}

case class UpdatePerson(name: Option[String], age: Option[Int]) {
  def updated(person: Person): Person =
    person.copy(
      name = name.getOrElse(person.name),
      age  = age.getOrElse(person.age)
    )
}

object UpdatePerson {
  implicit val updatePersonDecoder: Decoder[UpdatePerson] = Decoder.forProduct2("name", "age")(UpdatePerson.apply)
  implicit val updatePersonEncoder: Encoder[UpdatePerson] = Encoder.forProduct2("name", "age")(u => (u.name, u.age))
}
