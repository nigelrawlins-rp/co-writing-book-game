package com.nigel.bookgame.rest.domain;

/**
 * Enumeration describing the possible outcomes of an attempt to update a book.
 * 
 * @author nigel
 */
public enum EUpdateOutcome {
    SUCCESS,
    REQUESTING_PLAYER_DOES_NOT_HAVE_LOCK,
    NULL_BOOK_SUPPLIED,
    NOT_FOUND
}