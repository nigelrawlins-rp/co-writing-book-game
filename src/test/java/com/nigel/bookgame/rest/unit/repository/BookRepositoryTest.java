package com.nigel.bookgame.rest.unit.repository;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.nigel.bookgame.rest.domain.Book;
import com.nigel.bookgame.rest.domain.BookContainer;
import com.nigel.bookgame.rest.domain.ERetrievalOutcome;
import com.nigel.bookgame.rest.domain.EUpdateOutcome;
import com.nigel.bookgame.rest.repository.BookRepository;

/**
 * Test class for {@link BookRepository}.
 * 
 * @author nigel
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class BookRepositoryTest {
	
	private static final long NON_EXISTENT_ID = 10;
	private static final String PLAYER_NAME_1 = "Nigel";
    private static final String PLAYER_NAME_2 = "Marie";

	@Autowired
	private BookRepository repository;
	
	@Before
	public void setUp() {
		this.repository.clear();
	}
	
	@Test
	public void testFindNonExistentBook() {
		
	    final BookContainer bookContainer = this.repository.findById(NON_EXISTENT_ID, PLAYER_NAME_1);
		
	    Assert.assertNull(bookContainer.getBook());
		Assert.assertEquals(ERetrievalOutcome.NOT_FOUND, bookContainer.getRetrievalOutcome());
	}
	
	@Test
	public void testFindExistingBook() {
	    
		final Book book = createBook();
		
		final BookContainer bookContainer = this.repository.findById(book.getId(), PLAYER_NAME_1);
		
		final Book bookFound = bookContainer.getBook();
		
        Assert.assertNotNull(bookFound);
        Assert.assertEquals(ERetrievalOutcome.SUCCESS, bookContainer.getRetrievalOutcome());
        assertBooksMatch(book, bookFound);
	}
    
    @Test
    public void testFindExistingBookSecondPlayerFailsToGetLock() {
        
        final Book book = createBook();
        
        this.repository.findById(book.getId(), PLAYER_NAME_1);
        
        final BookContainer bookContainer = this.repository.findById(book.getId(), PLAYER_NAME_2);
        
        Assert.assertNull(bookContainer.getBook());
        Assert.assertEquals(ERetrievalOutcome.LOCKED_BY_ANOTHER_PLAYER, bookContainer.getRetrievalOutcome());
    }
    
    @Test
    public void testFindExistingBookPlayerLocksTwice() {
        
        final Book book = createBook();
        
        this.repository.findById(book.getId(), PLAYER_NAME_1);
        
        final BookContainer bookContainer = this.repository.findById(book.getId(), PLAYER_NAME_1);
        
        Assert.assertNotNull(bookContainer.getBook());
        Assert.assertEquals(ERetrievalOutcome.SUCCESS, bookContainer.getRetrievalOutcome());
    }
	
	@Test
	public void testFindAllNoBooks() {
	    
        final List<Book> books = this.repository.findAll();
        
        Assert.assertTrue(books.isEmpty());
	}
	
	@Test
	public void testFindAllOneBook() {
		
	    final List<Book> books = createBooks(1);
	    
        final List<Book> booksFound = this.repository.findAll();
        
        Assert.assertEquals(1, booksFound.size());
        assertBooksMatch(books.get(0), booksFound.get(0));
	}
	
	@Test
	public void testFindAllTwoBooks() {
        
        final List<Book> books = createBooks(2);
        
        final List<Book> booksFound = this.repository.findAll();
        
        Assert.assertEquals(2, booksFound.size());
        assertBooksMatch(books.get(0), booksFound.get(0));
        assertBooksMatch(books.get(1), booksFound.get(1));
	}
	
	@Test
	public void testUpdateBookWithoutLock() {
	    
		final Book book = createBook();
		
        final EUpdateOutcome updateOutcome = this.repository.update(book.getId(), new Book(), PLAYER_NAME_1, true);
        
        Assert.assertEquals(EUpdateOutcome.REQUESTING_PLAYER_DOES_NOT_HAVE_LOCK, updateOutcome);
	}
	
	@Test
	public void testUpdateBookNull() {
	    
		final Book book = createBook();
		
		final EUpdateOutcome updateOutcome = this.repository.update(book.getId(), null, PLAYER_NAME_1, true);
		
        Assert.assertEquals(EUpdateOutcome.NULL_BOOK_SUPPLIED, updateOutcome);
	}
    
    @Test
    public void testUpdateBookNotFound() {
        
        final Book book = new Book();
        book.lock(PLAYER_NAME_1);
        
        final EUpdateOutcome updateOutcome = this.repository.update(NON_EXISTENT_ID, book, PLAYER_NAME_1, true);
        
        Assert.assertEquals(EUpdateOutcome.NOT_FOUND, updateOutcome);
    }
    
    @Test
    public void testUpdateTwoBooks() {
        
        createBook();
        
        final Book book = createBook();
        book.lock(PLAYER_NAME_1);
        
        final Book updatedBook = new Book();
        updatedBook.setId(book.getId());
        updatedBook.lock(PLAYER_NAME_1);
        
        final EUpdateOutcome updateOutcome = this.repository.update(book.getId(), updatedBook, PLAYER_NAME_1, true);
        
        Assert.assertEquals(EUpdateOutcome.SUCCESS, updateOutcome);
    }
    
    @Test
    public void testUpdateWhereDoesNotUnlock() {
        
        final Book book = createBook();
        book.lock(PLAYER_NAME_1);
        
        final Book updatedBook = new Book();
        updatedBook.setId(book.getId());
        updatedBook.lock(PLAYER_NAME_1);
        
        final EUpdateOutcome updateOutcome = this.repository.update(book.getId(), updatedBook, PLAYER_NAME_1, false);
        
        Assert.assertEquals(EUpdateOutcome.SUCCESS, updateOutcome);
    }
    
    private Book createBook() {
        
        final Book book = new Book();
        book.setName("My book");
        
        return this.repository.create(book);
    }
    
    private List<Book> createBooks(final int count) {
        
        List<Book> books = new ArrayList<>();
        
        for (int i = 0; i < count; i++) {
            books.add(createBook());
        }
        
        return books;
    }
    
    private void assertBooksMatch(final Book expectedBook, final Book actualBook) {
        Assert.assertEquals(expectedBook.getId(), actualBook.getId());
        Assert.assertEquals(expectedBook.getName(), actualBook.getName());
        Assert.assertEquals(expectedBook.getLineDetailMap(), actualBook.getLineDetailMap());
        Assert.assertEquals(expectedBook.isComplete(), actualBook.isComplete());
    }
}