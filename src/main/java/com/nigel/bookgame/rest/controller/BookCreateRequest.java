package com.nigel.bookgame.rest.controller;

import com.nigel.bookgame.rest.domain.Book;

/**
 * Request class encapsulating what will be submitted as a book creation request.
 * 
 * @author nigel
 */
public class BookCreateRequest {

    private String name;

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Book toBook() {

        final Book book = new Book();
        book.setName(this.name);

        return book;
    }
}