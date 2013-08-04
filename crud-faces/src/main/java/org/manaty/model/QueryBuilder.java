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
package org.manaty.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.manaty.bean.PaginationConfiguration;

public class QueryBuilder {

	// Nb max d'arguments de la clause sql IN (Oracle) Value of 1000 was causing
	// stackoverflow
	private final static int NB_MAX_IN_CLAUSE = 1000;

	// Temporary field until oracle fix (Limitations of Oracle that supports
	// only a maximum of 5 clauses IN)
	private final static int NB_IN_CLAUSE = 5;

	private StringBuffer q;
	private Map<String, Object> params;

	private boolean hasOneOrMoreCriteria;
	private boolean inOrClause;
	private int nbCriteriaInOrClause;
	private boolean whereClauseOnly = false;

	private boolean inNotClause;
	private int nbCriteriaInNotClause;

	private boolean inAndClause;
	private int nbCriteriaInAndClause;

	private PaginationConfiguration paginationConfiguration;
	private String paginationSortAlias;

	/*
	 * Should cache be bypassed and refreshed upon query execution. <br/>
	 * Default value is false - entities from cache should be used if they exist
	 * there.
	 */
	private boolean bypassCache = false;

	/*
	 * If readonly is set, then entities retrieved by this query will be loaded
	 * in a read-only mode where Hibernate will never dirty-check them or make
	 * changes persistent.
	 */
	private boolean readOnly = false;

	/*
	 * If cacheable is set, then the L2 cache will be used
	 */
	private boolean cacheable = false;

	/*
	 * If flushMode is set to NEVER, then the query execution wont trigger a
	 * flush
	 */
	private boolean flushModeNEVER = false;

	public QueryBuilder(String sql) {
		q = new StringBuffer(sql);
		params = new HashMap<String, Object>();
		hasOneOrMoreCriteria = false;
		inOrClause = false;
		nbCriteriaInOrClause = 0;
		inAndClause = false;
		nbCriteriaInAndClause = 0;
	}

	public QueryBuilder(QueryBuilder qb) {
		this.q = new StringBuffer(qb.q);
		this.params = new HashMap<String, Object>(qb.params); // FIXME Clone
																// inside
																// elements
		this.hasOneOrMoreCriteria = qb.hasOneOrMoreCriteria;
		this.inOrClause = qb.inOrClause;
		this.nbCriteriaInOrClause = qb.nbCriteriaInOrClause;
		this.inAndClause = qb.inAndClause;
		this.nbCriteriaInAndClause = qb.nbCriteriaInAndClause;
		this.whereClauseOnly = qb.whereClauseOnly;
	}

	public QueryBuilder(Class<?> clazz, String alias) {
		this("from " + clazz.getName() + " " + alias);
	}

	public QueryBuilder(Class<?> clazz, String alias, List<String> fetchFields) {
		this(getInitQuery(clazz, alias, fetchFields));
	}

	private static String getInitQuery(Class<?> clazz, String alias, List<String> fetchFields) {
		StringBuilder query = new StringBuilder("from " + clazz.getName() + " " + alias);
		if (fetchFields != null && !fetchFields.isEmpty()) {
			for (String fetchField : fetchFields) {
				query.append(" left join fetch " + alias + "." + fetchField);
			}
		}

		return query.toString();
	}

	public StringBuffer getSqlStringBuffer() {
		return q;
	}

	public QueryBuilder addPaginationConfiguration(PaginationConfiguration paginationConfiguration) {
		return addPaginationConfiguration(paginationConfiguration, null);
	}

	public QueryBuilder addPaginationConfiguration(PaginationConfiguration paginationConfiguration,
			String sortAlias) {
		this.paginationSortAlias = sortAlias;
		this.paginationConfiguration = paginationConfiguration;
		return this;
	}

	public QueryBuilder addSql(String sql) {
		return addSqlCriterion(sql, null, null);
	}

