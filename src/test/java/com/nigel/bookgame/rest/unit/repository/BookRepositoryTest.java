package com.nigel.bookgame.rest.unit.repository;

import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
@SpringBootTest
public class BookRepositoryTest {
	
	private static final long NON_EXISTENT_ID = 10;
	private static final String PLAYER_NAME_1 = "Nigel";
    private static final String PLAYER_NAME_2 = "Marie";

	@Autowired
	private BookRepository repository;
	
	@BeforeEach
	public void setUp() {
		this.repository.clear();
	}
	
	@Test
	public void testFindNonExistentBook() {
		
	    final BookContainer bookContainer = this.repository.findById(NON_EXISTENT_ID, PLAYER_NAME_1);
		
	    Assertions.assertThat(bookContainer.getBook()).isNull();
		Assertions.assertThat(bookContainer.getRetrievalOutcome()).isEqualTo(ERetrievalOutcome.NOT_FOUND);
	}
	
	@Test
	public void testFindExistingBook() {
	    
		final Book book = createBook();
		
		final BookContainer bookContainer = this.repository.findById(book.getId(), PLAYER_NAME_1);
		
		final Book bookFound = bookContainer.getBook();
		
        Assertions.assertThat(bookFound).isNotNull();
        Assertions.assertThat(bookContainer.getRetrievalOutcome()).isEqualTo(ERetrievalOutcome.SUCCESS);
        assertBooksMatch(book, bookFound);
	}
    
    @Test
    public void testFindExistingBookSecondPlayerFailsToGetLock() {
        
        final Book book = createBook();
        
        this.repository.findById(book.getId(), PLAYER_NAME_1);
        
        final BookContainer bookContainer = this.repository.findById(book.getId(), PLAYER_NAME_2);
		
	    Assertions.assertThat(bookContainer.getBook()).isNull();
		Assertions.assertThat(bookContainer.getRetrievalOutcome()).isEqualTo(ERetrievalOutcome.LOCKED_BY_ANOTHER_PLAYER);
    }
    
    @Test
    public void testFindExistingBookPlayerLocksTwice() {
        
        final Book book = createBook();
        
        this.repository.findById(book.getId(), PLAYER_NAME_1);
        
        final BookContainer bookContainer = this.repository.findById(book.getId(), PLAYER_NAME_1);
		
        Assertions.assertThat(bookContainer.getBook()).isNotNull();
        Assertions.assertThat(bookContainer.getRetrievalOutcome()).isEqualTo(ERetrievalOutcome.SUCCESS);
    }
	
	@Test
	public void testFindAllNoBooks() {
	    
        final List<Book> books = this.repository.findAll();
        
        Assertions.assertThat(books).isEmpty();
	}
	
	@Test
	public void testFindAllOneBook() {
		
	    final List<Book> books = createBooks(1);
	    
        final List<Book> booksFound = this.repository.findAll();
        
        Assertions.assertThat(booksFound).hasSize(1);
        assertBooksMatch(books.get(0), booksFound.get(0));
	}
	
	@Test
	public void testFindAllTwoBooks() {
        
        final List<Book> books = createBooks(2);
        
        final List<Book> booksFound = this.repository.findAll();
        
        Assertions.assertThat(booksFound).hasSize(2);
        assertBooksMatch(books.get(0), booksFound.get(0));
        assertBooksMatch(books.get(1), booksFound.get(1));
	}
	
	@Test
	public void testUpdateBookWithoutLock() {
	    
		final Book book = createBook();
		
        final EUpdateOutcome updateOutcome = this.repository.update(book.getId(), new Book(), PLAYER_NAME_1, true);
        
        Assertions.assertThat(updateOutcome).isEqualTo(EUpdateOutcome.REQUESTING_PLAYER_DOES_NOT_HAVE_LOCK);
	}
	
	@Test
	public void testUpdateBookNull() {
	    
		final Book book = createBook();
		
		final EUpdateOutcome updateOutcome = this.repository.update(book.getId(), null, PLAYER_NAME_1, true);
        
        Assertions.assertThat(updateOutcome).isEqualTo(EUpdateOutcome.NULL_BOOK_SUPPLIED);
	}
    
    @Test
    public void testUpdateBookNotFound() {
        
        final Book book = new Book();
        book.lock(PLAYER_NAME_1);
        
        final EUpdateOutcome updateOutcome = this.repository.update(NON_EXISTENT_ID, book, PLAYER_NAME_1, true);
        
        Assertions.assertThat(updateOutcome).isEqualTo(EUpdateOutcome.NOT_FOUND);
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
        
        Assertions.assertThat(updateOutcome).isEqualTo(EUpdateOutcome.SUCCESS);
    }
    
    @Test
    public void testUpdateWhereDoesNotUnlock() {
        
        final Book book = createBook();
        book.lock(PLAYER_NAME_1);
        
        final Book updatedBook = new Book();
        updatedBook.setId(book.getId());
        updatedBook.lock(PLAYER_NAME_1);
        
        final EUpdateOutcome updateOutcome = this.repository.update(book.getId(), updatedBook, PLAYER_NAME_1, false);
        
        Assertions.assertThat(updateOutcome).isEqualTo(EUpdateOutcome.SUCCESS);
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
        Assertions.assertThat(actualBook.getId()).isEqualTo(expectedBook.getId());
        Assertions.assertThat(actualBook.getName()).isEqualTo(expectedBook.getName());
        Assertions.assertThat(actualBook.getLineDetailMap()).isEqualTo(expectedBook.getLineDetailMap());
        Assertions.assertThat(actualBook.isComplete()).isEqualTo(expectedBook.isComplete());
    }
}