package org.manaty.service.user;

import org.manaty.service.BusinessEntityException;

public class UsernameAlreadyExistsException extends BusinessEntityException {

	private static final long serialVersionUID = 1L;

	public UsernameAlreadyExistsException(String username) {
		super(username);
	}

	public UsernameAlreadyExistsException() {
	}
}
