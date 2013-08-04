package org.manaty.security;

import javax.enterprise.inject.Model;
import javax.inject.Inject;

import org.jboss.seam.security.BaseAuthenticator;
import org.jboss.seam.security.Credentials;
import org.jboss.solder.logging.Logger;
import org.manaty.model.account.AccountType;
import org.manaty.model.user.Permission;
import org.manaty.model.user.Role;
import org.manaty.model.user.User;
import org.manaty.service.user.InactiveEntityException;
import org.manaty.service.user.PasswordExpiredException;
import org.manaty.service.user.UnknownUserException;
import org.manaty.service.user.UserService;
import org.omnifaces.util.Messages;
import org.picketlink.idm.impl.api.PasswordCredential;

@Model
public class MeveoAuthenticator extends BaseAuthenticator {

	@Inject
	Credentials credentials;

	@Inject
	private Logger log;

	@Inject
	private UserService userService;

	private boolean expiredPasswordError = false;

	@Override
	public void authenticate() {

		expiredPasswordError = false;

		User user = null;
		try {
			user = userService.loginChecks(credentials.getUsername(),
					((PasswordCredential) credentials.getCredential())
							.getValue());
		} catch (InactiveEntityException e) {
			log.error("login failed with username="
					+ credentials.getUsername()
					+ " and password="
					+ ((PasswordCredential) credentials.getCredential())
							.getValue()
					+ " : cause Business Account or user is not active");
			Messages.addGlobalError("message.login.userInactive",
					new Object[] {});
		} catch (UnknownUserException e) {
			log.error("login failed with username="
					+ credentials.getUsername()
					+ " and password="
					+ ((PasswordCredential) credentials.getCredential())
							.getValue() + " : cause unknown username/password");
			Messages.addGlobalError("message.login.unknownUser",
					new Object[] {});
		} catch (PasswordExpiredException e) {
			log.error("The password of user " + credentials.getUsername()
					+ " has expired.");
			expiredPasswordError = true;
			Messages.addGlobalError("message.login.passwordExpired",
					new Object[] {});
		}

		if (user == null) {
			setStatus(AuthenticationStatus.FAILURE);
		} else {
			setStatus(AuthenticationStatus.SUCCESS);
			setUser(new MeveoUser(user));
			// TODO needed to overcome lazy loading issue. Remove once solved
			for (Role role : user.getRoles()) {
				for (Permission permission : role.getPermissions()) {
					permission.getName();
				}
				for (AccountType type : role.getManageableAccountTypes()) {
					type.getCode();
				}
			}
		}
	}

	public boolean isExpiredPasswordError() {
		return expiredPasswordError;
	}
}
