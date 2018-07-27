package com.nigel.bookgame.rest.domain;

/**
 * Interface to be implemented by domain objects which need to be identifiable via a unique ID.
 * 
 * @author nigel
 */
public interface Identifiable extends org.springframework.hateoas.Identifiable<Long> {
    
	public void setId(Long id);
}