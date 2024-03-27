package com.cooksys.socialmedia.exceptions;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
@AllArgsConstructor
@Getter
@Setter
public class BadRequestException extends RuntimeException {
    /**
	 *
	 */
	private static final long serialVersionUID = -2422766134517844127L;
	private String message;
}
