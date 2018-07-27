package com.nigel.bookgame.rest.unit.resource;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.nigel.bookgame.rest.domain.Book;
import com.nigel.bookgame.rest.resource.BookResource;

/**
 * Test class for {@link BookResource}.
 * 
 * @author nigel
 */
public class BookResourceTest {
    
    @Test
    public void testCorrectRepresentationExposedWhenBookNotComplete() {
        
        final Book book = createIncompleteBook();
        
        final BookResource bookResource = new BookResource(book);
        
        Assert.assertEquals(Long.valueOf(1), bookResource.getResourceId());
        Assert.assertEquals("My book", bookResource.getName());
        Assert.assertFalse(bookResource.isComplete());
        
        final List<String> lines = bookResource.getLines();
        Assert.assertEquals(1, lines.size());
        Assert.assertEquals("Line 4.", lines.get(0));
    }
    
    @Test
    public void testCorrectRepresentationExposedWhenBookComplete() {
        
        final Book book = createCompleteBook();
        
        final BookResource bookResource = new BookResource(book);
        
        Assert.assertEquals(Long.valueOf(1), bookResource.getResourceId());
        Assert.assertEquals("My book", bookResource.getName());
        Assert.assertTrue(bookResource.isComplete());
        
        final List<String> lines = bookResource.getLines();
        Assert.assertEquals(5, lines.size());
        Assert.assertEquals("Line 1.", lines.get(0));
        Assert.assertEquals("Line 2.", lines.get(1));
        Assert.assertEquals("Line 3.", lines.get(2));
        Assert.assertEquals("Line 4.", lines.get(3));
        Assert.assertEquals("Line 5.", lines.get(4));
    }
    
    @Test
    public void testCorrectRepresentationExposedWhenBookEmpty() {
        
        final Book book = createEmptyBook();
        
        final BookResource bookResource = new BookResource(book);
        
        Assert.assertEquals(Long.valueOf(1), bookResource.getResourceId());
        Assert.assertEquals("My book", bookResource.getName());
        Assert.assertFalse(bookResource.isComplete());
        Assert.assertTrue(bookResource.getLines().isEmpty());
    }
    
    private Book createEmptyBook() {
        
        final Book book = new Book();
        book.setId(Long.valueOf(1));
        book.setName("My book");
        
        return book;
    }
    
    private Book createIncompleteBook() {
        
        final Book book = createEmptyBook();
        book.addLineToLineDetailMap("Line 1.", "Nigel");
        book.addLineToLineDetailMap("Line 2.", "Marie");
        book.addLineToLineDetailMap("Line 3.", "Jeremy");
        book.addLineToLineDetailMap("Line 4.", "Chloe");
        
        return book;
    }
    
    private Book createCompleteBook() {
        
        final Book book = createIncompleteBook();
        book.addLineToLineDetailMap("Line 5.", "Steph");
        
        return book;
    }
}