package com.vol.solunote.model.vo.comm;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorShelf {

	private int code;
	private String message;
	private String desc;
	
	public ErrorShelf(int code, String message) {
		this.code = code;
		this.message = message;
	}

	public ErrorShelf(int code) {
		this.code = code;
	}

}
