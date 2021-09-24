package com.dee.blog_rest.exceptions;

import com.dee.blog_rest.requests_and_responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequestException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	private ApiResponse apiResponse;
	private HttpStatus httpStatus;
	private String message;

	public BadRequestException(ApiResponse apiResponse) {
//		super();super
		this.apiResponse = apiResponse;
	}

	public BadRequestException(HttpStatus httpStatus, String message){
		this.httpStatus = httpStatus;
		this.message = message;
	}

	public BadRequestException(String message) {
		super(message);
	}

	public BadRequestException(String message, Throwable cause) {
		super(message, cause);
	}

	public ApiResponse getApiResponse() {
		return apiResponse;
	}


}
