package com.nigel.bookgame.rest.unit.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.nigel.bookgame.rest.controller.BookController;
import com.nigel.bookgame.rest.controller.BookCreateRequest;
import com.nigel.bookgame.rest.controller.BookUpdateRequest;
import com.nigel.bookgame.rest.domain.Book;
import com.nigel.bookgame.rest.domain.BookContainer;
import com.nigel.bookgame.rest.domain.ERetrievalOutcome;
import com.nigel.bookgame.rest.domain.EUpdateOutcome;
import com.nigel.bookgame.rest.repository.BookRepository;
import com.nigel.bookgame.rest.resource.BookResource;
import com.nigel.bookgame.rest.resource.BookResourceAssembler;

/**
 * Test class for {@link BookController}.
 * 
 * @author nigel
 */
@RunWith(MockitoJUnitRunner.class)
public class BookControllerTest {
    
    @Mock
    private BookRepository mockBookRepository;
    
    @Mock
    private BookResourceAssembler mockBookResourceAssembler;
    
    @InjectMocks
    private BookController bookController;
    
    @Test
    public void testFindAllBooks() {
        
        final List<Book> listOfBooks = new ArrayList<Book>();
        final List<BookResource> listOfBookResources = new ArrayList<BookResource>();
        
        Mockito.when(this.mockBookRepository.findAll()).thenReturn(listOfBooks);
        Mockito.when(this.mockBookResourceAssembler.toResourceCollection(Mockito.anyCollection())).thenReturn(listOfBookResources);
        
        final ResponseEntity<Collection<BookResource>> responseEntity = this.bookController.findAllBooks();
        
        Assert.assertEquals(listOfBookResources, responseEntity.getBody());
        Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        
        Mockito.verify(this.mockBookRepository).findAll();
        Mockito.verify(this.mockBookResourceAssembler).toResourceCollection(listOfBooks);
    }
    
    @Test
    public void testFindBookByIdWhereNotFound() {
        
        final BookContainer bookContainer = new BookContainer();
        bookContainer.setBook(null);
        bookContainer.setRetrievalOutcome(ERetrievalOutcome.NOT_FOUND);
        
        Mockito.when(this.mockBookRepository.findById(Mockito.anyLong(), Mockito.anyString())).thenReturn(bookContainer);
        
        final ResponseEntity<BookResource> responseEntity = this.bookController.findBookById(Long.valueOf(1), "Nigel");
        
        Assert.assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        
        final ArgumentCaptor<Long> argumentCaptorForId = ArgumentCaptor.forClass(Long.class);
        final ArgumentCaptor<String> argumentCaptorForPlayerName = ArgumentCaptor.forClass(String.class);
        
        Mockito.verify(this.mockBookRepository).findById(argumentCaptorForId.capture(), argumentCaptorForPlayerName.capture());
        
        Assert.assertEquals(Long.valueOf(1), argumentCaptorForId.getValue());
        Assert.assertEquals("Nigel", argumentCaptorForPlayerName.getValue());
    }
    
    @Test
    public void testFindBookByIdWhereLockedByAnotherPlayer() {
        
        final BookContainer bookContainer = new BookContainer();
        bookContainer.setBook(null);
        bookContainer.setRetrievalOutcome(ERetrievalOutcome.LOCKED_BY_ANOTHER_PLAYER);
        
        Mockito.when(this.mockBookRepository.findById(Mockito.anyLong(), Mockito.anyString())).thenReturn(bookContainer);
        
        final ResponseEntity<BookResource> responseEntity = this.bookController.findBookById(Long.valueOf(1), "Nigel");
        
        Assert.assertEquals(HttpStatus.LOCKED, responseEntity.getStatusCode());
        
        final ArgumentCaptor<Long> argumentCaptorForId = ArgumentCaptor.forClass(Long.class);
        final ArgumentCaptor<String> argumentCaptorForPlayerName = ArgumentCaptor.forClass(String.class);
        
        Mockito.verify(this.mockBookRepository).findById(argumentCaptorForId.capture(), argumentCaptorForPlayerName.capture());
        
        Assert.assertEquals(Long.valueOf(1), argumentCaptorForId.getValue());
        Assert.assertEquals("Nigel", argumentCaptorForPlayerName.getValue());
    }
    
