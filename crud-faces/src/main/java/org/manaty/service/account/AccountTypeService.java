package org.manaty.service.account;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.manaty.model.QueryBuilder;
import org.manaty.model.account.AccountType;
import org.manaty.model.account.AccountType.AccountTypeEnum;
import org.manaty.util.MeveoJpa;

public class AccountTypeService implements Serializable {

	/**
     * 
     */
	private static final long serialVersionUID = -4100035764664765L;

	@Inject
	@MeveoJpa
	EntityManager em;

	@SuppressWarnings("unchecked")
	public AccountType getAccountType(AccountTypeEnum accountType) {

		String sql = "select distinct a from AccountType a";

		QueryBuilder qb = new QueryBuilder(sql).cacheable();

		if (accountType != null) {
			qb.addCriterionEntity("a.accountTypeEnum", accountType);
		}

		List<AccountType> accountTypes = qb.find(em);
		return accountTypes.size() > 0 ? accountTypes.get(0) : null;
	}

}
