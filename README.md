# co-writing-book-game
Co-writing book game exercise.

To run on localhost:

Clone the GIT repository and run:

mvn spring-boot:run

Then via Postman or curl, use the following endpoints:

To get all books (to view only, does not lock any):
GET
http://localhost:8080/book
Content-Type: application/json

To add a new book:
POST
http://localhost:8080/book
Content-Type: application/json
Body of the format:
{
    "name": "A Book"
}

To get (and lock) a book:
GET
http://localhost:8080/book/{id}?playerName={playerName}
Content-Type: application/json

To update a book (will work provided nobody else has the lock):
PUT
http://localhost:8080/book/{id}
Content-Type: application/json
Body of the format:
{
    "newLine": "A line.",
    "playerName": "Nigel"
}

To view the leaderboard:
GET
http://localhost:8080/leaderboard
Content-Type: application/json
