package com.vol.solunote.comm.model;

public class SoundException extends Exception {

	private static final long serialVersionUID = -8203853742380080586L;

	public SoundException() {
		super();
	}

	public SoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public SoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public SoundException(String message) {
		super(message);
	}

	public SoundException(Throwable cause) {
		super(cause);
	}

}
