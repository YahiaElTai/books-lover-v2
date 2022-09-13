package bookslover.infrastructure.repositories

import bookslover.domain.bookshelves.model.{BookShelf, BookShelfDraft}
import bookslover.domain.bookshelves.model.common._
import bookslover.domain.bookshelves.repository.BookShelfRepositoryAlgebra

import scala.collection.concurrent.TrieMap
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class BookShelfRepositoryInterpreter extends BookShelfRepositoryAlgebra[BookShelf, BookShelfId, BookShelfDraft] {
  private val repo = TrieMap.empty[BookShelfId, BookShelf]

  def query(id: BookShelfId): Future[Option[BookShelf]] = Future { repo.get(id) }

  def queryAll: Future[List[BookShelf]] = Future { repo.values.toList }

  def create(a: BookShelfDraft): Future[BookShelf] = Future {
    val id = java.util.UUID.randomUUID
    val bookshelf = BookShelf(id, a.name, a.books)

    repo += (id -> bookshelf)

    bookshelf
  }

  def delete(id: BookShelfId): Future[Option[BookShelf]] = Future { repo.remove(id)}


  def update(bookshelf: BookShelf): Future[Option[BookShelf]] = Future {
    repo.update(bookshelf.id, bookshelf)
    repo.get(bookshelf.id)
  }
}

object BookShelfRepositoryInterpreter {
  def apply() = new BookShelfRepositoryInterpreter()
}