	public QueryBuilder addSqlCriterion(String sql, String param, Object value) {
		if (param != null && isBlank(value))
			return this;

		// Do not append word "Where" if only Where clause interests us.
		if (hasOneOrMoreCriteria || whereClauseOnly) {
			if (inOrClause && nbCriteriaInOrClause != 0) {
				if (inAndClause && nbCriteriaInAndClause != 0) {
					q.append(" and ");
				} else {
					q.append(" or ");
				}
			} else {
				q.append(" and ");
			}
		} else {
			q.append(" where ");
		}

		if (inNotClause && nbCriteriaInNotClause == 0) {
			q.append(" not (");
		}

		if (inOrClause && nbCriteriaInOrClause == 0) {
			q.append("(");
		}

		if (inAndClause && nbCriteriaInAndClause == 0) {
			q.append("(");
		}

		q.append(sql);

		if (param != null)
			params.put(param, value);

		hasOneOrMoreCriteria = true;
		if (inNotClause) {
			nbCriteriaInNotClause++;
		}
		if (inOrClause) {
			nbCriteriaInOrClause++;
		}
		if (inAndClause) {
			nbCriteriaInAndClause++;
		}

		return this;
	}

	public QueryBuilder addBooleanCriterion(String field, Boolean value) {
		if (isBlank(value))
			return this;

		addSql(field + (value.booleanValue() ? " is true " : " is false "));
		return this;
	}

	public QueryBuilder addCriterion(String field, String operator, Object value,
			boolean caseInsensitive) {
		if (isBlank(value))
			return this;

		StringBuffer sql = new StringBuffer();
		String param = convertFieldToParam(field);
		Object nvalue = value;

		if (caseInsensitive && (value instanceof String))
			sql.append("lower(" + field + ")");
		else
			sql.append(field);

		sql.append(operator + ":" + param);

		if (caseInsensitive && (value instanceof String))
			nvalue = ((String) value).toLowerCase();

		return addSqlCriterion(sql.toString(), param, nvalue);
	}

	public QueryBuilder addCriterionEntity(String field, Object entity) {
		if (entity == null)
			return this;

		String param = convertFieldToParam(field);

		return addSqlCriterion(field + "=:" + param, param, entity);
	}

	@SuppressWarnings("rawtypes")
	public QueryBuilder addCriterionEnum(String field, Enum enumValue) {
		if (enumValue == null)
			return this;

		String param = convertFieldToParam(field);

		return addSqlCriterion(field + "=:" + param, param, enumValue);
	}

	public QueryBuilder addExistsCriterion(QueryBuilder ext) {
		if (ext == null)
			return ext;

		addSql("exists(" + ext.q.toString() + ")");

		if (ext.params != null) {
			for (Map.Entry<String, Object> e : ext.params.entrySet()) {
				params.put(e.getKey(), e.getValue());
			}
		}

		return this;
	}

	public QueryBuilder addCriterion(QueryBuilder ext) {
		if (ext == null) {
			return ext;
		}

		addSql("(" + ext.q.toString() + ")");

		if (ext.params != null) {
			for (Map.Entry<String, Object> e : ext.params.entrySet()) {
				params.put(e.getKey(), e.getValue());
			}
		}

		return this;
	}

	public QueryBuilder addInCriterion(String field, QueryBuilder ext) {
		if (ext == null)
			return ext;

		addSql(field + " in (" + ext.q.toString() + ")");

		if (ext.params != null) {
			for (Map.Entry<String, Object> e : ext.params.entrySet()) {
				params.put(e.getKey(), e.getValue());
			}
		}

		return this;
	}

	/**
	 * Ajouter un critere like
	 * 
	 * @param field
	 * @param value
	 * @param style
	 *            : 0=Search for exact value, 1=Search for beginning of the word
	 *            ("value%"), 2=Search for anywhere in the word ("%value%")
	 * @param caseInsensitive
	 * @return
	 */
	public QueryBuilder like(String field, String value, int style, boolean caseInsensitive) {
		if (isBlank(value))
			return this;

		String v = value;

		if (style != 0) {
			if (style == 1 || style == 2)
				v = v + "%";
			if (style == 2)
				v = "%" + v;
		}

		return addCriterion(field, " like ", v, caseInsensitive);
	}

	public QueryBuilder addCriterionWildcard(String field, String value, boolean caseInsensitive) {
		if (isBlank(value))
			return this;
		boolean wildcard = (value.indexOf("*") != -1);

		if (wildcard)
			return like(field, value.replace("*", "%"), 0, caseInsensitive);
		else
			return addCriterion(field, "=", value, caseInsensitive);
	}

