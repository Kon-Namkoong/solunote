package com.vol.solunote.comm.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FFMpegCallException extends Exception {
	
	private static final long serialVersionUID = -3186041448452302821L;
	
	private String file;

	public FFMpegCallException(String file, String message) {
		super(message);
		this.file = file;
	}

	public FFMpegCallException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public FFMpegCallException(String message, Throwable cause) {
		super(message, cause);
	}

	public FFMpegCallException(String message) {
		super(message);
	}

	public FFMpegCallException(Throwable cause) {
		super(cause);
	}

	@Override
	public String toString() {
		return "FFMpegCallException [file=" + file + ", message=" + getMessage() + "]";
	}

}
