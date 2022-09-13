package bookslover.infrastructure.endpoints

// scala execution context

import scala.concurrent.ExecutionContextExecutor
import scala.util.{Failure, Success}
// akka
import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.server.Route
// spray json formatter
import spray.json.DefaultJsonProtocol.jsonFormat4
import spray.json.RootJsonFormat
import spray.json.DefaultJsonProtocol._
// domain
import bookslover.domain.bookshelves.model.BookShelfDraft
import bookslover.domain.bookshelves.model.{Book, BookShelf}
import bookslover.domain.bookshelves.service.BookShelfServiceInterpreter

class BookShelfEndpoints(bookshelfService: BookShelfServiceInterpreter) {
  implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "my-system")
  implicit val executionContext: ExecutionContextExecutor = system.executionContext

  import UUIDJson._

  implicit val bookFormat: RootJsonFormat[Book] = jsonFormat2(Book)
  implicit val bookshelfFormat: RootJsonFormat[BookShelf] = jsonFormat4(BookShelf)
  implicit val bookshelfDraftFormat: RootJsonFormat[BookShelfDraft] = jsonFormat2(BookShelfDraft)

  def shelvesEndpoint: Route = concat(
    get {
      val maybeBookshelves = bookshelfService.getAll
      onComplete(maybeBookshelves) {
        case Failure(exception) => complete(exception.getMessage)
        case Success(bookshelves) => complete(bookshelves)
      }
    },
    post {
      entity(as[BookShelfDraft]) { bookShelfDraft =>
        val maybeCreated = bookshelfService.create(bookShelfDraft)
        onComplete(maybeCreated) {
          case Failure(exception) => complete(exception.getMessage)
          case Success(bookshelf) => complete(bookshelf)
        }
      }
    }
  )

  def shelfEndpoints(id: java.util.UUID): Route = concat(
    get {
      val maybeBookShelf = bookshelfService.get(id)
      onComplete(maybeBookShelf) {
        case Failure(exception) => complete(exception.getMessage)
        case Success(bookshelf) => complete(bookshelf)
      }

    },
    delete {
      val maybeDeleted = bookshelfService.delete(id)
      onComplete(maybeDeleted) {
        case Failure(exception) => complete(exception.getMessage)
        case Success(bookshelf) => complete(bookshelf)
      }
    },
    put {
      entity(as[BookShelfDraft]) { bookShelfDraft =>
        val maybeUpdated = bookshelfService.update(id, bookShelfDraft)

        onComplete(maybeUpdated) {
          case Failure(exception) => complete(exception.getMessage)
          case Success(bookshelf) => complete(bookshelf)
        }
      }
    }
  )
}

object BookShelfEndpoints {
  def endpoints(bookshelfService: BookShelfServiceInterpreter): BookShelfEndpoints =
    new BookShelfEndpoints(bookshelfService)
}