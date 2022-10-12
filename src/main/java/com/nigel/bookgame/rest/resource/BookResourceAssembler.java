package com.nigel.bookgame.rest.resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.server.EntityLinks;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;

import com.nigel.bookgame.rest.domain.Book;

/**
 * Resource assembler for a book which enhances the resource to be exposed with HATEOAS links for self and update.
 * 
 * @author NRawli01
 */
@Component
public class BookResourceAssembler extends ResourceAssembler<Book, BookResource> {
	
	@Autowired
	protected EntityLinks entityLinks;
	
	@Override
	public BookResource toResource(final Book book) {
        
        final Link selfLink = this.entityLinks.linkToItemResource(book, b -> b.getId());
		
		final BookResource bookResource = new BookResource(book);
		bookResource.add(selfLink.withSelfRel());
		bookResource.add(selfLink.withRel("update"));
		
		return bookResource;
	}
}