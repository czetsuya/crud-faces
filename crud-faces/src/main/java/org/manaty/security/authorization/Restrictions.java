package org.manaty.security.authorization;

import org.jboss.seam.security.Identity;
import org.jboss.seam.security.annotations.Secures;

public class Restrictions {

	@Secures
	@UserManager
	public boolean isUserManager(Identity identity) {
		return identity.hasPermission("user", "manage");
	}
	
}
