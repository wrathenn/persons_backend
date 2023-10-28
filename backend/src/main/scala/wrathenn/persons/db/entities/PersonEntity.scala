package wrathenn.persons.db.entities

import wrathenn.persons.model.Person

case class PersonEntity(
  id: Long,
  firstName: String,
  lastName: String,
  description: Option[String],
) {
  lazy val toModel: Person = Person(
    id = id,
    firstName = firstName,
    lastName = lastName,
    description = description
  )
}

case class PersonInsertableEntity(
  firstName: String,
  lastName: String,
  description: Option[String],
)
