package com.nigel.bookgame.rest.unit.resource;

import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import com.nigel.bookgame.rest.domain.Book;
import com.nigel.bookgame.rest.resource.LeaderboardResource;
import com.nigel.bookgame.rest.resource.LeaderboardResource.Player;

/**
 * Test class for {@link LeaderboardResource}.
 * 
 * @author nigel
 */
public class LeaderboardResourceTest {
    
    /*
     * Tests that:
     * 
     * 1. Scores for incomplete books are not included.
     * 2. Where somebody contributes multiple times to the same book, they only score 10 points for that book.
     * 3. Where scores in the leaderboard are tied, ordering is alphabetical.
     * 4. Where 5th place is tied, the ties are included but no further.
     */
    @Test
    public void testLeaderboardCorrect() {
        
        final Book book1 = new Book();
        book1.setId(Long.valueOf(1));
        book1.setName("Book 1");
        book1.addLineToLineDetailMap("Line 1.", "Nigel");
        book1.addLineToLineDetailMap("Line 2.", "Marie");
        book1.addLineToLineDetailMap("Line 3.", "Jeremy");
        book1.addLineToLineDetailMap("Line 4.", "Chloe");
        book1.addLineToLineDetailMap("Line 5.", "Steph");
        
        final Book book2 = new Book();
        book2.setId(Long.valueOf(2));
        book2.setName("Book 2");
        book2.addLineToLineDetailMap("Line 1.", "Nick");
        book2.addLineToLineDetailMap("Line 2.", "Nigel");
        book2.addLineToLineDetailMap("Line 3.", "Jeremy");
        book2.addLineToLineDetailMap("Line 4.", "Chloe");
        book2.addLineToLineDetailMap("Line 5.", "Jeremy");
        
        final Book book3 = new Book();
        book3.setId(Long.valueOf(3));
        book3.setName("Book 3");
        book3.addLineToLineDetailMap("Line 1.", "Marie");
        book3.addLineToLineDetailMap("Line 2.", "Steph");
        book3.addLineToLineDetailMap("Line 3.", "Chloe");
        book3.addLineToLineDetailMap("Line 4.", "Nick");
        book3.addLineToLineDetailMap("Line 5.", "Amaury");
        
        final Book book4 = new Book();
        book4.setId(Long.valueOf(4));
        book4.setName("Book 4");
        book4.addLineToLineDetailMap("Line 1.", "Aubry");
        book4.addLineToLineDetailMap("Line 2.", "Jeremy");
        book4.addLineToLineDetailMap("Line 3.", "Marie");
        book4.addLineToLineDetailMap("Line 4.", "Nick");
        book4.addLineToLineDetailMap("Line 5.", "Amaury");
        
        final Book book5 = new Book();
        book5.setId(Long.valueOf(5));
        book5.setName("Book 5");
        book5.addLineToLineDetailMap("Line 1.", "Chloe");
        book5.addLineToLineDetailMap("Line 2.", "Amaury");
        book5.addLineToLineDetailMap("Line 3.", "Aubry");
        book5.addLineToLineDetailMap("Line 4.", "Nigel");
        
        final List<Book> listOfBooks = new ArrayList<Book>();
        listOfBooks.add(book1);
        listOfBooks.add(book2);
        listOfBooks.add(book3);
        listOfBooks.add(book4);
        listOfBooks.add(book5);
        
        final LeaderboardResource leaderboardResource = new LeaderboardResource(listOfBooks);
        
        final List<Player> leaderboard = leaderboardResource.getLeaderboard();
        
        Assertions.assertThat(leaderboard).hasSize(7);
        Assertions.assertThat(leaderboard.get(0).getPlayerName()).isEqualTo("Chloe");
        Assertions.assertThat(leaderboard.get(0).getScore()).isEqualTo(Integer.valueOf(30));
        Assertions.assertThat(leaderboard.get(1).getPlayerName()).isEqualTo("Jeremy");
        Assertions.assertThat(leaderboard.get(1).getScore()).isEqualTo(Integer.valueOf(30));
        Assertions.assertThat(leaderboard.get(2).getPlayerName()).isEqualTo("Marie");
        Assertions.assertThat(leaderboard.get(2).getScore()).isEqualTo(Integer.valueOf(30));
        Assertions.assertThat(leaderboard.get(3).getPlayerName()).isEqualTo("Nick");
        Assertions.assertThat(leaderboard.get(3).getScore()).isEqualTo(Integer.valueOf(30));
        Assertions.assertThat(leaderboard.get(4).getPlayerName()).isEqualTo("Amaury");
        Assertions.assertThat(leaderboard.get(4).getScore()).isEqualTo(Integer.valueOf(20));
        Assertions.assertThat(leaderboard.get(5).getPlayerName()).isEqualTo("Nigel");
        Assertions.assertThat(leaderboard.get(5).getScore()).isEqualTo(Integer.valueOf(20));
        Assertions.assertThat(leaderboard.get(6).getPlayerName()).isEqualTo("Steph");
        Assertions.assertThat(leaderboard.get(6).getScore()).isEqualTo(Integer.valueOf(20));
    }
}