package com.nigel.bookgame.rest.unit.resource;

import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

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
        
        Assertions.assertThat(bookResource.getResourceId()).isEqualTo(Long.valueOf(1));
        Assertions.assertThat(bookResource.getName()).isEqualTo("My book");
        Assertions.assertThat(bookResource.isComplete()).isFalse();
        
        final List<String> lines = bookResource.getLines();
        Assertions.assertThat(lines).hasSize(1);
        Assertions.assertThat(lines.get(0)).isEqualTo("Line 4.");
    }
    
    @Test
    public void testCorrectRepresentationExposedWhenBookComplete() {
        
        final Book book = createCompleteBook();
        
        final BookResource bookResource = new BookResource(book);
        
        Assertions.assertThat(bookResource.getResourceId()).isEqualTo(Long.valueOf(1));
        Assertions.assertThat(bookResource.getName()).isEqualTo("My book");
        Assertions.assertThat(bookResource.isComplete()).isTrue();
        
        final List<String> lines = bookResource.getLines();
        Assertions.assertThat(lines).hasSize(5);
        Assertions.assertThat(lines.get(0)).isEqualTo("Line 1.");
        Assertions.assertThat(lines.get(1)).isEqualTo("Line 2.");
        Assertions.assertThat(lines.get(2)).isEqualTo("Line 3.");
        Assertions.assertThat(lines.get(3)).isEqualTo("Line 4.");
        Assertions.assertThat(lines.get(4)).isEqualTo("Line 5.");
    }
    
    @Test
    public void testCorrectRepresentationExposedWhenBookEmpty() {
        
        final Book book = createEmptyBook();
        
        final BookResource bookResource = new BookResource(book);
        
        Assertions.assertThat(bookResource.getResourceId()).isEqualTo(Long.valueOf(1));
        Assertions.assertThat(bookResource.getName()).isEqualTo("My book");
        Assertions.assertThat(bookResource.isComplete()).isFalse();
        Assertions.assertThat(bookResource.getLines()).isEmpty();
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