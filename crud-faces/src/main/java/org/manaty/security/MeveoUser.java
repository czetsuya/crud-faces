package org.manaty.security;

import java.io.Serializable;

import org.picketlink.idm.api.User;

public class MeveoUser implements User, Serializable {

	private static final long serialVersionUID = 4333140556503076034L;

	private org.manaty.model.user.User user;

	public MeveoUser(org.manaty.model.user.User user) {
		this.user = user;
	}

	@Override
	public String getKey() {
		return getId();
	}

	@Override
	public String getId() {
		return user.getUsername();
	}

	public org.manaty.model.user.User getUser() {
		return this.user;
	}
}