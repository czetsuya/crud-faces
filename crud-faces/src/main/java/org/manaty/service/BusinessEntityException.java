package org.manaty.service;

public class BusinessEntityException extends BusinessException {

	private static final long serialVersionUID = 1L;

	private String id;
	
	public BusinessEntityException() {
		super();
	}

	public BusinessEntityException(String message, Throwable cause) {
		super(message, cause);
	}

	public BusinessEntityException(String message) {
		super(message);
	}

	public BusinessEntityException(Throwable cause) {
		super(cause);
	}

	public BusinessEntityException(String message, String id,Throwable cause) {
		super(message, cause);
		this.id=id;
	}

	public BusinessEntityException(String message, String id) {
		super(message);
		this.id=id;
	}

	public BusinessEntityException(Throwable cause, String id) {
		super(cause);
		this.id=id;
	}

	public String getId() {
		return id;
	}
}
