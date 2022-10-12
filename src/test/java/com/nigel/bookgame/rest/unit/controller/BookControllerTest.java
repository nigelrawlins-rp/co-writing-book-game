package com.nigel.bookgame.rest.unit.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
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
@ExtendWith(MockitoExtension.class)
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
        
        Assertions.assertThat(responseEntity.getBody()).isEqualTo(listOfBookResources);
        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        
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
        
        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        
        final ArgumentCaptor<Long> argumentCaptorForId = ArgumentCaptor.forClass(Long.class);
        final ArgumentCaptor<String> argumentCaptorForPlayerName = ArgumentCaptor.forClass(String.class);
        
        Mockito.verify(this.mockBookRepository).findById(argumentCaptorForId.capture(), argumentCaptorForPlayerName.capture());
        
        Assertions.assertThat(argumentCaptorForId.getValue()).isEqualTo(Long.valueOf(1));
        Assertions.assertThat(argumentCaptorForPlayerName.getValue()).isEqualTo("Nigel");
    }
    
    @Test
    public void testFindBookByIdWhereLockedByAnotherPlayer() {
        
        final BookContainer bookContainer = new BookContainer();
        bookContainer.setBook(null);
        bookContainer.setRetrievalOutcome(ERetrievalOutcome.LOCKED_BY_ANOTHER_PLAYER);
        
        Mockito.when(this.mockBookRepository.findById(Mockito.anyLong(), Mockito.anyString())).thenReturn(bookContainer);
        
        final ResponseEntity<BookResource> responseEntity = this.bookController.findBookById(Long.valueOf(1), "Nigel");
        
        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.LOCKED);
        
        final ArgumentCaptor<Long> argumentCaptorForId = ArgumentCaptor.forClass(Long.class);
        final ArgumentCaptor<String> argumentCaptorForPlayerName = ArgumentCaptor.forClass(String.class);
        
        Mockito.verify(this.mockBookRepository).findById(argumentCaptorForId.capture(), argumentCaptorForPlayerName.capture());
        
        Assertions.assertThat(argumentCaptorForId.getValue()).isEqualTo(Long.valueOf(1));
        Assertions.assertThat(argumentCaptorForPlayerName.getValue()).isEqualTo("Nigel");
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
        
        Assertions.assertThat(responseEntity.getBody()).isEqualTo(bookResource);
        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        
        final ArgumentCaptor<Long> argumentCaptorForId = ArgumentCaptor.forClass(Long.class);
        final ArgumentCaptor<String> argumentCaptorForPlayerName = ArgumentCaptor.forClass(String.class);
        
        Mockito.verify(this.mockBookRepository).findById(argumentCaptorForId.capture(), argumentCaptorForPlayerName.capture());
        Mockito.verify(this.mockBookResourceAssembler).toResource(bookContainer.getBook());
        
        Assertions.assertThat(argumentCaptorForId.getValue()).isEqualTo(Long.valueOf(1));
        Assertions.assertThat(argumentCaptorForPlayerName.getValue()).isEqualTo("Nigel");
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
        
        Assertions.assertThat(responseEntity.getBody()).isEqualTo(bookResource);
        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        
        Mockito.verify(this.mockBookRepository).create(Mockito.isA(Book.class));
        Mockito.verify(this.mockBookResourceAssembler).toResource(book);
    }
    
    @Test
    public void testUpdateWherePlayerNameNull() {
        
        final BookUpdateRequest bookUpdateRequest = new BookUpdateRequest();
        bookUpdateRequest.setNewLine("A line.");
        bookUpdateRequest.setPlayerName(null);
        
        final ResponseEntity<BookResource> responseEntity = this.bookController.updateBook(Long.valueOf(1), bookUpdateRequest);
        
        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
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
        
        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        
        final ArgumentCaptor<Long> argumentCaptorForId = ArgumentCaptor.forClass(Long.class);
        final ArgumentCaptor<String> argumentCaptorForPlayerName = ArgumentCaptor.forClass(String.class);
        
        Mockito.verify(this.mockBookRepository).findById(argumentCaptorForId.capture(), argumentCaptorForPlayerName.capture());
        
        Assertions.assertThat(argumentCaptorForId.getValue()).isEqualTo(Long.valueOf(1));
        Assertions.assertThat(argumentCaptorForPlayerName.getValue()).isEqualTo("Nigel");
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
        
        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.LOCKED);
        
        final ArgumentCaptor<Long> argumentCaptorForId = ArgumentCaptor.forClass(Long.class);
        final ArgumentCaptor<String> argumentCaptorForPlayerName = ArgumentCaptor.forClass(String.class);
        
        Mockito.verify(this.mockBookRepository).findById(argumentCaptorForId.capture(), argumentCaptorForPlayerName.capture());
        
        Assertions.assertThat(argumentCaptorForId.getValue()).isEqualTo(Long.valueOf(1));
        Assertions.assertThat(argumentCaptorForPlayerName.getValue()).isEqualTo("Nigel");
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
        
        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        
        final ArgumentCaptor<Long> argumentCaptorForId = ArgumentCaptor.forClass(Long.class);
        final ArgumentCaptor<String> argumentCaptorForPlayerName = ArgumentCaptor.forClass(String.class);
        
        Mockito.verify(this.mockBookRepository).findById(argumentCaptorForId.capture(), argumentCaptorForPlayerName.capture());
        Mockito.verify(this.mockBookRepository).update(Mockito.eq(Long.valueOf(1)), Mockito.any(Book.class), Mockito.eq("Nigel"), Mockito.eq(true));
        
        Assertions.assertThat(argumentCaptorForId.getValue()).isEqualTo(Long.valueOf(1));
        Assertions.assertThat(argumentCaptorForPlayerName.getValue()).isEqualTo("Nigel");
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
        
        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.LOCKED);
        
        final ArgumentCaptor<Long> argumentCaptorForId = ArgumentCaptor.forClass(Long.class);
        final ArgumentCaptor<String> argumentCaptorForPlayerName = ArgumentCaptor.forClass(String.class);
        
        Mockito.verify(this.mockBookRepository).findById(argumentCaptorForId.capture(), argumentCaptorForPlayerName.capture());
        Mockito.verify(this.mockBookRepository).update(Mockito.eq(Long.valueOf(1)), Mockito.any(Book.class), Mockito.eq("Nigel"), Mockito.eq(true));
        
        Assertions.assertThat(argumentCaptorForId.getValue()).isEqualTo(Long.valueOf(1));
        Assertions.assertThat(argumentCaptorForPlayerName.getValue()).isEqualTo("Nigel");
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
        
        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        
        final ArgumentCaptor<Long> argumentCaptorForId = ArgumentCaptor.forClass(Long.class);
        final ArgumentCaptor<String> argumentCaptorForPlayerName = ArgumentCaptor.forClass(String.class);
        
        Mockito.verify(this.mockBookRepository).findById(argumentCaptorForId.capture(), argumentCaptorForPlayerName.capture());
        Mockito.verify(this.mockBookRepository).update(Mockito.eq(Long.valueOf(1)), Mockito.any(Book.class), Mockito.eq("Nigel"), Mockito.eq(true));
        
        Assertions.assertThat(argumentCaptorForId.getValue()).isEqualTo(Long.valueOf(1));
        Assertions.assertThat(argumentCaptorForPlayerName.getValue()).isEqualTo("Nigel");
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
        
        Assertions.assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
        
        final ArgumentCaptor<Long> argumentCaptorForId = ArgumentCaptor.forClass(Long.class);
        final ArgumentCaptor<String> argumentCaptorForPlayerName = ArgumentCaptor.forClass(String.class);
        
        Mockito.verify(this.mockBookRepository).findById(argumentCaptorForId.capture(), argumentCaptorForPlayerName.capture());
        Mockito.verify(this.mockBookRepository).update(Mockito.eq(Long.valueOf(1)), Mockito.any(Book.class), Mockito.eq("Nigel"), Mockito.eq(true));
        Mockito.verify(this.mockBookResourceAssembler).toResource(Mockito.isA(Book.class));
        
        Assertions.assertThat(argumentCaptorForId.getValue()).isEqualTo(Long.valueOf(1));
        Assertions.assertThat(argumentCaptorForPlayerName.getValue()).isEqualTo("Nigel");
    }
}