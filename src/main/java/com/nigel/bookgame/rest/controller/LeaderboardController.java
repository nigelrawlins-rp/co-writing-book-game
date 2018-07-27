package com.nigel.bookgame.rest.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.nigel.bookgame.rest.domain.Book;
import com.nigel.bookgame.rest.repository.BookRepository;
import com.nigel.bookgame.rest.resource.LeaderboardResource;
import com.nigel.bookgame.rest.resource.LeaderboardResourceAssembler;

/**
 * Leaderboard controller class providing endpoints for:
 * 
 * - Getting the leaderboard.
 * 
 * The logic here is in the presentation/resource layer only: no separate domain logic or repository is required
 * as the leaderboard can always be determined by the current state of the stored books.
 * 
 * @author nigel
 */
@RestController
@RequestMapping(value = "/leaderboard", produces = "application/json")
public class LeaderboardController {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private LeaderboardResourceAssembler leaderboardResourceAssembler;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<LeaderboardResource> showLeaderboard() {
        List<Book> books = this.bookRepository.findAll();
        return new ResponseEntity<>(this.leaderboardResourceAssembler.toResource(books), HttpStatus.OK);
    }
}