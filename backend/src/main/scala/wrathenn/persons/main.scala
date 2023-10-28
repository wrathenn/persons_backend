package wrathenn.persons

import cats.effect.{Async, IO, IOApp, Resource}
import com.comcast.ip4s.{ipv4, port}
import doobie.Transactor
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import org.http4s.dsl.impl.QueryParamDecoderMatcher
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.Router
import wrathenn.persons.api.PersonsRoute
import wrathenn.persons.db.{PersonsRepository, PersonsRepositoryImpl}
import wrathenn.persons.services.{PersonsService, PersonsServiceImpl}

object TestParamMatcher extends QueryParamDecoderMatcher[String]("test-arg")

object Main extends IOApp.Simple {
  private def runServer[F[_]: Async]: F[Nothing] = {
    val transactor = Transactor.fromDriverManager[F](
      driver = "org.postgresql.Driver",
      url = "jdbc:postgresql://localhost:5727/persons",
      user = "master",
      password = "master",
      logHandler = None,
    )
    val personsRepo: PersonsRepository[F] = PersonsRepositoryImpl[F](transactor)
    val personsService: PersonsService[F] = PersonsServiceImpl[F](personsRepo)

    for {
      _ <- Resource.eval(Async[F].blocking(println("Starting :)")))
      api = Router[F](
        "" -> PersonsRoute.routes[F]("/persons")(personsService)
      ).orNotFound

      _ <- EmberServerBuilder.default[F]
        .withHost(ipv4"0.0.0.0")
        .withPort(port"8080")
        .withHttpApp(api)
        .build
    } yield ()
  }.useForever

  val run: IO[Nothing] = runServer[IO]
}
