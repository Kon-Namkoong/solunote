package com.vol.solunote.Exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SttCallException extends Exception {
	
	private static final long serialVersionUID = 1L;
	private String pgm;

	public SttCallException(String pgm, String message) {
		super(message);
		this.pgm = pgm;
	}

	public SttCallException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public SttCallException(String message, Throwable cause) {
		super(message, cause);
	}

	public SttCallException(String message) {
		super(message);
	}

	public SttCallException(Throwable cause) {
		super(cause);
	}

	@Override
	public String toString() {
		return "SttCallException [pgm=" + pgm + ", message=" + getMessage() + "]";
	}

}

