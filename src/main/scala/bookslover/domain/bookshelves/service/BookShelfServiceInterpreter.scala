package bookslover.domain.bookshelves.service

import bookslover.domain.bookshelves.model.common._
import bookslover.domain.bookshelves.model.{Book, BookShelf, BookShelfDraft}
import bookslover.domain.bookshelves.repository.BookShelfRepositoryAlgebra
import bookslover.domain.bookshelves.validation.BookShelfValidationAlgebra

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

class BookShelfServiceInterpreter(repo: BookShelfRepositoryAlgebra[BookShelf, BookShelfId, BookShelfDraft], validation: BookShelfValidationAlgebra[BookShelfId, BookShelf, BookShelfDraft])
  extends BookShelfServiceAlgebra[BookShelf, BookShelfId, BookShelfDraft] {
  def create(bookshelfDraft: BookShelfDraft): Future[BookShelf] = {
    validation.validateBookShelf(bookshelfDraft) match {
      case Failure(exception) => Future.failed(exception)
      case Success(validatedBookedShelf) => repo.create(validatedBookedShelf)
    }
  }

  def get(id: BookShelfId): Future[Option[BookShelf]] = validation.exists(id)

  def getAll: Future[List[BookShelf]] = repo.queryAll

  def delete(id: BookShelfId): Future[Option[BookShelf]] = for {
    _ <- validation.exists(id)
    deleted <- repo.delete(id)
  } yield deleted

  override def update(id: BookShelfId, bookShelfDraft: BookShelfDraft): Future[Option[BookShelf]] = for {
    // updating could probably be modelled better with update actions (case classes & pattern matching)
    maybeBookSelf <- validation.exists(id)
    updatedBookShelf <- {
      maybeBookSelf match {
        case Some(bookshelf) =>
          val updated = bookshelf.copy(name = bookShelfDraft.name, books = bookShelfDraft.books)
          repo.update(updated)
        case None => Future.failed(new Exception(s"Bookshelf with id $id could not be found."))
      }
    }
  } yield updatedBookShelf
}

object BookShelfServiceInterpreter {
  def apply(repo: BookShelfRepositoryAlgebra[BookShelf, BookShelfId, BookShelfDraft], validation: BookShelfValidationAlgebra[BookShelfId, BookShelf, BookShelfDraft]) =
    new BookShelfServiceInterpreter(repo, validation)
}