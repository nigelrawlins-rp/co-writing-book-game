package com.nigel.bookgame.rest.unit.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import com.nigel.bookgame.rest.domain.Book;
import com.nigel.bookgame.rest.domain.LineDetail;

/**
 * Test class for {@link Book}.
 * 
 * @author nigel
 */
public class BookTest {
    
    @Test
    public void testAddingLines() {
        
        final Book book = new Book();
        Assertions.assertThat(book.getLineDetailMap()).isEmpty();
        
        book.addLineToLineDetailMap("First line.", "Nigel");
        Assertions.assertThat(book.getLineDetailMap()).hasSize(1);
        
        final LineDetail expectedLineDetail = new LineDetail("First line.", "Nigel");
        Assertions.assertThat(expectedLineDetail.getLine()).isEqualTo(book.getLineDetailMap().get(1).getLine());
        Assertions.assertThat(expectedLineDetail.getPlayerName()).isEqualTo(book.getLineDetailMap().get(1).getPlayerName());
    }
    
    @Test
    public void testLocking() {
        
        final Book book = new Book();
        Assertions.assertThat(book.getLockedBy()).isNull();
        Assertions.assertThat(book.lock("Nigel")).isTrue();
        Assertions.assertThat(book.getLockedBy()).isEqualTo("Nigel");
        Assertions.assertThat(book.lock("Marie")).isFalse();
        Assertions.assertThat(book.getLockedBy()).isEqualTo("Nigel");
        
        book.unlock();
        Assertions.assertThat(book.getLockedBy()).isNull();
    }
    
    @Test
    public void testEquality() {
        
        final Book book1 = new Book();
        book1.setId(Long.valueOf(1));
        
        final Book book2 = new Book();
        book2.setId(Long.valueOf(2));
        
        final Object object = new Object();
        
        Assertions.assertThat(object).isNotEqualTo(book1);
        Assertions.assertThat(book2).isNotEqualTo(book1);
        
        book2.setId(Long.valueOf(1));
        
        Assertions.assertThat(book2).isEqualTo(book1);
    }
}