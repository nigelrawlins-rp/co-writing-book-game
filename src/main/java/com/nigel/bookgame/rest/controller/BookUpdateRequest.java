package com.nigel.bookgame.rest.controller;

/**
 * Request class encapsulating what will be submitted as a book update request.
 * 
 * @author nigel
 */
public class BookUpdateRequest {

    private String newLine;
    private String playerName;

    public String getNewLine() {
        return this.newLine;
    }

    public void setNewLine(final String newLine) {
        this.newLine = newLine;
    }

    public String getPlayerName() {
        return this.playerName;
    }

    public void setPlayerName(final String playerName) {
        this.playerName = playerName;
    }
}