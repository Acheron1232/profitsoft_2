Features

Author Management: Create, read, update, and delete authors.

Book Management: Create, read, update, and delete books.

Filtering & Pagination: List books with filters such as title, ISBN, author, and publish date.

CSV Export: Export filtered book lists to CSV format.

JSON Import: Upload books via JSON file.

Validation: Ensures correct data for ISBN, title, and other fields.

Tech Stack

Java 21

Spring Boot 4

Spring Data JPA

Hibernate

PostgreSQL (or any relational database)

Maven/Gradle

MapStruct + Lombok for mapping

JUnit 5 & Mockito for testing

Setup and Installation
Prerequisites

Java 21

Gradle or Maven

PostgreSQL (or another relational DB)


Build and Run

Using Gradle:

./gradlew build
./gradlew bootRun


The API will run at: http://localhost:8080.

API Endpoints
Author API
Method	Endpoint	Description
GET	/api/author	Get all authors
POST	/api/author	Create a new author
PUT	/api/author/{id}	Update author by ID
DELETE	/api/author/{id}	Delete author by ID
Book API
Method	Endpoint	Description
POST	/api/book	Create a new book
GET	/api/book/{id}	Get a book by ID
PUT	/api/book/{id}	Update book by ID
DELETE	/api/book/{id}	Delete book by ID
POST	/api/book/_list	List books with filters and pagination
POST	/api/book/_export	Export filtered books as CSV
POST	/api/book/upload	Upload books from a JSON file
Testing

Run all tests with:

./gradlew test


Uses JUnit 5 and Mockito for unit and integration tests.

Parameterized tests cover multiple input scenarios for book creation and filtering.

Uses MockMvc for API endpoint testing.

Example Requests
Create Author
POST /api/author
Content-Type: application/json

{
  "firstName": "John",
  "lastName": "Doe",
  "contactInfo": "john.doe@example.com"
}

Create Book
POST /api/book
Content-Type: application/json

{
  "authorId": "c1b9f1d8-1234-4d2a-a5df-123456789abc",
  "title": "Effective Java",
  "isbn": "9780134685991"
}

Upload Books via JSON File
POST /api/book/upload
Content-Type: multipart/form-data
File: books.json


Sample books.json:

[
  {
    "authorId": "c1b9f1d8-1234-4d2a-a5df-123456789abc",
    "title": "Clean Code",
    "isbn": "9780132350884",
    "publishDate": "2025-12-10T12:00:00Z"
  }
]

Export Books to CSV
POST /api/book/_export
Content-Type: application/json

{
  "title": "Clean Code",
  "page": 0,
  "size": 10
}

License
