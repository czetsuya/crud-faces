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
package org.manaty.service;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.TypeVariable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.enterprise.context.Conversation;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.manaty.bean.PaginationConfiguration;
import org.manaty.model.AuditableEntity;
import org.manaty.model.BaseEntity;
import org.manaty.model.EnableEntity;
import org.manaty.model.IEntity;
import org.manaty.model.IdentifiableEnum;
import org.manaty.model.QueryBuilder;
import org.manaty.model.UniqueEntity;
import org.manaty.model.account.AccountType;
import org.manaty.model.account.BusinessAccount;
import org.manaty.model.user.User;
import org.manaty.util.MeveoJpa;

/**
 * Generic implementation that provides the default implementation for
 * persistence methods declared in the {@link IPersistenceService} interface.
 */
public abstract class PersistenceService<E extends IEntity> extends BaseService
		implements IPersistenceService<E> {

	private static final long serialVersionUID = 5342354725370471959L;

	protected final Class<E> entityClass;

	@Inject
	@MeveoJpa
	protected EntityManager em;

	@Inject
	private Conversation conversation;

	/**
	 * Constructor.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public PersistenceService() {
		Class clazz = getClass();
		while (!(clazz.getGenericSuperclass() instanceof ParameterizedType)) {
			clazz = clazz.getSuperclass();
		}
		Object o = ((ParameterizedType) clazz.getGenericSuperclass())
				.getActualTypeArguments()[0];

		if (o instanceof TypeVariable) {
			this.entityClass = (Class<E>) ((TypeVariable) o).getBounds()[0];
		} else {
			this.entityClass = (Class<E>) o;
		}
	}

	@SuppressWarnings("unchecked")
	public PersistenceService(Class<? extends IEntity> entityClass) {
		this.entityClass = (Class<E>) entityClass;
	}

	/**
	 * @see org.meveo.service.base.local.IPersistenceService#getEntityClass()
	 */
	public Class<E> getEntityClass() {
		return entityClass;
	}

	/**
	 * Method called before creating an entity, to perform business validation.
	 * The default implementation is empty, and should be overriden when it's
	 * needed.
	 * 
	 * @param e
	 * @throws BusinessException
	 */
	public void validateBeforeCreate(E e) throws BusinessException {

	}

	/**
	 * Method called before updating an entity, to perform business validation.
	 * The default implementation is empty, and should be overriden when it's
	 * needed.
	 * 
	 * @param e
	 * @throws BusinessException
	 */
	public void validateBeforeUpdate(E e) throws BusinessException {

	}

	/**
	 * Method called before deleting an entity, to perform business validation.
	 * The default implementation is empty, and should be overriden when it's
	 * needed.
	 * 
	 * @param e
	 * @throws BusinessException
	 */
	public void validateBeforeRemove(E e) throws BusinessException {

	}

	/**
	 * @see org.meveo.service.base.local.IPersistenceService#findById(java.lang.Long)
	 */
	public E findById(Long id) {
		return findById(id, false);
	}

	/**
	 * @see org.meveo.service.base.local.IPersistenceService#findById(java.lang.Long,
	 *      java.util.List)
	 */
	public E findById(Long id, List<String> fetchFields) {
		return findById(id, fetchFields, false);
	}

	/**
	 * @see org.meveo.service.base.local.IPersistenceService#findById(java.lang.Long,
	 *      boolean)
	 */
	public E findById(Long id, boolean refresh) {
		log.debug("start of find {} by id (id={}) ..", getEntityClass()
				.getSimpleName(), id);
		final Class<? extends E> productClass = getEntityClass();
		E e = getEntityManager().find(productClass, id);
		if (refresh) {
			log.debug("refreshing loaded entity");
			getEntityManager().refresh(e);
		}
		log.debug("end of find {} by id (id={}). Result found={}.",
				getEntityClass().getSimpleName(), id, e != null);
		return e;
	}

	/**
	 * @see org.meveo.service.base.local.IPersistenceService#findById(java.lang.Long,
	 *      java.util.List, boolean)
	 */
	@SuppressWarnings("unchecked")
	public E findById(Long id, List<String> fetchFields, boolean refresh) {
		log.debug("start of find {} by id (id={}) ..", getEntityClass()
				.getSimpleName(), id);
		final Class<? extends E> productClass = getEntityClass();
		StringBuilder queryString = new StringBuilder("from "
				+ productClass.getName() + " a");
		if (fetchFields != null && !fetchFields.isEmpty()) {
			for (String fetchField : fetchFields) {
				queryString.append(" left join fetch a." + fetchField);
			}
		}
		queryString.append(" where a.id = :id");
		Query query = getEntityManager().createQuery(queryString.toString());
		query.setParameter("id", id);

		E e = (E) query.getResultList().get(0);

		if (refresh) {
			log.debug("refreshing loaded entity");
			getEntityManager().refresh(e);
		}
		log.debug("end of find {} by id (id={}). Result found={}.",
				getEntityClass().getSimpleName(), id, e != null);
		return e;
	}

	/**
	 * @throws BusinessException
	 * @see org.meveo.service.base.local.IPersistenceService#disable(java.lang.Long)
	 */
	public void disable(Long id) throws BusinessException {
		E e = findById(id);

		if (e instanceof EnableEntity) {
			((EnableEntity) e).setDisabled(true);
			update(e);
		}
	}

	@Override
	public void disable(E e) throws BusinessException {
		if (e instanceof EnableEntity) {
			((EnableEntity) e).setDisabled(true);
			update(e);
		}
	}

	/**
	 * @see org.meveo.service.base.local.IPersistenceService#remove(org.meveo.model.model.BaseEntity)
	 */
	public void remove(E e) {
		log.debug("start of remove {} entity (id={}) ..", getEntityClass()
				.getSimpleName(), e.getId());
		getEntityManager().remove(e);
		getEntityManager().flush();
		log.debug("end of remove {} entity (id={}).", getEntityClass()
				.getSimpleName(), e.getId());
	}

	/**
	 * @throws BusinessException
	 * @see org.meveo.service.base.local.IPersistenceService#remove(java.lang.Long)
	 */
	public E remove(Long id) throws BusinessException {
		E e = findById(id);
		if (e != null) {
			validateBeforeRemove(e);
			remove(e);
		}

		return e;
	}

	/**
	 * @see org.meveo.service.base.local.IPersistenceService#remove(java.util.Set)
	 */
	public void remove(Set<Long> ids) {
		Query query = getEntityManager().createQuery(
				"delete from " + getEntityClass().getName()
						+ " where id in (:ids) and provider.id = :providerId");
		query.setParameter("ids", ids);
		query.executeUpdate();
	}

	/**
	 * @throws BusinessException
	 * @see org.meveo.service.base.local.IPersistenceService#update(org.meveo.model.model.BaseEntity)
	 */
	public void update(E e) throws BusinessException {
		update(e, getCurrentUser());
	}

	/**
	 * @see org.meveo.service.base.local.IPersistenceService#update(org.meveo.model.model.BaseEntity,
	 *      org.manaty.model.user.User)
	 */
	public void update(E e, User updater) throws BusinessException {
		log.debug("start of update {} entity (id={}) ..", e.getClass()
				.getSimpleName(), e.getId());
		try {
			log.debug("start of validation ..");
			validateBeforeUpdate(e);
			log.debug("validation OK.");
			if (e instanceof AuditableEntity) {
				if (updater != null) {
					((AuditableEntity) e).updateAudit(updater);
				} else {
					((AuditableEntity) e).updateAudit(getCurrentUser());
				}
			}
			getEntityManager().merge(e);
		} catch (BusinessException ex) {
			log.error("validation KO. Cause : {}.", ex.getMessage());
			throw ex;
		}

		log.debug("end of update {} entity (id={}).", e.getClass()
				.getSimpleName(), e.getId());
	}

	/**
	 * @throws BusinessException
	 * @see org.meveo.service.base.local.IPersistenceService#create(org.meveo.model.model.BaseEntity)
	 */
	public void create(E e) throws BusinessException {
		create(e, getCurrentUser());
	}

	/**
	 * @see org.meveo.service.base.local.IPersistenceService#create(org.meveo.model.model.BaseEntity,
	 *      org.manaty.model.user.User)
	 */
	public void create(E e, User creator) throws BusinessException {
		log.debug("start of create {} entity...", e.getClass().getSimpleName());
		try {
			log.debug("start of validation ..");
			validateBeforeCreate(e);
			log.debug("validation OK.");
			if (e instanceof AuditableEntity) {
				if (creator != null) {
					((AuditableEntity) e).updateAudit(creator);
				} else {
					((AuditableEntity) e).updateAudit(getCurrentUser());
				}
			}
			getEntityManager().persist(e);
		} catch (BusinessException ex) {
			log.error("validation failed. Cause: {}.", ex.getMessage());
			throw ex;
		}

		log.debug("end of create {}. entity id={}.", e.getClass()
				.getSimpleName(), e.getId());
	}

	/**
	 * @see org.meveo.service.base.local.IPersistenceService#list()
	 */
	@SuppressWarnings("unchecked")
	public List<E> list() {
		final Class<? extends E> entityClass = getEntityClass();
		QueryBuilder queryBuilder = new QueryBuilder(entityClass, "a");
		Query query = queryBuilder.getQuery(getEntityManager());
		return query.getResultList();
	}

	/**
	 * @see org.meveo.service.base.local.IPersistenceService#list(org.meveo.admin.bean.util.pagination.PaginationConfiguration)
	 */

	@SuppressWarnings({ "unchecked" })
	public List<E> list(PaginationConfiguration config) {
		QueryBuilder queryBuilder = getQuery(config);
		Query query = queryBuilder.getQuery(getEntityManager());
		return query.getResultList();
	}

	/**
	 * @see org.meveo.service.base.local.IPersistenceService#count(PaginationConfiguration
	 *      config)
	 */
	public long count(PaginationConfiguration config) {
		List<String> fetchFields = config.getFetchFields();
		config.setFetchFields(null);
		QueryBuilder queryBuilder = getQuery(config);
		config.setFetchFields(fetchFields);
		return queryBuilder.count(getEntityManager());
	}

	/**
	 * @see org.meveo.service.base.local.IPersistenceService#count()
	 */

	public long count() {
		final Class<? extends E> entityClass = getEntityClass();
		QueryBuilder queryBuilder = new QueryBuilder(entityClass, "a");
		return queryBuilder.count(getEntityManager());
	}

	/**
	 * @see org.meveo.service.base.local.IPersistenceService#detach
	 */

	public void detach(Object entity) {
		// TODO: Hibernate. org.hibernate.Session session = (Session)
		// getEntityManager().getDelegate();
		// session.evict(entity);
	}

	/**
	 * @see org.meveo.service.base.local.IPersistenceService#refresh(org.meveo.model.BaseEntity)
	 */

	public void refresh(BaseEntity entity) {
		// entity manager throws exception if trying to refresh not managed
		// entity (ejb spec requires this).
		/*
		 * TODO: Hibernate. org.hibernate.Session session = (Session)
		 * getEntityManager().getDelegate(); session.refresh(entity);
		 */
		// getEntityManager().getEntityManagerFactory().getCache().evict(entity.getClass(),
		// entity.getId());
		if (getEntityManager().contains(entity)) {
			getEntityManager().refresh(entity);
		}
	}

	public void flush() {
		getEntityManager().flush();
	}

	/**
	 * Creates query to filter entities according data provided in pagination
	 * configuration.
	 * 
	 * @param config
	 *            PaginationConfiguration data holding object
	 * @return query to filter entities according pagination configuration data.
	 */
	@SuppressWarnings("rawtypes")
	private QueryBuilder getQuery(PaginationConfiguration config) {

		final Class<? extends E> entityClass = getEntityClass();

		QueryBuilder queryBuilder = new QueryBuilder(entityClass, "a",
				config.getFetchFields());

		Map<String, Object> filters = config.getFilters();
		if (filters != null) {
			if (!filters.isEmpty()) {
				for (String key : filters.keySet()) {
					Object filter = filters.get(key);
					if (filter != null) {
						// if ranged search (from - to fields)
						if (key.contains("fromRange-")) {
							String parsedKey = key.substring(10);
							if (filter instanceof Double) {
								BigDecimal rationalNumber = new BigDecimal(
										(Double) filter);
								queryBuilder.addCriterion("a." + parsedKey,
										" >= ", rationalNumber, true);
							} else if (filter instanceof Number) {
								queryBuilder.addCriterion("a." + parsedKey,
										" >= ", filter, true);
							} else if (filter instanceof Date) {
								queryBuilder
										.addCriterionDateRangeFromTruncatedToDay(
												"a." + parsedKey, (Date) filter);
							}
						} else if (key.contains("toRange-")) {
							String parsedKey = key.substring(8);
							if (filter instanceof Double) {
								BigDecimal rationalNumber = new BigDecimal(
										(Double) filter);
								queryBuilder.addCriterion("a." + parsedKey,
										" <= ", rationalNumber, true);
							} else if (filter instanceof Number) {
								queryBuilder.addCriterion("a." + parsedKey,
										" <= ", filter, true);
							} else if (filter instanceof Date) {
								queryBuilder
										.addCriterionDateRangeToTruncatedToDay(
												"a." + parsedKey, (Date) filter);
							}
						} else if (key.contains("list-")) {
							// if searching elements from list
							String parsedKey = key.substring(5);
							queryBuilder.addSqlCriterion(":" + parsedKey
									+ " in elements(a." + parsedKey + ")",
									parsedKey, filter);
						}
						// if not ranged search
						else {
							if (filter instanceof String) {
								// if contains dot, that means join is needed
								String filterString = (String) filter;
								queryBuilder.addCriterionWildcard("a." + key,
										filterString, true);
							} else if (filter instanceof Date) {
								queryBuilder.addCriterionDateTruncatedToDay(
										"a." + key, (Date) filter);
							} else if (filter instanceof Number) {
								queryBuilder.addCriterion("a." + key, " = ",
										filter, true);
							} else if (filter instanceof Boolean) {
								queryBuilder.addCriterion("a." + key, " is ",
										filter, true);
							} else if (filter instanceof Enum) {
								if (filter instanceof IdentifiableEnum) {
									String enumIdKey = new StringBuilder(key)
											.append("Id").toString();
									queryBuilder
											.addCriterion("a." + enumIdKey,
													" = ",
													((IdentifiableEnum) filter)
															.getId(), true);
								} else {
									queryBuilder.addCriterionEnum("a." + key,
											(Enum) filter);
								}
							} else if (BaseEntity.class.isAssignableFrom(filter
									.getClass())) {
								queryBuilder.addCriterionEntity("a." + key,
										filter);
							} else if (filter instanceof UniqueEntity
									|| filter instanceof IEntity) {
								queryBuilder.addCriterionEntity("a." + key,
										filter);
							}
						}
					}
				}
			}
		}
		queryBuilder.addPaginationConfiguration(config, "a");

		return queryBuilder;
	}

	/**
	 * Add filtering or business account records by what current user has access
	 * to
	 * 
	 * @param qb
	 *            Query to supplement
	 * @param accountEntityVariable
	 *            Variable in a query holding business account
	 */
	protected void addRecordFilteringByCurrentUser(QueryBuilder qb,
			String accountEntityVariable) {
		User user = getCurrentUser();

		// Limit by account type
		Collection<AccountType> types = user.getManageableAccountTypes();
		if (!types.isEmpty()) {
			qb.addInCriterion(accountEntityVariable + ".accountType", types);

			// Let query fail if does have access to any account type
		} else {
			qb.addSql("1=2");
		}

		// Limit by account children
		if (user.getBusinessAccount() != null) {
			List<BusinessAccount> accounts = collectAccountChildren(user
					.getBusinessAccount());
			accounts.add(user.getBusinessAccount());
			qb.addInCriterion(accountEntityVariable, accounts);
		}
	}

	/**
	 * Compile a list of child accounts, including all the hierarchy down
	 * 
	 * @param businessAccount
	 *            Business account to examine
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<BusinessAccount> collectAccountChildren(
			BusinessAccount businessAccount) {

		List<BusinessAccount> accounts = new ArrayList<BusinessAccount>();

		List<BusinessAccount> accountChildren = em
				.createQuery(
						"select ba from BusinessAccount ba where ba.parentAccount=:businessAccount")
				.setParameter("businessAccount", businessAccount)
				.getResultList();
		accounts.addAll(accountChildren);
		for (BusinessAccount accountChild : accountChildren) {
			accounts.addAll(collectAccountChildren(accountChild));
		}

		return accounts;
	}

	protected EntityManager getEntityManager() {
		EntityManager result = null;
		if (conversation != null) {
			try {
				conversation.isTransient();
				result = em;
			} catch (Exception e) {
			}
		}
		return result;
	}

}
