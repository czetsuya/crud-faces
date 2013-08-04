package org.manaty.service.user;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.UUID;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;

import org.apache.commons.lang3.StringUtils;
import org.manaty.bean.PaginationConfiguration;
import org.manaty.model.QueryBuilder;
import org.manaty.model.account.AccountType;
import org.manaty.model.account.AccountType.AccountTypeEnum;
import org.manaty.model.account.BusinessAccount;
import org.manaty.model.account.Name;
import org.manaty.model.user.Role;
import org.manaty.model.user.User;
import org.manaty.security.authorization.UserManager;
import org.manaty.service.BusinessException;
import org.manaty.service.PersistenceService;
import org.manaty.util.Sha1Encrypt;

@Stateless
public class UserService extends PersistenceService<User> {

	private static final long serialVersionUID = 7797841046747270540L;

	public void create(User user, String appUrl) throws BusinessException {

		if (isExistsUsername(user.getUsername(), user.getId())) {
			getEntityManager().refresh(user);
			throw new UsernameAlreadyExistsException(user.getUsername());
		}

		user.setUsername(user.getUsername().toLowerCase());
		String encryptedPassword = Sha1Encrypt.encodePassword(user
				.getNewPassword());
		user.setPassword(encryptedPassword);
		user.setLastPasswordModification(new Date());
		user.setDisabled(false);

		super.create(user);
	}

	@Override
	public void update(User user) throws BusinessException {
		if (user.isNewPasswordSet()) {
			String encryptedPassword = Sha1Encrypt.encodePassword(user
					.getNewPassword());
			user.setPassword(encryptedPassword);
			user.setLastPasswordModification(new Date());
		}

		super.update(user);
	}

	@Override
	@UserManager
	public void remove(User user) {

		user.getRoles().clear();
		super.remove(user);
	}

	private QueryBuilder getFindQuery(String username, String lastName,
			AccountTypeEnum accountTypeFilter, BusinessAccount account,
			Role role, boolean limitByCurrentUser,
			PaginationConfiguration configuration) {
		log.info("find users for accountTypeFilter=" + accountTypeFilter);

		String sql = "select distinct u from User u";

		if (role != null) {
			sql = sql + ", IN (u.roles) r ";
		}

		QueryBuilder qb = new QueryBuilder(sql).cacheable();

		if (role != null) {
			qb.addCriterionEntity("r", role);
		}

		if (accountTypeFilter != null) {
			qb.addCriterionEnum(
					"u.businessAccount.accountType.accountTypeEnum",
					accountTypeFilter);
		}

		if (account != null) {
			qb.addCriterionEntity("u.businessAccount", account);
		}

		if (limitByCurrentUser) {
			addRecordFilteringByCurrentUser(qb, "u.businessAccount");
		}

		// Adding the username to the criteria of the query if it has not an
		// empty value
		if (!StringUtils.isBlank(username)) {
			username = username.toLowerCase();
			qb.addSqlCriterion("u.username LIKE :username", "username", "%"
					+ username + "%");
		}

		if (!StringUtils.isBlank(lastName)) {
			lastName = lastName.toLowerCase();
			qb.addSqlCriterion("u.name.lastName LIKE :lastName", "lastName",
					"%" + lastName + "%");
		}

		qb.addPaginationConfiguration(configuration);

		return qb;
	}

	@SuppressWarnings("unchecked")
	public List<User> find(String username, String lastName,
			AccountTypeEnum accountTypeFilter, BusinessAccount account,
			Role role, boolean limitByCurrentUser,
			PaginationConfiguration configuration) {
		return getFindQuery(username, lastName, accountTypeFilter, account,
				role, limitByCurrentUser, configuration).find(
				getEntityManager());
	}

	public long count(String username, String lastName,
			AccountTypeEnum accountTypeFilter, BusinessAccount account,
			Role role, boolean limitByCurrentUser) {
		return getFindQuery(username, lastName, accountTypeFilter, account,
				role, limitByCurrentUser, null).count(getEntityManager());
	}

