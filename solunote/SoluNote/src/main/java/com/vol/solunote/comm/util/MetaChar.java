package com.vol.solunote.comm.util;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MetaChar {

	private Character c;
	private boolean b;
	
	public MetaChar(Character c) {
		super();
		this.c = c;
	}
	
	public MetaChar(Character c, boolean b) {
		super();
		this.c = c;
		this.b = b;
	}
	

}
