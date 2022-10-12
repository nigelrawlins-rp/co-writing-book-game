package com.nigel.bookgame.rest.resource;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;

import org.springframework.hateoas.RepresentationModel;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nigel.bookgame.rest.domain.Book;
import com.nigel.bookgame.rest.domain.LineDetail;

/**
 * Resource object representation of a book which captures what should be presented to the caller.
 * 
 * @author nigel
 */
public class BookResource extends RepresentationModel<BookResource> {
    
	private final long id;
	private final String name;
	private final List<String> lines;
	private final boolean isComplete;
	
	public BookResource(final Book book) {
		this.id = book.getId();
		this.name = book.getName();
		this.lines = determineLinesToExpose(book);
		this.isComplete = book.isComplete();
	}
	
	private List<String> determineLinesToExpose(final Book book) {
        
	    final List<String> linesToExpose = new ArrayList<String>();
	    
	    final SortedMap<Integer, LineDetail> lineDetailMap = book.getLineDetailMap();
	    
	    if (book.isComplete()) {
	        addAllLines(linesToExpose, lineDetailMap);
	    }
	    else {
	        addLastLine(linesToExpose, lineDetailMap);
	    }
	    
	    return linesToExpose;
    }
	
    private void addAllLines(final List<String> linesToExpose, final SortedMap<Integer, LineDetail> lineDetailMap) {
        for (Integer mapKey : lineDetailMap.keySet()) {
            linesToExpose.add(lineDetailMap.get(mapKey).getLine());
        }
    }
    
    private void addLastLine(final List<String> linesToExpose, final SortedMap<Integer, LineDetail> lineDetailMap) {
        if (!lineDetailMap.isEmpty()) {
            linesToExpose.add(lineDetailMap.get(lineDetailMap.lastKey()).getLine());
        }
    }
	
    @JsonProperty("id")
	public Long getResourceId() {
		return this.id;
	}
	
	public String getName() {
		return this.name;
	}
	
	public List<String> getLines() {
	    return this.lines;
	}
	
	public boolean isComplete() {
		return this.isComplete;
	}
}