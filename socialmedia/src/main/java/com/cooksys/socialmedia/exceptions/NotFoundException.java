package com.cooksys.socialmedia.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
@AllArgsConstructor
@Getter
@Setter
public class NotFoundException extends RuntimeException{
    /**
	 * 
	 */
	private static final long serialVersionUID = -7254332651835146037L;
	private String message;
}
