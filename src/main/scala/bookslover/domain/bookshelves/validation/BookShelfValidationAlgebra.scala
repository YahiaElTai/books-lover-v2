package bookslover.domain.bookshelves.validation

import scala.concurrent.Future
import scala.util.Try

trait BookShelfValidationAlgebra[BookShelfId, BookShelf, BookShelfDraft] {
  def exists(id: BookShelfId): Future[Option[BookShelf]]

  def validateBookShelf(bookShelfDraft: BookShelfDraft): Try[BookShelfDraft]
}
