package bookslover.domain.bookshelves.validation

import bookslover.domain.bookshelves.model.common._
import bookslover.domain.bookshelves.model.{Book, BookShelf, BookShelfDraft}
import bookslover.domain.bookshelves.repository.BookShelfRepositoryAlgebra

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success, Try}

class BookShelfValidationInterpreter(repo: BookShelfRepositoryAlgebra[BookShelf, BookShelfId, BookShelfDraft])
  extends BookShelfValidationAlgebra[BookShelfId, BookShelf, BookShelfDraft] {

  def exists(id: BookShelfId): Future[Option[BookShelf]] = repo.query(id).flatMap {
    case Some(bookshelf) => Future.successful(Option(bookshelf))
    case None => Future.failed(new NoSuchElementException(s"Bookshelf with id $id could not be found"))
  }

  def validateBookShelf(bookshelfDraft: BookShelfDraft): Try[BookShelfDraft] = {
    if (bookshelfDraft.name.isEmpty) Failure(new Exception("You must provide a name."))
    else if (bookshelfDraft.books.isEmpty) Failure(new Exception("You must provide at least one book to create a bookshelf."))
    else Success(bookshelfDraft)
  }
}


object BookShelfValidationInterpreter {
  def apply(repo: BookShelfRepositoryAlgebra[BookShelf, BookShelfId, BookShelfDraft]) =
    new BookShelfValidationInterpreter(repo)
}
