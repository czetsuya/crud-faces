package org.manaty.service.account;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

import org.jboss.seam.security.Identity;
import org.manaty.model.account.AccountType;
import org.manaty.security.MeveoUser;
import org.manaty.util.MeveoJpa;

@SessionScoped
@Named
public class BusinessAccountConfig implements Serializable {

	private static final long serialVersionUID = -4715566072433589266L;

	@Inject
	@MeveoJpa
	EntityManager entityManager;

	@Inject
	Identity identity;

	private List<AccountType> accountTypes;

	@SuppressWarnings("unchecked")
	@PostConstruct
	private void init() {
		accountTypes = entityManager.createQuery(
				"select at from AccountType at").getResultList();
	}

	public AccountType getAccountType(Long id) {
		for (AccountType type : accountTypes) {
			if (type.getId().longValue() == id.longValue()) {
				return type;
			}
		}
		return null;
	}

	/**
	 * Get a list of account types, optionally only ones that user can manage
	 * 
	 * @param limitByCurrentUser
	 *            Should account types be filtered by a user
	 * @return
	 */
	public List<AccountType> getAccountTypes(boolean limitByCurrentUser) {

		if (!limitByCurrentUser) {
			return accountTypes;

		} else {
			Set<AccountType> types = ((MeveoUser) identity.getUser()).getUser()
					.getManageableAccountTypes();
			return Arrays.asList(types.toArray(new AccountType[] {}));
		}
	}

	/**
	 * Get a list of account types that user can create - same as manage minus
	 * own account type
	 * 
	 * @param limitByCurrentUser
	 *            Should account types be filtered by a user
	 * @return
	 */
	public List<AccountType> getAccountTypesForCreate() {

		List<AccountType> types = getAccountTypes(true);
		if (((MeveoUser) identity.getUser()).getUser().getBusinessAccount() != null) {
			List<AccountType> filteredTypes = new ArrayList<AccountType>();
			AccountType userAccountType = ((MeveoUser) identity.getUser())
					.getUser().getBusinessAccount().getAccountType();

			for (AccountType accountType : types) {
				if (!accountType.equals(userAccountType)) {
					filteredTypes.add(accountType);
				}
			}
			return filteredTypes;
		}
		return types;
	}
}