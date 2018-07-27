package com.nigel.bookgame.rest.domain;

/**
 * Container class representing a book together with a retrieval outcome.
 * 
 * @author nigel
 */
public class BookContainer {

    private Book book;
    private ERetrievalOutcome retrievalOutcome;

    public Book getBook() {
        return this.book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public ERetrievalOutcome getRetrievalOutcome() {
        return this.retrievalOutcome;
    }

    public void setRetrievalOutcome(ERetrievalOutcome retrievalOutcome) {
        this.retrievalOutcome = retrievalOutcome;
    }
}