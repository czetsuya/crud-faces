package org.manaty.service.user;

import org.manaty.model.user.User;
import org.manaty.service.BusinessException;

/**
 * Exception thrown when {@link User} entity does not exist
 * 
 */
public class UnknownUserException extends BusinessException {

	/** */
	private static final long serialVersionUID = 1L;

	public UnknownUserException(Long id) {
		super("User with id='" + id + "' does not  exist");
	}

}
