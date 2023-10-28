package wrathenn.persons.api

import cats.effect.kernel.Async
import org.http4s.HttpRoutes
import org.http4s.dsl.Http4sDsl
import wrathenn.persons.services.PersonsService

object PersonsRoute {
  def routes
      [F[_]: Async]
      (location: String)
      (personsService: PersonsService[F])
  : HttpRoutes[F] = {
    val dsl = Http4sDsl[F]
    import dsl.*

    val rootLocation = Root / location
    HttpRoutes.of[F] {
      case GET -> rootLocation => ???
      case GET -> rootLocation => ???
      case POST -> rootLocation => ???
      case PATCH -> rootLocation => ???
      case DELETE -> rootLocation => ???
    }
  }
}
