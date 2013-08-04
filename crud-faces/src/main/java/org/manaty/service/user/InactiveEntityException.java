package org.manaty.service.user;

import org.manaty.service.BusinessException;

public class InactiveEntityException extends BusinessException {

	private static final long serialVersionUID = 1L;

	@SuppressWarnings("rawtypes")
	public InactiveEntityException(Class clazz, Long id) {
		super("Inactive " + clazz.getSimpleName() + " id:" + id);
	}

}
