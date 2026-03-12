package com.vol.solunote.comm;

import org.springframework.data.domain.Pageable;

public class OffsetPageable {
	
	private Pageable pageable;

	public OffsetPageable(Pageable pageable) {
		this.pageable = pageable;
	}

	public int getOffset() {
		return (this.pageable.getPageNumber() - 1 ) * this.pageable.getPageSize();
	}
	
	public long getPageSize() {
		return this.pageable.getPageSize();
	}
	
	public long getPageNumber() {
		return this.pageable.getPageNumber();
	}

}
