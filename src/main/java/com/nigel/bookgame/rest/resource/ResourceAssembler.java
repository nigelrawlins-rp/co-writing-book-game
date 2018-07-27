package com.nigel.bookgame.rest.resource;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Abstract superclass of resource assemblers.
 * 
 * @author nigel
 *
 * @param <T> The type of the domain object.
 * @param <R> The type of the resource.
 */
public abstract class ResourceAssembler<T, R> {
	
	public abstract R toResource(T domainObject);

	public Collection<R> toResourceCollection(Collection<T> domainObjects) {
		return domainObjects.stream().map(d -> toResource(d)).collect(Collectors.toList());
	}
}