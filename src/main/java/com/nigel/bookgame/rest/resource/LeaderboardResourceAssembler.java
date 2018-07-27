package com.nigel.bookgame.rest.resource;

import java.util.List;

import org.springframework.stereotype.Component;

import com.nigel.bookgame.rest.domain.Book;

/**
 * Resource assembler for a leaderboard. Included for similarity with {@link BookResourceAssembler}
 * but is trivial in practice because no HATEOAS links are included.
 * 
 * @author nigel
 */
@Component
public class LeaderboardResourceAssembler extends ResourceAssembler<List<Book>, LeaderboardResource> {
    
    @Override
    public LeaderboardResource toResource(List<Book> books) {
        LeaderboardResource resource = new LeaderboardResource(books);
        return resource;
    }
}