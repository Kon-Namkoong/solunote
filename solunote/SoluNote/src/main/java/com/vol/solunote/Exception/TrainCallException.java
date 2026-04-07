package com.vol.solunote.Exception;

import java.util.HashMap;

import org.springframework.web.client.HttpClientErrorException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vol.solunote.comm.util.MenuSortHandler;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
public class TrainCallException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7460800034004245082L;
	private String url;
	private String status = "";
	private Class<? extends Exception> exClass;
	private String detail;
	
	/*
1.전송된 파일이 오디오 파일이 아닙니다
--   2.파일 이름 확장자와 실제 파일 확장자가 다릅니다
3.음성 파일 길이가 0입니다
::::: 4.음성 파일 길이가 30 초 이상입니다
              음원 파일 길이가 30초 이상이여서 학습시키기에는 너무 깁니다.
5.비어있는 전사 파일입니다. 
6.전사 길이가 너무 깁니다 
	 */
	
	/*
	 * static final class 
HttpClientErrorException.BadRequest
HttpClientErrorException for status HTTP 400 Bad Request.
static final class 
HttpClientErrorException.Conflict
HttpClientErrorException for status HTTP 409 Conflict.
static final class 
HttpClientErrorException.Forbidden
HttpClientErrorException for status HTTP 403 Forbidden.
static final class 
HttpClientErrorException.Gone
HttpClientErrorException for status HTTP 410 Gone.
static final class 
HttpClientErrorException.MethodNotAllowed
HttpClientErrorException for status HTTP 405 Method Not Allowed.
static final class 
HttpClientErrorException.NotAcceptable
HttpClientErrorException for status HTTP 406 Not Acceptable.
static final class 
HttpClientErrorException.NotFound
HttpClientErrorException for status HTTP 404 Not Found.
static final class 
HttpClientErrorException.TooManyRequests
HttpClientErrorException for status HTTP 429 Too Many Requests.
static final class 
HttpClientErrorException.Unauthorized
HttpClientErrorException for status HTTP 401 Unauthorized.
static final class 
HttpClientErrorException.UnprocessableEntity
HttpClientErrorException for status HTTP 422 Unprocessable Entity.
static final class 
HttpClientErrorException.UnsupportedMediaType
HttpClientErrorException for status HTTP 415 Unsupported Media Type.
	 */

	public TrainCallException(Exception e, String url) {
		super(e);
		this.url = url;
		
		this.exClass = e.getClass();
		
		String message = e.getMessage();
		if ( message != null ) {
			String[] ary = message.split("\\s+", 2);
			if ( ary.length > 1 ) {
				this.status = ary[0];
				ObjectMapper om = new ObjectMapper();
				try {
					if ( ary[1] != null ) {
						int start = ary[1].indexOf("{");
						int end = ary[1].lastIndexOf("}");
						if ( start < end ) {
							HashMap<String, Object> map = om.readValue(ary[1].substring(start, end+1),  new TypeReference<HashMap <String, Object>>() {});
							this.detail = (String) map.get("detail");
						}
					}
				} catch (JsonProcessingException e1) {
					log.info("JsonProcessingException in TrainCallException");
				}
			}
		}
		
		if ( this.detail == null ) {
			this.detail = "";
		}
	}

	public TrainCallException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public TrainCallException(String message, Throwable cause) {
		super(message, cause);
	}

	public TrainCallException(String message) {
		super(message);
	}

	public TrainCallException(Throwable cause) {
		super(cause);
	}



	@Override
	public String toString() {
		return "TrainCallException [url=" + url + ", status=" + status + ", message=" + getMessage() + "]";
	}

	public boolean canTrainRepeatable() {
		if ( this.exClass == HttpClientErrorException.BadRequest.class ) {
			if ( detail.startsWith("음원 파일 길이가 30") || 
					detail.startsWith("전사 길이가 너무 깁니다") ) {
				return true;
			}
			
		}
		return false;
	}

}
