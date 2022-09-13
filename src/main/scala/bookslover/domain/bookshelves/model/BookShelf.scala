package bookslover.domain.bookshelves.model

object common {
  type BookShelfId = java.util.UUID
  type BookId = java.util.UUID
}

import common._

final case class Book(id: BookId, title: String)

final case class BookShelf(id: BookShelfId, name: String, books: List[Book], isArchived: Boolean = false)
final case class BookShelfDraft(name: String, books: List[Book])