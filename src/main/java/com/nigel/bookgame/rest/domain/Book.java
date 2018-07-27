package com.nigel.bookgame.rest.domain;

import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Domain logic class representing a book.
 * 
 * @author nigel
 */
public class Book implements Identifiable {

    private static final Short MAXIMUM_BOOK_LENGTH = 5;

	private Long id;
	private String name;
	private SortedMap<Integer, LineDetail> lineDetailMap = new TreeMap<Integer, LineDetail>();
	private boolean isComplete = false;
	private String lockedBy;

	@Override
	public Long getId() {
		return this.id;
	}

	@Override
	public void setId(final Long id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public SortedMap<Integer, LineDetail> getLineDetailMap() {

	    final SortedMap<Integer, LineDetail> lineDetailMapCopy = new TreeMap<Integer, LineDetail>();

	    for (Integer mapKey : this.lineDetailMap.keySet()) {
	        lineDetailMapCopy.put(mapKey, new LineDetail(this.lineDetailMap.get(mapKey)));
	    }

	    return lineDetailMapCopy;
	}

	public boolean addLineToLineDetailMap(final String line, final String playerName) {

	    if (isComplete()) {
	        return false;
	    }

	    Integer currentMaximumLineNumber = 0;

	    if (!this.lineDetailMap.isEmpty()) {
	        currentMaximumLineNumber = this.lineDetailMap.lastKey();
	    }

	    this.lineDetailMap.put(currentMaximumLineNumber + 1, new LineDetail(line, playerName));

	    if (MAXIMUM_BOOK_LENGTH == this.lineDetailMap.size()) {
	        setComplete(true);
	    }

	    return true;
	}

    public boolean isComplete() {
        return this.isComplete;
    }

	public void setComplete(final boolean isComplete) {
		this.isComplete = isComplete;
	}

    public String getLockedBy() {
        return this.lockedBy;
    }

    public synchronized boolean lock(final String playerName) {

        boolean successfully_locked = false;

        if (null == this.lockedBy) {
            this.lockedBy = playerName;
            successfully_locked = true;
        }

        return successfully_locked;
    }

    public void unlock() {
        this.lockedBy = null;
    }

    @Override
    public boolean equals(final Object object) {

        if (!(object instanceof Book)) {
            return false;
        }

        return ((Book) object).getId().equals(this.id);
    }

    @Override
    public int hashCode() {
        return this.id.toString().hashCode();
    }
}