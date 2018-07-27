package com.nigel.bookgame.rest.repository;

import java.util.concurrent.atomic.AtomicLong;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * ID generator for domain objects.
 * 
 * @author nigel
 */
@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class IdGenerator {
    
	private AtomicLong nextId = new AtomicLong(1);
	
	public long getNextId() {
		return this.nextId.getAndIncrement();
	}
}