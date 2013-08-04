/*
\ * (C) Copyright 2009-2013 Manaty SARL (http://manaty.net/) and contributors.
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

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.security.auth.login.LoginException;

import org.apache.commons.lang3.StringUtils;
import org.manaty.bean.BaseDetailBean;
import org.manaty.model.user.Role;
import org.manaty.model.user.User;
import org.manaty.service.BusinessException;
import org.manaty.service.user.RoleService;
import org.manaty.service.user.UserService;
import org.manaty.util.Sha1Encrypt;
import org.omnifaces.util.Messages;
import org.primefaces.model.DualListModel;

/**
 * @author Edward P. Legaspi
 * @since Jun 5, 2013
 **/
@Named
@ConversationScoped
public class UserBean extends BaseDetailBean<User, UserService> {

	private static final long serialVersionUID = 6331738501556309760L;

	@Inject
	private RoleService roleService;

	private DualListModel<Role> listOfRoles;

	private String currentPassword;

	public UserBean() {
		super(User.class, UserService.class);
	}

	public List<Role> getDualListSource() {
		List<Role> source = roleService.list();
		return source;
	}

	public DualListModel<Role> getDualListModel() {
		if (listOfRoles == null) {
			List<Role> source = roleService.list();
			List<Role> target = new ArrayList<Role>();
			if (getEntity().getRoles() != null) {
				target.addAll(getEntity().getRoles());
			}
			source.removeAll(target);
			listOfRoles = new DualListModel<Role>(source, target);
		}
		return listOfRoles;
	}

	public void setDualListModel(DualListModel<Role> listOfRoles) {
		entity.setRoles(new ArrayList<Role>((List<Role>) listOfRoles
				.getTarget()));
	}

	public String changePassword() throws LoginException, BusinessException {

		User currentUser = getEntity();
		if (currentUser.getRoles().contains(roleService.findByName("admin"))) {

			try {

				persistenceService.changePassword(currentUser, getEntity()
						.getNewPassword());
				Messages.addGlobalInfo(
						"message.changePassword.passwordChanged",
						new Object[] {});

			} catch (org.manaty.service.BusinessException e) {
				Messages.addGlobalError("message.generic.error",
						new Object[] {});
				e.printStackTrace();
			}

		} else {
			if (currentPassword != null) {
				if (validate(currentUser)) {
					try {
						if (currentPassword != getEntity().getNewPassword()) {
							persistenceService.changePassword(currentUser,
									getEntity().getNewPassword());
							Messages.addGlobalInfo(
									"message.changePassword.passwordChanged",
									new Object[] {});
						}
					} catch (org.manaty.service.BusinessException e) {
						Messages.addGlobalError("message.generic.error",
								new Object[] {});
						e.printStackTrace();
					}
				}
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

	public boolean showPasswordField() {
		if (getEntity().getId() == null) {
			return true;
		}
		return false;
	}

	public Boolean isUserAdmin() {
		if (getEntity().getRoles().contains(roleService.findByName("admin"))) {
			return true;
		}
		return false;
	}

}