	// add the date field searching support

	public QueryBuilder addCriterionDate(String field, Date value) {
		if (isBlank(value))
			return this;
		return addCriterion(field, "=", value, false);

	}

	public QueryBuilder addCriterionDateTruncatedToDay(String field, Date value) {
		if (isBlank(value))
			return this;
		Calendar c = Calendar.getInstance();
		c.setTime(value);
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int date = c.get(Calendar.DATE);
		c.set(year, month, date, 0, 0, 0);
		Date start = c.getTime();
		c.set(year, month, date, 23, 59, 59);
		Date end = c.getTime();

		return addSqlCriterion(field + ">=:start", "start", start).addSqlCriterion(
				field + "<=:end", "end", end);

	}

	public QueryBuilder addCriterionDateRangeFromTruncatedToDay(String field, Date valueFrom) {
		if (valueFrom != null)
			return this;
		Calendar calFrom = Calendar.getInstance();
		calFrom.setTime(valueFrom);
		int yearFrom = calFrom.get(Calendar.YEAR);
		int monthFrom = calFrom.get(Calendar.MONTH);
		int dateFrom = calFrom.get(Calendar.DATE);
		calFrom.set(yearFrom, monthFrom, dateFrom, 0, 0, 0);
		Date start = calFrom.getTime();

		String startDateParameterName = "start" + field.replace(".", "");
		return addSqlCriterion(field + ">=:" + startDateParameterName, startDateParameterName,
				start);
	}

	public QueryBuilder addCriterionDateRangeToTruncatedToDay(String field, Date valueTo) {
		if (valueTo != null)
			return this;
		Calendar calTo = Calendar.getInstance();
		calTo.setTime(valueTo);
		int yearTo = calTo.get(Calendar.YEAR);
		int monthTo = calTo.get(Calendar.MONTH);
		int dateTo = calTo.get(Calendar.DATE);
		calTo.set(yearTo, monthTo, dateTo, 23, 59, 59);
		Date end = calTo.getTime();

		String endDateParameterName = "end" + field.replace(".", "");
		return addSqlCriterion(field + "<=:" + endDateParameterName, endDateParameterName, end);
	}

	public QueryBuilder startNotClause() {
		inNotClause = true;
		nbCriteriaInNotClause = 0;
		return this;
	}

	public QueryBuilder startOrClause() {
		inOrClause = true;
		nbCriteriaInOrClause = 0;
		return this;
	}

	public QueryBuilder endNotClause() {
		if (nbCriteriaInNotClause != 0)
			q.append(")");

		inNotClause = false;
		nbCriteriaInNotClause = 0;
		return this;
	}

	public QueryBuilder endOrClause() {
		if (nbCriteriaInOrClause != 0)
			q.append(")");

		inOrClause = false;
		nbCriteriaInOrClause = 0;
		return this;
	}

	public QueryBuilder startInnerAndClause() {
		inAndClause = true;
		nbCriteriaInAndClause = 0;
		return this;
	}

	public QueryBuilder endInnerAndClause() {
		if (nbCriteriaInAndClause != 0)
			q.append(")");

		inAndClause = false;
		nbCriteriaInAndClause = 0;
		return this;
	}

	// FIXME Limitations of Oracle that supports only a maximum of 5 clauses IN
	// => Could be solved by a union
	public QueryBuilder addInCriterion(String field, Collection<?> values) {
		if (values == null || values.isEmpty())
			return this;

		int listSize = values.size();
		String paramName;
		List<Object> list = new ArrayList<Object>(values);
		List<Object> sublist;

		int nbSublists = (listSize / NB_MAX_IN_CLAUSE) + (listSize % NB_MAX_IN_CLAUSE > 0 ? 1 : 0);

		startOrClause();
		for (int i = 0; i < nbSublists; i++) {
			paramName = convertFieldToParam(field + i);
			sublist = list.subList(i * NB_MAX_IN_CLAUSE,
					Math.min((i + 1) * NB_MAX_IN_CLAUSE, listSize));
			addSqlCriterion(field + " in (:" + paramName + ")", paramName, sublist);
		}

		endOrClause();

		return this;
	}

