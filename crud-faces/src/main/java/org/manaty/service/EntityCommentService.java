package org.manaty.service;

import java.io.Serializable;
import java.util.List;

import org.manaty.model.BaseEntity;
import org.manaty.model.EntityComment;
import org.manaty.model.QueryBuilder;

public class EntityCommentService extends PersistenceService<EntityComment>
		implements Serializable {

	/**
     * 
     */
	private static final long serialVersionUID = 9132223543424789240L;

	public void createMessage(Long entityId, String entityName,
			String fieldName, String comment) throws BusinessException {
		if (comment != null && !"".equals(comment.trim())) {
			EntityComment message = new EntityComment(entityId, entityName,
					fieldName, comment);
			create(message);
		}

	}

	@SuppressWarnings("unchecked")
	public String getComment(String fieldName, BaseEntity entity) {

		if (fieldName == null || entity == null) {
			return null;
		}

		String sql = "select distinct m from EntityComment m";

		QueryBuilder qb = new QueryBuilder(sql).cacheable();
		qb.addCriterionEntity("m.entityId", entity.getId());
		qb.addCriterionEntity("m.entityName", entity.getClass().getSimpleName());
		qb.addCriterionWildcard("m.fieldName", fieldName.toString(), true);

		List<EntityComment> msgList = qb.find(getEntityManager());
		EntityComment msg = msgList.size() > 0 ? msgList.get(0) : null;
		String comment = msg != null ? msg.getComment() : null;
		return comment;
	}

}
