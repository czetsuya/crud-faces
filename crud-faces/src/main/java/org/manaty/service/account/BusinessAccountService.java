package org.manaty.service.account;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.jboss.seam.security.Identity;
import org.manaty.bean.PaginationConfiguration;
import org.manaty.model.QueryBuilder;
import org.manaty.model.account.AccountType;
import org.manaty.model.account.AccountType.AccountTypeEnum;
import org.manaty.model.account.BusinessAccount;
import org.manaty.model.user.User;
import org.manaty.service.BusinessException;
import org.manaty.service.PersistenceService;
import org.manaty.service.user.UserService;

@Stateless
@Named
public class BusinessAccountService extends PersistenceService<BusinessAccount> {

	private static final long serialVersionUID = -4400082994633797467L;

	@Inject
	Identity identity;

	@Inject
	BusinessAccountConfig businessAccountConfig;

	@Inject
	UserService userService;

	@Override
	public void validateBeforeCreate(BusinessAccount account)
			throws BusinessException {
		super.validateBeforeCreate(account);
		account.setNameForSearch(account.getBusinessName().toUpperCase());
	}

	@Override
	public void validateBeforeUpdate(BusinessAccount account)
			throws BusinessException {
		super.validateBeforeUpdate(account);
		account.setNameForSearch(account.getBusinessName().toUpperCase());
	}

	@SuppressWarnings("unchecked")
	public List<User> getPrincipalContacts(BusinessAccount businessAccount) {
		if (businessAccount != null) {
			return (List<User>) getEntityManager()
					.createQuery(
							"from User as r where r.businessAccount.id=:businessAccountId")
					.setParameter("businessAccountId", businessAccount.getId())
					.getResultList();
		}
		return new ArrayList<User>();
	}

	private QueryBuilder getFindQuery(String name, String code,
			AccountTypeEnum accountTypeFilter, BusinessAccount account,
			AccountType accountType, boolean limitByCurrentUser,
			PaginationConfiguration configuration) {
		log.info("find accounts for accountTypeFilter=" + accountTypeFilter);

		String sql = "select distinct a from BusinessAccount a";

		QueryBuilder qb = new QueryBuilder(sql).cacheable();

		if (accountType != null) {
			qb.addCriterionEntity("a.accountType", accountType);
		} else if (accountTypeFilter != null) {
			qb.addCriterionEnum("a.accountType.accountTypeEnum",
					accountTypeFilter);
		}

		if (account != null) {
			qb.addCriterionEntity("a.parentAccount", account);
		}

		// Adding the username to the criteria of the query if it has not an
		// empty value
		if (!StringUtils.isBlank(name)) {
			name = name.toUpperCase();
			qb.addSqlCriterion("a.nameForSearch lIKE :name", "name", "%" + name
					+ "%");
		}

		if (!StringUtils.isBlank(code)) {
			code = code.toUpperCase();
			qb.addSqlCriterion("a.code lIKE :code", "code", "%" + code + "%");
		}

		if (limitByCurrentUser) {
			addRecordFilteringByCurrentUser(qb, "a");
		}

		qb.addPaginationConfiguration(configuration);

		return qb;
	}

	@SuppressWarnings("unchecked")
	public List<BusinessAccount> find(String name, String code,
			AccountTypeEnum accountTypeFilter, BusinessAccount account,
			AccountType accountType, boolean limitByCurrentUser,
			PaginationConfiguration configuration) {
		return getFindQuery(name, code, accountTypeFilter, account,
				accountType, limitByCurrentUser, configuration).find(
				getEntityManager());
	}

	public long count(String name, String code,
			AccountTypeEnum accountTypeFilter, BusinessAccount account,
			AccountType accountType, boolean limitByCurrentUser) {
		return getFindQuery(name, code, accountTypeFilter, account,
				accountType, limitByCurrentUser, null)
				.count(getEntityManager());
	}

	/**
	 * Get a list of all business accounts, with possibly to filter what user
	 * can see
	 * 
	 * @param accountType
	 * 
	 * @param limitByCurrentUser
	 *            Should list be limited by what current user has access to
	 * @return
	 */
	public List<BusinessAccount> getAllAccounts(AccountType accountType,
			AccountTypeEnum accountTypeFilter, boolean limitByCurrentUser) {
		return find(null, null, accountTypeFilter, null, accountType,
				limitByCurrentUser, null);

	}

	public boolean isExistsCode(String code, Long id) {
		QueryBuilder qb = new QueryBuilder(
				"select count(*) from BusinessAccount").flushModeNEVER();
		qb.addCriterion("code", "=", code, true);
		qb.addSqlCriterion("id!=:id", "id", id);
		return ((Long) qb.getQuery(getEntityManager()).getSingleResult())
				.intValue() != 0;
	}

}