    @Test
    public void testFindBookByIdWhereSuccessful() {
        
        final Book book = new Book();
        book.setId(Long.valueOf(1));
        
        final BookContainer bookContainer = new BookContainer();
        bookContainer.setBook(book);
        bookContainer.setRetrievalOutcome(ERetrievalOutcome.SUCCESS);
        
        final BookResource bookResource = new BookResource(bookContainer.getBook());
        
        Mockito.when(this.mockBookRepository.findById(Mockito.anyLong(), Mockito.anyString())).thenReturn(bookContainer);
        Mockito.when(this.mockBookResourceAssembler.toResource(Mockito.any(Book.class))).thenReturn(bookResource);
        
        final ResponseEntity<BookResource> responseEntity = this.bookController.findBookById(Long.valueOf(1), "Nigel");
        
        Assert.assertEquals(bookResource, responseEntity.getBody());
        Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        
        final ArgumentCaptor<Long> argumentCaptorForId = ArgumentCaptor.forClass(Long.class);
        final ArgumentCaptor<String> argumentCaptorForPlayerName = ArgumentCaptor.forClass(String.class);
        
        Mockito.verify(this.mockBookRepository).findById(argumentCaptorForId.capture(), argumentCaptorForPlayerName.capture());
        Mockito.verify(this.mockBookResourceAssembler).toResource(bookContainer.getBook());
        
        Assert.assertEquals(Long.valueOf(1), argumentCaptorForId.getValue());
        Assert.assertEquals("Nigel", argumentCaptorForPlayerName.getValue());
    }
    
    @Test
    public void testCreate() {
        
        final Book book = new Book();
        book.setId(Long.valueOf(1));
        
        final BookResource bookResource = new BookResource(book);
        
        final BookCreateRequest bookCreateRequest = new BookCreateRequest();
        bookCreateRequest.setName("My book");
        
        Mockito.when(this.mockBookRepository.create(Mockito.any(Book.class))).thenReturn(book);
        Mockito.when(this.mockBookResourceAssembler.toResource(Mockito.any(Book.class))).thenReturn(bookResource);
        
        final ResponseEntity<BookResource> responseEntity = this.bookController.createBook(bookCreateRequest);
        
        Assert.assertEquals(bookResource, responseEntity.getBody());
        Assert.assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        
        Mockito.verify(this.mockBookRepository).create(Mockito.isA(Book.class));
        Mockito.verify(this.mockBookResourceAssembler).toResource(book);
    }
    
    @Test
    public void testUpdateWherePlayerNameNull() {
        
        final BookUpdateRequest bookUpdateRequest = new BookUpdateRequest();
        bookUpdateRequest.setNewLine("A line.");
        bookUpdateRequest.setPlayerName(null);
        
        final ResponseEntity<BookResource> responseEntity = this.bookController.updateBook(Long.valueOf(1), bookUpdateRequest);
        
        Assert.assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }
    
    @Test
    public void testUpdateWhereNotFound() {
        
        final BookContainer bookContainer = new BookContainer();
        bookContainer.setBook(null);
        bookContainer.setRetrievalOutcome(ERetrievalOutcome.NOT_FOUND);
        
        Mockito.when(this.mockBookRepository.findById(Mockito.anyLong(), Mockito.anyString())).thenReturn(bookContainer);
        
        final BookUpdateRequest bookUpdateRequest = new BookUpdateRequest();
        bookUpdateRequest.setNewLine("A line.");
        bookUpdateRequest.setPlayerName("Nigel");
        
        final ResponseEntity<BookResource> responseEntity = this.bookController.updateBook(Long.valueOf(1), bookUpdateRequest);
        
        Assert.assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        
        final ArgumentCaptor<Long> argumentCaptorForId = ArgumentCaptor.forClass(Long.class);
        final ArgumentCaptor<String> argumentCaptorForPlayerName = ArgumentCaptor.forClass(String.class);
        
        Mockito.verify(this.mockBookRepository).findById(argumentCaptorForId.capture(), argumentCaptorForPlayerName.capture());
        
        Assert.assertEquals(Long.valueOf(1), argumentCaptorForId.getValue());
        Assert.assertEquals("Nigel", argumentCaptorForPlayerName.getValue());
    }
    
