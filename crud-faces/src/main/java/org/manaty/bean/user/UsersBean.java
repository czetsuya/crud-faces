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
import javax.persistence.EntityExistsException;

import org.jboss.seam.security.Identity;
import org.manaty.bean.BaseListBean;
import org.manaty.model.account.AccountType.AccountTypeEnum;
import org.manaty.model.account.BusinessAccount;
import org.manaty.model.user.User;
import org.manaty.security.MeveoUser;
import org.manaty.service.user.UserService;
import org.omnifaces.util.Messages;

/**
 * @author Edward P. Legaspi
 * @since Jun 5, 2013
 **/
@Named
@ConversationScoped
public class UsersBean extends BaseListBean<User, UserService> {

	private static final long serialVersionUID = 1L;

	@Inject
	private Identity identity;

	private BusinessAccount account;
	private AccountTypeEnum accountTypeFilter;

	public UsersBean() {
		super(User.class, UserService.class);
	}

	public AccountTypeEnum getAccountTypeFilter() {
		return accountTypeFilter;
	}

	public void setAccountTypeFilter(AccountTypeEnum accountTypeFilter) {
		this.accountTypeFilter = accountTypeFilter;
	}

	public BusinessAccount getAccount() {
		return account;
	}

	public void setAccount(BusinessAccount account) {
		this.account = account;
	}

	@Override
	public void delete() {

		try {
			if (Integer.parseInt(((MeveoUser) identity.getUser()).getUser()
					.getId().toString()) == Integer.parseInt(getEntity()
					.getId().toString())) {
				Messages.addGlobalError(
						"You cannot delete a user which is currently logged in.",
						new Object[] {});
			} else if (Integer.parseInt(((MeveoUser) identity.getUser())
					.getUser().getId().toString()) != Integer
					.parseInt(getEntity().getId().toString())) {
				log.debug(String.format("Deleting entity %s with id = %s",
						entityClass.getName(), entity.getId()));
				persistenceService.remove((Long) entity.getId());
				Messages.addGlobalInfo("message.entity.delete.ok",
						new Object[] {});
			}
		} catch (Throwable t) {
			if (t.getCause() instanceof EntityExistsException) {
				log.error(
						"delete was unsuccessful because entity is used in the system",
						t);
				Messages.addGlobalError("message.entity.delete.entityUse	",
						new Object[] {});

			} else {
				log.error("unexpected exception when deleting!", t);
				Messages.addGlobalError("message.entity.delete.unexpected",
						new Object[] {});
			}
		}

	}
}
