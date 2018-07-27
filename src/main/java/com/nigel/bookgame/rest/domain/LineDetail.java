package com.nigel.bookgame.rest.domain;

/**
 * Class describing information about a line in a book.
 * Equals is not overridden because the same player could add the same line (to any book).
 * 
 * @author nigel
 */
public class LineDetail {

    private String line;
    private String playerName;

    public LineDetail(final String line, final String playerName) {
        this.line = line;
        this.playerName = playerName;
    }

    public LineDetail(final LineDetail lineDetail) {
        this.line = lineDetail.getLine();
        this.playerName = lineDetail.getPlayerName();
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }
}