    @Test
    public void testUpdateWhereNotLockedByAnotherPlayer() {
        
        final BookContainer bookContainer = new BookContainer();
        bookContainer.setBook(null);
        bookContainer.setRetrievalOutcome(ERetrievalOutcome.LOCKED_BY_ANOTHER_PLAYER);
        
        Mockito.when(this.mockBookRepository.findById(Mockito.anyLong(), Mockito.anyString())).thenReturn(bookContainer);
        
        final BookUpdateRequest bookUpdateRequest = new BookUpdateRequest();
        bookUpdateRequest.setNewLine("A line.");
        bookUpdateRequest.setPlayerName("Nigel");
        
        final ResponseEntity<BookResource> responseEntity = this.bookController.updateBook(Long.valueOf(1), bookUpdateRequest);
        
        Assert.assertEquals(HttpStatus.LOCKED, responseEntity.getStatusCode());
        
        final ArgumentCaptor<Long> argumentCaptorForId = ArgumentCaptor.forClass(Long.class);
        final ArgumentCaptor<String> argumentCaptorForPlayerName = ArgumentCaptor.forClass(String.class);
        
        Mockito.verify(this.mockBookRepository).findById(argumentCaptorForId.capture(), argumentCaptorForPlayerName.capture());
        
        Assert.assertEquals(Long.valueOf(1), argumentCaptorForId.getValue());
        Assert.assertEquals("Nigel", argumentCaptorForPlayerName.getValue());
    }
    
    @Test
    public void testUpdateWhereFoundButNullBookSuppliedToUpdate() {
        
        final Book book = new Book();
        book.setId(Long.valueOf(1));
        
        final BookContainer bookContainer = new BookContainer();
        bookContainer.setBook(new Book());
        bookContainer.setRetrievalOutcome(ERetrievalOutcome.SUCCESS);
        
        Mockito.when(this.mockBookRepository.findById(Mockito.anyLong(), Mockito.anyString())).thenReturn(bookContainer);
        Mockito.when(this.mockBookRepository.update(Mockito.eq(Long.valueOf(1)), Mockito.any(Book.class), Mockito.eq("Nigel"), Mockito.eq(true))).thenReturn(EUpdateOutcome.NULL_BOOK_SUPPLIED);
        
        final BookUpdateRequest bookUpdateRequest = new BookUpdateRequest();
        bookUpdateRequest.setNewLine("A line.");
        bookUpdateRequest.setPlayerName("Nigel");
        
        final ResponseEntity<BookResource> responseEntity = this.bookController.updateBook(Long.valueOf(1), bookUpdateRequest);
        
        Assert.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        
        final ArgumentCaptor<Long> argumentCaptorForId = ArgumentCaptor.forClass(Long.class);
        final ArgumentCaptor<String> argumentCaptorForPlayerName = ArgumentCaptor.forClass(String.class);
        
        Mockito.verify(this.mockBookRepository).findById(argumentCaptorForId.capture(), argumentCaptorForPlayerName.capture());
        Mockito.verify(this.mockBookRepository).update(Mockito.eq(Long.valueOf(1)), Mockito.any(Book.class), Mockito.eq("Nigel"), Mockito.eq(true));
        
        Assert.assertEquals(Long.valueOf(1), argumentCaptorForId.getValue());
        Assert.assertEquals("Nigel", argumentCaptorForPlayerName.getValue());
    }
    
    @Test
    public void testUpdateWhereFoundButRequestingPlayerDoesNotHaveLock() {
        
        final Book book = new Book();
        book.setId(Long.valueOf(1));
        
        final BookContainer bookContainer = new BookContainer();
        bookContainer.setBook(new Book());
        bookContainer.setRetrievalOutcome(ERetrievalOutcome.SUCCESS);
        
        Mockito.when(this.mockBookRepository.findById(Mockito.anyLong(), Mockito.anyString())).thenReturn(bookContainer);
        Mockito.when(this.mockBookRepository.update(Mockito.eq(Long.valueOf(1)), Mockito.any(Book.class), Mockito.eq("Nigel"), Mockito.eq(true))).thenReturn(EUpdateOutcome.REQUESTING_PLAYER_DOES_NOT_HAVE_LOCK);
        
        final BookUpdateRequest bookUpdateRequest = new BookUpdateRequest();
        bookUpdateRequest.setNewLine("A line.");
        bookUpdateRequest.setPlayerName("Nigel");
        
        final ResponseEntity<BookResource> responseEntity = this.bookController.updateBook(Long.valueOf(1), bookUpdateRequest);
        
        Assert.assertEquals(HttpStatus.LOCKED, responseEntity.getStatusCode());
        
        final ArgumentCaptor<Long> argumentCaptorForId = ArgumentCaptor.forClass(Long.class);
        final ArgumentCaptor<String> argumentCaptorForPlayerName = ArgumentCaptor.forClass(String.class);
        
        Mockito.verify(this.mockBookRepository).findById(argumentCaptorForId.capture(), argumentCaptorForPlayerName.capture());
        Mockito.verify(this.mockBookRepository).update(Mockito.eq(Long.valueOf(1)), Mockito.any(Book.class), Mockito.eq("Nigel"), Mockito.eq(true));
        
        Assert.assertEquals(Long.valueOf(1), argumentCaptorForId.getValue());
        Assert.assertEquals("Nigel", argumentCaptorForPlayerName.getValue());
    }
    
