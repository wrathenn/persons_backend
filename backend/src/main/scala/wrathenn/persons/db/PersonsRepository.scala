package wrathenn.persons.db

import wrathenn.persons.db.entities.PersonEntity
import wrathenn.persons.model.{ApiException, BadArgumentException, NotFoundException, Person, PersonCreate}
import cats.effect.kernel.Async
import doobie.implicits.toSqlInterpolator
import doobie.util.transactor.Transactor
import doobie.*
import doobie.implicits.*
import cats.implicits.*
import doobie.enumerated.SqlState

trait PersonsRepository[F[_] : Async]:
  def selectAll(): F[Either[ApiException, List[Person]]]
  def selectById(id: Long): F[Either[ApiException, Person]]
  def insert(person: PersonCreate): F[Either[ApiException, Long]]
  def delete(id: Long): F[Either[ApiException, Long]]
  def update(id: Long, person: PersonCreate): F[Either[ApiException, Person]]


class PersonsRepositoryImpl[F[_] : Async](
  val xa: Transactor[F],
) extends PersonsRepository[F] {
  override def selectById(id: Long): F[Either[ApiException, Person]] =
    sql"""
      select id, first_name, last_name, description
      from persons.persons
      where id = ${id}
    """
    .query[PersonEntity]
    .option
    .transact(xa)
    .map {
      case Some(personEntity) => personEntity.toModel.asRight
      case None => NotFoundException(s"Person with id = $id not found").asLeft
    }

  override def insert(person: PersonCreate): F[Either[ApiException, Long]] =
    sql"""
      insert into persons.persons(first_name, last_name, description)
      values (${person.firstName}, ${person.lastName}, ${person.description})
    """
      .update
      .withUniqueGeneratedKeys[Long]("id")
      .attemptSomeSqlState {
        case state => BadArgumentException(s"Could not insert, error $state")
        case _ => ApiException(s"Error", None)
      }
      .transact(xa)

  override def selectAll(): F[Either[ApiException, List[Person]]] = {
    for {
      listResult: Either[ApiException, List[PersonEntity]] <- sql"""
            select id, first_name, last_name, description
            from persons.persons
          """
        .query[PersonEntity]
        .to[List]
        .attemptSomeSqlState {
          case state: SqlState => BadArgumentException(s"Could not select, error $state")
          case _ => ApiException(s"Error", None)
        }
        .transact(xa)
      converted <- (listResult match {
        case Right(persons) => Right(persons.map { _.toModel })
        case Left(e) => Left(e)
      }).pure[F]
    } yield converted
  }

  override def delete(id: Long): F[Either[ApiException, Long]] = {
    sql"""
      delete from persons.persons
      where id = $id
    """
      .update
      .run
      .transact(xa)
      .map {
        case 0 => NotFoundException(s"Person with id=$id not found").asLeft
        case n => n.toLong.asRight
      }
  }

  override def update(id: Long, person: PersonCreate): F[Either[ApiException, Person]] = {
    sql"""
      update persons.persons
      set first_name = ${person.firstName}
          last_name = ${person.lastName}
          description = ${person.description}
      where id = $id
    """
      .update
      .withGeneratedKeys[Person]("id", "first_name", "last_name", "description")
      .attemptSomeSqlState {
        case state => BadArgumentException(s"Could not update, error $state")
        case _ => ApiException(s"Error", None)
      }
      .transact(xa)
      .compile
      .lastOrError
  }
}