	// FIXME Limitations of Oracle that supports only a maximum of 5 clauses IN
	public static List<List<?>> getInLists(List<?> values) {
		return getInLists(values, NB_IN_CLAUSE * NB_MAX_IN_CLAUSE);
	}

	// FIXME Limitations of Oracle that supports only a maximum of 5 clauses IN
	public static List<List<?>> getInLists(List<?> values, int size) {
		if (values == null || values.isEmpty())
			return null;

		int listSize = values.size();
		int nbSublists = (listSize / size) + (listSize % size > 0 ? 1 : 0);
		List<List<?>> results = new ArrayList<List<?>>();

		for (int i = 0; i < nbSublists; i++) {
			results.add(values.subList(i * size, Math.min((i + 1) * size, listSize)));
		}
		return results;
	}

	/**
	 * Transform query builder into Entity manager's javax.persistence.Query
	 * 
	 * @param em
	 *            Entity manager
	 * @return javax.persistence.Query
	 */
	public Query getQuery(EntityManager em) {
		applyPagination(paginationSortAlias);

		try {
			Query result = em.createQuery(q.toString());
			applyPagination(result);

			for (Map.Entry<String, Object> e : params.entrySet()) {
				result.setParameter(e.getKey(), e.getValue());
			}

			// if (bypassCache) {
			// result.setHint("org.hibernate.cacheMode", CacheMode.REFRESH);
			// }

			if (readOnly) {
				result.setHint("org.hibernate.readOnly", true);
			}

			if (cacheable) {
				result.setHint("org.hibernate.cacheable", Boolean.TRUE);
			}

			if (flushModeNEVER) {
				result.setHint("org.hibernate.flushMode", "NEVER");
			}

			return result;

		} catch (RuntimeException e) {
			// Log log = LogFactory.getLog(QueryBuilder.class);
			// log.error("Failed to construct a query. Base query: " + this);
			throw e;
		}
	}

	/**
	 * Construct a count query from a base query and transform into Entity
	 * manager's javax.persistence.Query
	 * 
	 * @param em
	 *            Entity manager
	 * @return javax.persistence.Query
	 */
	public Query getCountQuery(EntityManager em) {
		String countQueryString = buildCountQueryString();

		try {
			Query result = em.createQuery(countQueryString);
			for (Map.Entry<String, Object> e : params.entrySet())
				result.setParameter(e.getKey(), e.getValue());
			return result;

		} catch (RuntimeException e) {
			// Log log = LogFactory.getLog(QueryBuilder.class);
			// log.error("Failed to construct a count query. Base query: " +
			// this + ", count query produced: " + countQueryString, e);
			throw e;
		}
	}

	private String buildCountQueryString() {
		final String SELECT = "select";
		final String FROM = "from ";
		final String ORDERBY = "order by";

		StringBuilder result = new StringBuilder("select count(");

		String trimmedSql = q.toString().trim();

		// Remove any "order by" clause
		int iOrderBy = trimmedSql.toLowerCase().indexOf(ORDERBY);
		if (iOrderBy > 0) {
			trimmedSql = trimmedSql.substring(0, iOrderBy);
		}

		// Replace select clause with count(*) or count(xx) clause
		int iFrom = trimmedSql.toLowerCase().indexOf(FROM);

		if (trimmedSql.toLowerCase().startsWith(SELECT)) {
			String betweenSelectAndFrom = trimmedSql.substring(SELECT.length() + 1, iFrom - 1);
			result.append(betweenSelectAndFrom.trim());
			result.append(") ");
			result.append(trimmedSql.substring(iFrom));
		} else if (trimmedSql.toLowerCase().startsWith(FROM)) {
			result.append("*) ");
			result.append(trimmedSql);
		}

		return result.toString();
	}

	/**
	 * Count records matching the query
	 * 
	 * @param em
	 *            Entity manager
	 * @return Number of records matched
	 */
	public Long count(EntityManager em) {
		Query query = getCountQuery(em);
		return (Long) query.getSingleResult();
	}

