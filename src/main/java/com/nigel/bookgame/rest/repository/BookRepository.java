package com.nigel.bookgame.rest.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.nigel.bookgame.rest.domain.Book;
import com.nigel.bookgame.rest.domain.BookContainer;
import com.nigel.bookgame.rest.domain.ERetrievalOutcome;
import com.nigel.bookgame.rest.domain.EUpdateOutcome;

/**
 * Repository layer for books which holds the state of all books in memory.
 * 
 * @author nigel
 */
@Repository
public class BookRepository {
    
    @Autowired
    private IdGenerator idGenerator;
    
    private List<Book> books = Collections.synchronizedList(new ArrayList<Book>());
    
    public Book create(final Book book) {
        
        this.books.add(book);
        book.setId(this.idGenerator.getNextId());
        
        return book;
    }
    
    public EUpdateOutcome update(final Long id, final Book book, final String playerName, final boolean unlock) {
        
        if (null == book) {
            return EUpdateOutcome.NULL_BOOK_SUPPLIED;
        }
        
        if (!playerName.equals(book.getLockedBy())) {
            return EUpdateOutcome.REQUESTING_PLAYER_DOES_NOT_HAVE_LOCK;
        }
        
        for (int index = 0 ; index < getCount() ; index++) {
            if (this.books.get(index).getId().equals(book.getId())) {
                if (unlock) {
                    book.unlock();
                }
                this.books.set(index, book);
                return EUpdateOutcome.SUCCESS;
            }
        }
        
        return EUpdateOutcome.NOT_FOUND;
    }
    
    public List<Book> findAll() {
        return this.books;
    }
    
    public BookContainer findById(final Long id, final String playerName) {
        
        final BookContainer bookContainer = new BookContainer();
        
        final Optional<Book> bookOptional = this.books.stream().filter(book -> book.getId().equals(id)).findFirst();
        
        if (bookOptional.isPresent()) {
            attemptToLockBook(id, playerName, bookContainer, bookOptional);
        }
        else {
            bookContainer.setBook(null);
            bookContainer.setRetrievalOutcome(ERetrievalOutcome.NOT_FOUND);
        }
        
        return bookContainer;
    }
    
    private void attemptToLockBook(final Long id, final String playerName, final BookContainer bookContainer, final Optional<Book> bookOptional) {
        
        final Book book = bookOptional.get();
        
        final String lockedBy = book.getLockedBy();
        
        if (null == lockedBy) {
            attemptToLockBookWhereNotCurrentlyLocked(id, playerName, bookContainer, book);
        }
        else {
            attemptToLockBookWhereCurrentlyLocked(playerName, bookContainer, book);
        }
    }
    
    private void attemptToLockBookWhereNotCurrentlyLocked(final Long id, final String playerName, final BookContainer bookContainer, Book book) {
        
        if (book.lock(playerName)) {
            update(id, book, playerName, false); // Can only return SUCCESS when called from here. Passes false to ensure we don't unlock the lock we just secured.
            bookContainer.setBook(book);
            bookContainer.setRetrievalOutcome(ERetrievalOutcome.SUCCESS);
        }
        else { // Unlikely to happen (and hard to test) - the lock would have to occurred between the check in the calling method and now.
            bookContainer.setBook(null);
            bookContainer.setRetrievalOutcome(ERetrievalOutcome.LOCKED_BY_ANOTHER_PLAYER);
        }
    }
    
    // The only chance of success here is if a player already has the lock and is trying unnecessarily to lock the book again.
    private void attemptToLockBookWhereCurrentlyLocked(final String playerName, final BookContainer bookContainer, final Book book) {
        
        if (playerName.equals(book.getLockedBy())) {
            bookContainer.setBook(book);
            bookContainer.setRetrievalOutcome(ERetrievalOutcome.SUCCESS);
        }
        else {
            bookContainer.setBook(null);
            bookContainer.setRetrievalOutcome(ERetrievalOutcome.LOCKED_BY_ANOTHER_PLAYER);
        }
    }
    
    public int getCount() {
        return this.books.size();
    }
    
    public void clear() {
        this.books.clear();
    }
}