    @Test
    public void testUpdateWhereFoundButNotFoundOnUpdate() {
        
        final Book book = new Book();
        book.setId(Long.valueOf(1));
        
        final BookContainer bookContainer = new BookContainer();
        bookContainer.setBook(new Book());
        bookContainer.setRetrievalOutcome(ERetrievalOutcome.SUCCESS);
        
        Mockito.when(this.mockBookRepository.findById(Mockito.anyLong(), Mockito.anyString())).thenReturn(bookContainer);
        Mockito.when(this.mockBookRepository.update(Mockito.eq(Long.valueOf(1)), Mockito.any(Book.class), Mockito.eq("Nigel"), Mockito.eq(true))).thenReturn(EUpdateOutcome.NOT_FOUND);
        
        final BookUpdateRequest bookUpdateRequest = new BookUpdateRequest();
        bookUpdateRequest.setNewLine("A line.");
        bookUpdateRequest.setPlayerName("Nigel");
        
        final ResponseEntity<BookResource> responseEntity = this.bookController.updateBook(Long.valueOf(1), bookUpdateRequest);
        
        Assert.assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        
        final ArgumentCaptor<Long> argumentCaptorForId = ArgumentCaptor.forClass(Long.class);
        final ArgumentCaptor<String> argumentCaptorForPlayerName = ArgumentCaptor.forClass(String.class);
        
        Mockito.verify(this.mockBookRepository).findById(argumentCaptorForId.capture(), argumentCaptorForPlayerName.capture());
        Mockito.verify(this.mockBookRepository).update(Mockito.eq(Long.valueOf(1)), Mockito.any(Book.class), Mockito.eq("Nigel"), Mockito.eq(true));
        
        Assert.assertEquals(Long.valueOf(1), argumentCaptorForId.getValue());
        Assert.assertEquals("Nigel", argumentCaptorForPlayerName.getValue());
    }
    
    @Test
    public void testUpdateWhereSuccessful() {
        
        final Book book = new Book();
        book.setId(Long.valueOf(1));
        
        final BookContainer bookContainer = new BookContainer();
        bookContainer.setBook(new Book());
        bookContainer.setRetrievalOutcome(ERetrievalOutcome.SUCCESS);
        
        final BookResource bookResource = new BookResource(book);
        
        Mockito.when(this.mockBookRepository.findById(Mockito.anyLong(), Mockito.anyString())).thenReturn(bookContainer);
        Mockito.when(this.mockBookRepository.update(Mockito.eq(Long.valueOf(1)), Mockito.any(Book.class), Mockito.eq("Nigel"), Mockito.eq(true))).thenReturn(EUpdateOutcome.SUCCESS);
        Mockito.when(this.mockBookResourceAssembler.toResource(Mockito.any(Book.class))).thenReturn(bookResource);
        
        final BookUpdateRequest bookUpdateRequest = new BookUpdateRequest();
        bookUpdateRequest.setNewLine("A line.");
        bookUpdateRequest.setPlayerName("Nigel");
        
        final ResponseEntity<BookResource> responseEntity = this.bookController.updateBook(Long.valueOf(1), bookUpdateRequest);
        
        Assert.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        
        final ArgumentCaptor<Long> argumentCaptorForId = ArgumentCaptor.forClass(Long.class);
        final ArgumentCaptor<String> argumentCaptorForPlayerName = ArgumentCaptor.forClass(String.class);
        
        Mockito.verify(this.mockBookRepository).findById(argumentCaptorForId.capture(), argumentCaptorForPlayerName.capture());
        Mockito.verify(this.mockBookRepository).update(Mockito.eq(Long.valueOf(1)), Mockito.any(Book.class), Mockito.eq("Nigel"), Mockito.eq(true));
        Mockito.verify(this.mockBookResourceAssembler).toResource(Mockito.isA(Book.class));
        
        Assert.assertEquals(Long.valueOf(1), argumentCaptorForId.getValue());
        Assert.assertEquals("Nigel", argumentCaptorForPlayerName.getValue());
    }
}