	/**
	 * Find corresponding records
	 * 
	 * @param em
	 *            Entity manager
	 * @return List of records
	 */
	@SuppressWarnings("rawtypes")
	public List find(EntityManager em) {
		Query query = getQuery(em);
		return query.getResultList();
	}

	/**
	 * *************************************************************************
	 * ******************************
	 */
	/* PRIVATE */
	/**
	 * *************************************************************************
	 * ******************************
	 */
	private String convertFieldToParam(String field) {
		field = field.replace(".", "_").replace("(", "_").replace(")", "_");
		StringBuilder newField = new StringBuilder(field);
		while (params.containsKey(newField.toString()))
			newField = new StringBuilder(field).append("_"
					+ String.valueOf(new Random().nextInt(100)));
		return newField.toString();
	}

	private void applyPagination(String alias) {
		if (paginationConfiguration == null)
			return;

		if (paginationConfiguration.isSorted())
			addOrderCriterion(
					((alias != null) ? (alias + ".") : "") + paginationConfiguration.getSortField(),
					paginationConfiguration.isAscendingSorting());

	}

	private void applyPagination(Query query) {
		if (paginationConfiguration == null)
			return;

		query.setFirstResult(paginationConfiguration.getFirstRow());
		query.setMaxResults(paginationConfiguration.getNumberOfRows());
	}

	/**
	 * *************************************************************************
	 * ******************************
	 */
	/* DEBUG */
	/**
	 * *************************************************************************
	 * ******************************
	 */
	public void debug() {
		System.out.println("Requete : " + q.toString());
		for (Map.Entry<String, Object> e : params.entrySet())
			System.out.println("Param name:" + e.getKey() + " value:" + e.getValue().toString());
	}

	public String toString() {
		String result = q.toString();
		for (Map.Entry<String, Object> e : params.entrySet()) {
			result = result + " Param name:" + e.getKey() + " value:" + e.getValue().toString();
		}
		return result;
	}

	public String getSqlString() {
		return q.toString();
	}

	public Map<String, Object> getParams() {
		return Collections.unmodifiableMap(params);
	}

	public void addOrderCriterion(String orderColumn, boolean ascending) {
		q.append(" ORDER BY " + orderColumn);
		if (ascending) {
			q.append(" ASC ");
		} else {
			q.append(" DESC ");
		}

	}

	public QueryBuilder addOrderDoubleCriterion(String orderColumn, boolean ascending,
			String orderColumn2, boolean ascending2) {
		q.append(" ORDER BY " + orderColumn);
		if (ascending) {
			q.append(" ASC ");
		} else {
			q.append(" DESC ");
		}
		q.append(", " + orderColumn2);
		if (ascending2) {
			q.append(" ASC ");
		} else {
			q.append(" DESC ");
		}
		return this;
	}

	public QueryBuilder addOrderUniqueCriterion(String orderColumn, boolean ascending) {
		q.append(" ORDER BY " + orderColumn);
		if (ascending) {
			q.append(" ASC ");
		} else {
			q.append(" DESC ");
		}
		return this;
	}

	public boolean isBypassCache() {
		return bypassCache;
	}

	public void setBypassCache(boolean bypassCache) {
		this.bypassCache = bypassCache;
	}

	public boolean isReadOnly() {
		return readOnly;
	}

	public void setReadOnly(boolean readonly) {
		this.readOnly = readonly;
	}

	public QueryBuilder cacheable() {
		this.cacheable = true;
		return this;
	}

	public QueryBuilder flushModeNEVER() {
		this.flushModeNEVER = true;
		return this;
	}

	/**
	 * Set a parameter value
	 * 
	 * @param parameterName
	 *            Parameter name
	 * @param value
	 *            Parameter value
	 */
	public void setParameter(String parameterName, Object value) {
		if (params != null) {
			params.put(parameterName, value);
		}
	}

	/**
	 * Do not append word "Where" if only Where clause interests us.
	 * 
	 * @param whereClauseOnly
	 *            Should word "Where" be omitted in the generated SQL statement
	 */
	public void setWhereClauseOnly(boolean whereClauseOnly) {
		this.whereClauseOnly = whereClauseOnly;
	}

	private boolean isBlank(Object value) {
		return ((value == null) || ((value instanceof String) && ((String) value).trim().length() == 0));
	}
}