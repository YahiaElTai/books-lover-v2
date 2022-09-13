package bookslover.domain.bookshelves.repository

import scala.concurrent.Future

trait BookShelfRepositoryAlgebra[BookShelf, BookShelfId, BookShelfDraft] {
  def queryAll: Future[List[BookShelf]]

  def query(id: BookShelfId): Future[Option[BookShelf]]

  def create(a: BookShelfDraft): Future[BookShelf]

  def delete(id: BookShelfId): Future[Option[BookShelf]]

  def update(bookshelf: BookShelf): Future[Option[BookShelf]]
}