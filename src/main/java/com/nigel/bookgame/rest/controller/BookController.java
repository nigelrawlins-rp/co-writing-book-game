package com.nigel.bookgame.rest.controller;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nigel.bookgame.rest.domain.Book;
import com.nigel.bookgame.rest.domain.BookContainer;
import com.nigel.bookgame.rest.domain.EUpdateOutcome;
import com.nigel.bookgame.rest.repository.BookRepository;
import com.nigel.bookgame.rest.resource.BookResource;
import com.nigel.bookgame.rest.resource.BookResourceAssembler;

/**
 * Book controller class providing endpoints for:
 * 
 * - Finding all books: not part of requirement, can be called by anyone to see the current state of all books (full if complete, last line only if not).
 * - Finding a specific book by ID.
 * - Creating a book: needed to set up books in the first place, can be called by anyone.
 * - Updating a book.
 * 
 * @author nigel
 */
@RestController
@ExposesResourceFor(Book.class)
@RequestMapping(value = "/book", produces = "application/json")
public class BookController {

	@Autowired
	private BookRepository bookRepository;

	@Autowired
	private BookResourceAssembler bookResourceAssembler;

	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<Collection<BookResource>> findAllBooks() {
		List<Book> books = this.bookRepository.findAll();
		return new ResponseEntity<>(this.bookResourceAssembler.toResourceCollection(books), HttpStatus.OK);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public ResponseEntity<BookResource> findBookById(@PathVariable Long id, @RequestParam String playerName) {

	    BookContainer bookContainer = this.bookRepository.findById(id, playerName);

	    switch (bookContainer.getRetrievalOutcome()) {
	    case NOT_FOUND:
	        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	    case LOCKED_BY_ANOTHER_PLAYER:
	        return new ResponseEntity<>(HttpStatus.LOCKED);
	    default:
	        return new ResponseEntity<>(this.bookResourceAssembler.toResource(bookContainer.getBook()), HttpStatus.OK);
	    }
	}

    @RequestMapping(method = RequestMethod.POST, consumes = "application/json")
    public ResponseEntity<BookResource> createBook(@RequestBody BookCreateRequest bookCreateRequest) {
        Book createdBook = this.bookRepository.create(bookCreateRequest.toBook());
        return new ResponseEntity<>(this.bookResourceAssembler.toResource(createdBook), HttpStatus.CREATED);
    }

	@RequestMapping(value = "/{id}", method = RequestMethod.PUT, consumes = "application/json")
	public ResponseEntity<BookResource> updateBook(@PathVariable Long id, @RequestBody BookUpdateRequest bookUpdateRequest) {

	    String playerName = bookUpdateRequest.getPlayerName();

	    if (null == playerName) {
	        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	    }

	    BookContainer bookContainer = this.bookRepository.findById(id, playerName);

        switch (bookContainer.getRetrievalOutcome()) {
        case NOT_FOUND:
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        case LOCKED_BY_ANOTHER_PLAYER:
            return new ResponseEntity<>(HttpStatus.LOCKED);
        default:
            final Book book = bookContainer.getBook();
            book.addLineToLineDetailMap(bookUpdateRequest.getNewLine(), playerName);
            EUpdateOutcome updateOutcome = this.bookRepository.update(id, book, playerName, true); // Passes true as we want to unlock the book so it is available again.
            switch (updateOutcome) {
            case NULL_BOOK_SUPPLIED:
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            case REQUESTING_PLAYER_DOES_NOT_HAVE_LOCK:
                return new ResponseEntity<>(HttpStatus.LOCKED);
            case NOT_FOUND:
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            default:
                return new ResponseEntity<>(this.bookResourceAssembler.toResource(bookContainer.getBook()), HttpStatus.OK);
            }
        }
	}
}