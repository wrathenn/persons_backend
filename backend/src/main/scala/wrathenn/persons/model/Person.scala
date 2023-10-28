package wrathenn.persons.model

case class Person(
  id: Long,
  firstName: String,
  lastName: String,
  description: Option[String],
)

case class PersonCreate(
  firstName: String,
  lastName: String,
  description: Option[String],
)
