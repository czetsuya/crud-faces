/*
 * (C) Copyright 2009-2013 Manaty SARL (http://manaty.net/) and contributors.
 *
 * Licensed under the GNU Public Licence, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.gnu.org/licenses/gpl-2.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.manaty.bean.user;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.security.auth.login.LoginException;

import org.apache.commons.lang3.StringUtils;
import org.jboss.seam.security.Identity;
import org.manaty.bean.BaseDetailBean;
import org.manaty.model.user.User;
import org.manaty.security.MeveoUser;
import org.manaty.service.BusinessException;
import org.manaty.service.user.InactiveEntityException;
import org.manaty.service.user.PasswordExpiredException;
import org.manaty.service.user.UnknownUserException;
import org.manaty.service.user.UserService;
import org.manaty.util.Sha1Encrypt;
import org.omnifaces.util.Messages;
import org.slf4j.Logger;

@Named
@ConversationScoped
public class ChangePasswordBean extends BaseDetailBean<User, UserService> {

	private static final long serialVersionUID = -9093923845628912824L;

	@Inject
	protected Logger log;

	@Inject
	private Identity identity;

	@Inject
	private UserService userService;

	private String username;

	private String currentPassword;

	public ChangePasswordBean() {
		super(User.class, UserService.class);
	}

	public String update() throws LoginException, BusinessException {

		User currentUser = null;

		if (currentPassword != null) {
			if (identity.isLoggedIn()) {
				currentUser = userService.findById(((MeveoUser) identity
						.getUser()).getUser().getId());
			} else {
				try {
					currentUser = userService.loginChecks(username,
							currentPassword, true);
				} catch (InactiveEntityException e) {
					Messages.addGlobalError("message.login.userInactive",
							new Object[] {});
				} catch (UnknownUserException e) {
					Messages.addGlobalError("message.login.unknownUser",
							new Object[] {});
				} catch (PasswordExpiredException e) {
					Messages.addGlobalError("message.login.passwordExpired",
							new Object[] {});
				}
			}
			if (validate(currentUser)) {
				try {
					if (currentPassword != getEntity().getNewPassword()) {
						userService.changePassword(currentUser, getEntity()
								.getNewPassword());
						Messages.addGlobalInfo(
								"message.changePassword.passwordChanged",
								new Object[] {});
					}
				} catch (org.manaty.service.BusinessException e) {
					Messages.addGlobalError("message.generic.error",
							new Object[] {});
					e.printStackTrace();
				}
				return null;
			}
		}
		return null;
	}

	private boolean validate(User currentUser) {
		if (!Sha1Encrypt.encodePassword(currentPassword).equals(
				currentUser.getPassword())) {
			Messages.addGlobalError(
					"message.changePassword.currentPasswordIncorrect",
					new Object[] {});
			return false;
		}
		if (Sha1Encrypt.encodePassword(getEntity().getNewPassword()).equals(
				currentUser.getPassword())) {
			Messages.addGlobalError(
					"message.changePassword.passwordMustBeDifferent",
					new Object[] {});
			return false;
		}
		if (!StringUtils.equals(getEntity().getNewPassword(), getEntity()
				.getNewPasswordConfirmation())) {
			Messages.addGlobalError(
					"message.changePassword.newPasswordMustBeEqualToConfirm",
					new Object[] {});
			return false;
		}
		return true;
	}

	public String getCurrentPassword() {
		return currentPassword;
	}

	public void setCurrentPassword(String currentPassword) {
		this.currentPassword = currentPassword;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

}