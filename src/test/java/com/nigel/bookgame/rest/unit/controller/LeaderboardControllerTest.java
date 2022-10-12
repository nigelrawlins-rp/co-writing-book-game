package com.nigel.bookgame.rest.unit.controller;

import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.nigel.bookgame.rest.controller.LeaderboardController;
import com.nigel.bookgame.rest.domain.Book;
import com.nigel.bookgame.rest.repository.BookRepository;
import com.nigel.bookgame.rest.resource.LeaderboardResource;
import com.nigel.bookgame.rest.resource.LeaderboardResourceAssembler;

/**
 * Test class for {@link LeaderboardController}.
 * 
 * @author nigel
 */
@ExtendWith(MockitoExtension.class)
public class LeaderboardControllerTest {
    
    @Mock
    private BookRepository mockBookRepository;
    
    @Mock
    private LeaderboardResourceAssembler leaderboardResourceAssembler;
    
    @InjectMocks
    private LeaderboardController leaderboardController;
    
    @Test
    public void testShowLeaderboard() {
        
        final List<Book> listOfBooks = new ArrayList<Book>();
        final LeaderboardResource leaderboardResource = new LeaderboardResource(listOfBooks);
        
        Mockito.when(this.mockBookRepository.findAll()).thenReturn(listOfBooks);
        Mockito.when(this.leaderboardResourceAssembler.toResource(Mockito.anyList())).thenReturn(leaderboardResource);
        
        final ResponseEntity<LeaderboardResource> responseEntity = this.leaderboardController.showLeaderboard();
        
        Assertions.assertThat(responseEntity.getBody()).isEqualTo(leaderboardResource);
        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        
        Mockito.verify(this.mockBookRepository).findAll();
        Mockito.verify(this.leaderboardResourceAssembler).toResource(listOfBooks);
    }
}