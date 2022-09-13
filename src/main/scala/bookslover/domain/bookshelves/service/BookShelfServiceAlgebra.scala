package bookslover.domain.bookshelves.service

import scala.concurrent.Future

trait BookShelfServiceAlgebra[BookShelf, BookShelfId, BookShelfDraft] {
  def create(bookshelfDraft: BookShelfDraft): Future[BookShelf]

  def get(id: BookShelfId): Future[Option[BookShelf]]

  def getAll: Future[List[BookShelf]]

  def delete(id: BookShelfId): Future[Option[BookShelf]]

  def update(id: BookShelfId, bookShelfDraft: BookShelfDraft): Future[Option[BookShelf]]
}
