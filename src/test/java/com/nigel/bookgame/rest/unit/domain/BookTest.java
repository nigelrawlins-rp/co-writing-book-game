package com.nigel.bookgame.rest.unit.domain;

import org.junit.Assert;
import org.junit.Test;

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
        Assert.assertTrue(book.getLineDetailMap().isEmpty());
        
        book.addLineToLineDetailMap("First line.", "Nigel");
        Assert.assertEquals(1, book.getLineDetailMap().size());
        
        final LineDetail expectedLineDetail = new LineDetail("First line.", "Nigel");
        Assert.assertEquals(expectedLineDetail.getLine(), book.getLineDetailMap().get(1).getLine());
        Assert.assertEquals(expectedLineDetail.getPlayerName(), book.getLineDetailMap().get(1).getPlayerName());
    }
    
    @Test
    public void testLocking() {
        
        final Book book = new Book();
        Assert.assertNull(book.getLockedBy());
        Assert.assertTrue(book.lock("Nigel"));
        Assert.assertEquals("Nigel", book.getLockedBy());
        Assert.assertFalse(book.lock("Marie"));
        Assert.assertEquals("Nigel", book.getLockedBy());
        
        book.unlock();
        Assert.assertNull(book.getLockedBy());
    }
    
    @Test
    public void testEquality() {
        
        final Book book1 = new Book();
        book1.setId(Long.valueOf(1));
        
        final Book book2 = new Book();
        book2.setId(Long.valueOf(2));
        
        final Object object = new Object();
        
        Assert.assertFalse(book1.equals(object));
        Assert.assertFalse(book1.equals(book2));
        
        book2.setId(Long.valueOf(1));
        
        Assert.assertTrue(book1.equals(book2));
    }
}