	@SuppressWarnings("unchecked")
	public List<User> findUsersByRoles(String... roles) {

		QueryBuilder qb = new QueryBuilder(
				"select distinct u from User u join u.roles as r ")
				.cacheable()
				.flushModeNEVER()
				.addSqlCriterion("r.name in (:roles)", "roles",
						Arrays.asList(roles));
		return qb.getQuery(getEntityManager()).getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<User> findByBusinessAccount(Long accountId) {
		QueryBuilder qb = new QueryBuilder(User.class, "u").cacheable()
				.addCriterion("u.businessAccount.id", "=", accountId, true);
		return qb.getQuery(getEntityManager()).getResultList();

	}

	public boolean isExistsUsername(String username, Long id) {
		QueryBuilder qb = new QueryBuilder("select count(*) from User")
				.flushModeNEVER();
		qb.addCriterion("username", "=", username.toLowerCase(), true);
		qb.addSqlCriterion("id!=:id", "id", id);
		return ((Long) qb.getQuery(getEntityManager()).getSingleResult())
				.intValue() != 0;
	}

	public Role findRoleByName(String name) {
		QueryBuilder qb = new QueryBuilder(Role.class, "r").cacheable()
				.addSqlCriterion("r.name = :role", "role", name);
		try {
			return (Role) qb.getQuery(getEntityManager()).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	// /**
	// * Find all roles. If there is Party and its level has roles then return
	// level roles. If there is party, but no roles in level, then return roles
	// that starts with 'party'
	// * prefix. If no party it return all roles that DO NOT start with 'party'
	// prefix.
	// */
	// @SuppressWarnings("unchecked")
	// public List<Role> findAvailableRoles(Party party) {
	// QueryBuilder qb = new QueryBuilder(Role.class, "r").cacheable();
	// List<Role> result = qb.getQuery(em).getResultList();
	//
	// // Remove automatically the role 'admin' if the current user is not an
	// // administrator
	// if (!Identity.instance().hasRole("admin"))
	// for (Iterator<Role> i = result.iterator(); i.hasNext();) {
	// Role next = i.next();
	// if (next.getName().equalsIgnoreCase("admin")) {
	// i.remove();
	// break;
	// }
	// }
	//
	// // Roles available for a Party
	// if (party != null) {
	// Set<Role> tmp = new HashSet<Role>();
	//
	// PartyLevel level = party.getPartyLevel();
	// // Get the PartyLevel.getRoles as the available roles for the party
	// if (level != null && level.getRoles() != null &&
	// !level.getRoles().isEmpty()) {
	// tmp.addAll(level.getRoles());
	// }
	// // If no PartyLevel.getRoles, then populate with roles with a name
	// // starting by 'party'
	// else {
	// for (Role r : result)
	// if (r.getName().startsWith("party"))
	// tmp.add(r);
	// }
	//
	// result = new ArrayList<Role>(tmp);
	// } else {
	// // Roles availables for a standard user
	// for (Iterator<Role> i = result.iterator(); i.hasNext();) {
	// Role r = i.next();
	// if (r.getName().startsWith("party") || r.isWebService())
	// i.remove();
	// }
	// }
	//
	// // Sort
	// Collections.sort(result, Role.COMP_BY_ROLE_NAME);
	//
	// return result;
	// }

	public User findByUsernameAndPassword(String username, String password) {
		try {
			log.info("[UserService] Find user with username = " + username
					+ ", password =" + password);

			password = Sha1Encrypt.encodePassword(password);
			log.info("[UserService] Find user with username = " + username
					+ ", password =" + password);
			return (User) getEntityManager()
					.createQuery(
							"from User where username = :username and password = :password")
					.setParameter("username", username.toLowerCase())
					.setParameter("password", password).getSingleResult();
		} catch (NoResultException ex) {
			return null;
		}
	}

	public User findByUsernameAndPasswordHash(String username,
			String passwordHash) {
		try {
			// log.info("[UserService] Find user with password hashed");
			return (User) getEntityManager()
					.createQuery(
							"from User where username = :username and password = :password")
					.setParameter("username", username.toLowerCase())
					.setParameter("password", passwordHash).getSingleResult();
		} catch (NoResultException ex) {
			return null;
		}
	}

	public User findByEmail(String email) {
		try {
			// log.info("[UserService] Find user with email");
			return (User) getEntityManager()
					.createQuery("from User where email = :email")
					.setParameter("email", email).getSingleResult();
		} catch (NoResultException ex) {
			return null;
		}
	}

	public User findByUsername(String username) {
		// log.debug("[UserService] Find user with username = #0", username);
		try {
			QueryBuilder qb = new QueryBuilder(User.class, "u").cacheable()
					.addCriterion("u.username", "=", username.toLowerCase(),
							false);
			return (User) qb.getQuery(getEntityManager()).getSingleResult();
		} catch (NoResultException ex) {
			return null;
		}
	}

	public User changePassword(User user, String newPassword)
			throws BusinessException {
		user.setLastPasswordModification(new Date());
		user.setPassword(Sha1Encrypt.encodePassword(newPassword));
		super.update(user);

		return user;
	}

	@SuppressWarnings("unchecked")
	public List<Role> getRolesByAccountType(AccountType accountType) {
		if (accountType != null) {
			return getEntityManager()
					.createQuery(
							"select r from Role as r, IN (r.applicableToAccountTypes) accountType where accountType=:accountType order by r.description")
					.setParameter("accountType", accountType).getResultList();
		} else {
			return getEntityManager().createQuery(
					"select r from Role as r order by r.description")
					.getResultList();
		}
	}

	public Role getRoleByName(String name) {
		QueryBuilder qb = new QueryBuilder(Role.class, "r").addCriterion(
				"r.name", "=", name, false).cacheable();
		return (Role) qb.getQuery(getEntityManager()).getSingleResult();
	}

	public User loginChecks(String username, String password)
			throws InactiveEntityException, UnknownUserException,
			PasswordExpiredException {
		return loginChecks(username, password, false);
	}

	public User loginChecks(String username, String password,
			boolean skipPasswordExpiracy) throws InactiveEntityException,
			UnknownUserException, PasswordExpiredException {
		User user = findByUsernameAndPassword(username, password);
		if (skipPasswordExpiracy) {
			// log.debug("[UserService] Skipping expiry check asked");
		} else {
			// log.debug("[UserService] Checking expiry asked");
		}
		return loginChecks(user, skipPasswordExpiracy);
	}

	public User loginChecks(String username) throws InactiveEntityException,
			UnknownUserException, PasswordExpiredException {
		User user = findByUsername(username);
		return loginChecks(user, true);
	}

	public User loginChecksWithPasswordHash(String username, String passwordHash)
			throws InactiveEntityException, UnknownUserException,
			PasswordExpiredException {
		User user = findByUsernameAndPasswordHash(username, passwordHash);
		return loginChecks(user);

	}

	public User loginChecks(User user) throws InactiveEntityException,
			UnknownUserException, PasswordExpiredException {
		return loginChecks(user, false);
	}

	/**
	 * Login check to see if the user is authorized to login
	 * 
	 * FIXME : should be of the responsability of the model, not the service
	 * layer
	 * 
	 * @throws PasswordExpiredException
	 * @throws UnknownUserException
	 * 
	 */
	public User loginChecks(User user, boolean skipPasswordExpiracy)
			throws InactiveEntityException, PasswordExpiredException,
			UnknownUserException {

		if (user == null) {
			throw new UnknownUserException(null);
		}

		// Check if the user is active
		if (!user.isActive()) {
			throw new InactiveEntityException(User.class, user.getId());
		}

		// Check if business account is active
		if (user.getBusinessAccount() != null
				&& !user.getBusinessAccount().isActive()) {
			throw new InactiveEntityException(BusinessAccount.class, user
					.getBusinessAccount().getId());
		}

		// Check if the user password has expired
		String passwordExpiracy = "90";// ParamBean.getInstance().getProperty("password.expiracyInDays",
										// "90");// default 90 days
		if (user.isPasswordExpired(Integer.parseInt(passwordExpiracy))) {
			if (!skipPasswordExpiracy) {
				throw new PasswordExpiredException();
			}
		}

		// // Check the roles
		// if (user.getRoles() == null || user.getRoles().isEmpty()) {
		// throw new NoRoleException("The user #" + user.getId() +
		// " has no role!");
		// }
		return user;
	}

	public User duplicate(User user) {

		Name oldName = user.getName();

		User newUser = new User();

		Name newName = new Name();
		newName.setTitle(oldName.getTitle());
		newName.setFirstName("new_" + oldName.getFirstName());
		newName.setLastName("new_" + oldName.getLastName());
		newUser.setName(newName);

		newUser.setDisabled(true);
		newUser.setUsername("new_" + user.getUsername());
		newUser.setEmail(user.getEmail());
		newUser.setRoles(new ArrayList<Role>(user.getRoles()));
		newUser.setBusinessAccount(user.getBusinessAccount());

		return newUser;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.manaty.service.user.UserServiceLocal#simulateLogin(java.lang.String,
	 * boolean)
	 */
	public User simulateLogin(String username, boolean skipPasswordExpiracy)
			throws InactiveEntityException, UnknownUserException,
			PasswordExpiredException {
		User currentUser = findByUsername(username);
		return loginChecks(currentUser, skipPasswordExpiracy);
	}

	@SuppressWarnings("unchecked")
	public List<Role> getRolesByName(List<String> rolesNames) {
		return (List<Role>) getEntityManager()
				.createQuery("from Role as r where r.name in (:rolesNames)")
				.setParameter("rolesNames", rolesNames).getResultList();
	}

	public String remindPassword(User user, String appUrl)
			throws BusinessException {

		String newPassword = UUID.randomUUID().toString().substring(0, 8);
		getEntityManager().merge(user);
		// Set to an old date, so password would expire right away
		Calendar calendar = new GregorianCalendar(2000, 1, 1);
		user.setLastPasswordModification(calendar.getTime());
		user.setPassword(Sha1Encrypt.encodePassword(newPassword));
		super.update(user);

		return newPassword;
	}
}