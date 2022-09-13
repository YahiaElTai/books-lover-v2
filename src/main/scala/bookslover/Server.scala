package bookslover

// akka

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
// domain
import bookslover.domain.bookshelves.service.BookShelfServiceInterpreter
import bookslover.domain.bookshelves.validation.BookShelfValidationInterpreter
import bookslover.infrastructure.repositories.BookShelfRepositoryInterpreter
import bookslover.infrastructure.endpoints.BookShelfEndpoints


object Server {
  implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "my-system")

  val bookshelfRepo: BookShelfRepositoryInterpreter = BookShelfRepositoryInterpreter()
  val bookshelfValidation: BookShelfValidationInterpreter = BookShelfValidationInterpreter(bookshelfRepo)
  val bookshelfService: BookShelfServiceInterpreter = BookShelfServiceInterpreter(bookshelfRepo, bookshelfValidation)

  val endpoints: BookShelfEndpoints = BookShelfEndpoints.endpoints(bookshelfService)

  val routes: Route =
    concat(
      pathEndOrSingleSlash {
        complete("I'm the API.")
      },
      path("shelves")(endpoints.shelvesEndpoint),
      pathPrefix("shelves" / JavaUUID)(endpoints.shelfEndpoints)
    )

  def main(args: Array[String]): Unit = {
    Http().newServerAt("localhost", 8080).bind(routes)
    println("Server now online")
  }
}
