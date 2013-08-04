package org.manaty.service.user;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.manaty.model.QueryBuilder;
import org.manaty.model.user.Role;
import org.manaty.service.PersistenceService;

/**
 * @author Edward P. Legaspi
 * @since Jun 17, 2013
 **/
@Stateless
public class RoleService extends PersistenceService<Role> {

	private static final long serialVersionUID = 5564134854009451554L;

	@SuppressWarnings("unchecked")
	@Override
	public List<Role> list() {
		QueryBuilder queryBuilder = new QueryBuilder(entityClass, "a", null);
		Query query = queryBuilder.getQuery(getEntityManager());
		return query.getResultList();
	}

	public Role findByName(String name) {
		try {
			return (Role) getEntityManager()
					.createQuery(
							"from " + Role.class.getName()
									+ " as r where r.name=:roleName")
					.setParameter("roleName", name).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}
}
