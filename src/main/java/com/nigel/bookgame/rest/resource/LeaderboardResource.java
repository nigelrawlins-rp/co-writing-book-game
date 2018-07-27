package com.nigel.bookgame.rest.resource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.hateoas.ResourceSupport;

import com.nigel.bookgame.rest.domain.Book;
import com.nigel.bookgame.rest.domain.LineDetail;

/**
 * Resource object representation of a leaderboard which captures what should be presented to the caller.
 * 
 * @author nigel
 */
public class LeaderboardResource extends ResourceSupport {
    
    private List<Player> leaderboard = new ArrayList<Player>();
    
    public LeaderboardResource(final List<Book> books) {
        
        final Map<String, Integer> playerScoreMap = new HashMap<String, Integer>();
        
        for (Book book : books) {
            if (book.isComplete()) {
                final Set<String> bookContributors = deriveBookContributors(book);
                updateOverallScoresOfContributors(playerScoreMap, bookContributors);
            }
        }
        
        addPlayersToLeaderboard(playerScoreMap);
        this.leaderboard = this.leaderboard.stream().sorted().collect(Collectors.toList());
        short numberOfPlayersToDisplay = deriveMaximumNumberOfPlayersToDisplay();
        this.leaderboard = this.leaderboard.stream().limit(numberOfPlayersToDisplay).collect(Collectors.toList());
    }
    
    private Set<String> deriveBookContributors(final Book book) {
        
        final Set<String> bookContributors = new HashSet<String>();
        
        for (LineDetail lineDetail : book.getLineDetailMap().values()) {
            bookContributors.add(lineDetail.getPlayerName());
        }
        
        return bookContributors;
    }
    
    private void updateOverallScoresOfContributors(final Map<String, Integer> playerScoreMap, final Set<String> bookContributors) {
        
        for (String bookContributor : bookContributors) {
            if (playerScoreMap.containsKey(bookContributor)) {
                playerScoreMap.put(bookContributor, playerScoreMap.get(bookContributor) + 10);
            }
            else {
                playerScoreMap.put(bookContributor, 10);
            }
        }
    }
    
    private void addPlayersToLeaderboard(final Map<String, Integer> playerScoreMap) {
        
        for (Map.Entry<String, Integer> playerScoreMapEntry : playerScoreMap.entrySet()) {
            final Player player = new Player(playerScoreMapEntry.getKey(), playerScoreMapEntry.getValue());
            this.leaderboard.add(player);
        }
    }
    
    // This is to cater to ties for 5th place.
    private short deriveMaximumNumberOfPlayersToDisplay() {
        
        short maximumNumberOfPlayersToDisplay = 5;
        short rank = 0;
        Integer rank_5_score = Integer.valueOf(0);
        
        for (Player player : this.leaderboard) {
            
            rank++;
            
            if (rank < 5) {
                continue;
            }
            
            if (rank == 5) {
                rank_5_score = player.getScore();
                continue;
            }
            
            if (player.getScore().equals(rank_5_score)) {
                maximumNumberOfPlayersToDisplay++;
            }
            else {
                break;
            }
        }
        
        return maximumNumberOfPlayersToDisplay;
    }
    
    public List<Player> getLeaderboard() {
        return this.leaderboard;
    }
    
    public class Player implements Comparable<Player> {
        
        private String playerName;
        private Integer score;
        
        private Player(String playerName, Integer score) {
            this.playerName = playerName;
            this.score = score;
        }
        
        public String getPlayerName() {
            return this.playerName;
        }
        
        public Integer getScore() {
            return this.score;
        }
        
        @Override
        public int compareTo(Player otherPlayer) {
            
            if (otherPlayer.getScore().equals(this.score)) {
                return this.playerName.compareTo(otherPlayer.getPlayerName());
            }
            
            return otherPlayer.getScore().compareTo(this.score);
        }
    }
}