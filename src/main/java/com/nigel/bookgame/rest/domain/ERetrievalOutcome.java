package com.nigel.bookgame.rest.domain;

/**
 * Enumeration describing the possible outcomes of an attempt to retrieve a book.
 * 
 * @author nigel
 */
public enum ERetrievalOutcome {
    SUCCESS,
    NOT_FOUND,
    LOCKED_BY_ANOTHER_PLAYER
}