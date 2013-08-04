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
package org.manaty.bean;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.Conversation;
import javax.inject.Inject;

import org.jboss.seam.security.Identity;
import org.manaty.model.IEntity;
import org.manaty.model.user.User;
import org.manaty.security.MeveoUser;
import org.manaty.service.BusinessException;
import org.manaty.service.PersistenceService;
import org.manaty.util.StringUtils;
import org.omnifaces.util.Faces;
import org.omnifaces.util.Messages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Edward P. Legaspi
 * @since Jun 5, 2013
 * @description Base bean class. Other backing beans extends this class if they
 *              need functionality it provides.
 */
public abstract class BaseBean<E extends IEntity, P extends PersistenceService<E>>
		implements Serializable {

	private static final long serialVersionUID = 6839931968074051911L;
	protected Logger log = LoggerFactory.getLogger(BaseBean.class);

	@Inject
	protected P persistenceService;

	@Inject
	private Conversation conversation;

	@Inject
	private Identity identity;

	protected Map<String, Object> filters = new HashMap<String, Object>();
	protected E entity;
	protected Class<E> entityClass;
	protected Class<P> serviceClass;

	private Long objectId;
	private boolean edit;
	private String ccid;

	public BaseBean() {
		super();
	}

	public BaseBean(Class<E> entityClass, Class<P> serviceClass) {
		super();
		this.entityClass = entityClass;
		this.serviceClass = serviceClass;
	}

	public void beginConversation() {
		if (conversation.isTransient()) {
			conversation.begin();
		}
	}

	public void endConversation() {
		if (!conversation.isTransient()) {
			conversation.end();
		}
	}

	public void preRenderView() {
		beginConversation();
	}

	public String getConversationId() {
		beginConversation();
		return conversation.getId();
	}

	public void setConversationId(String conversationId) {
	}

	public E initEntity() {
		log.debug("instantiating " + this.getClass());
		if (getObjectId() != null) {
			if (getFormFieldsToFetch() == null) {
				entity = (E) persistenceService.findById(getObjectId(), true);
			} else {
				entity = (E) persistenceService.findById(getObjectId(),
						getFormFieldsToFetch());
			}
		} else {
			try {
				entity = getInstance();
			} catch (InstantiationException e) {
				log.error("Unexpected error!", e);
				throw new IllegalStateException(
						"could not instantiate a class, abstract class");
			} catch (IllegalAccessException e) {
				log.error("Unexpected error!", e);
				throw new IllegalStateException(
						"could not instantiate a class, constructor not accessible");
			}
		}

		return entity;
	}

	public String saveOrUpdate() throws BusinessException {
		return saveOrUpdate(false);
	}

	public String saveOrUpdate(boolean killConversation)
			throws BusinessException {
		String outcome = saveOrUpdate(entity);
		return outcome;
	}

	/**
	 * Save or update entity depending on if entity is transient.
	 * 
	 * @param entity
	 *            Entity to save.
	 * @throws BusinessException
	 */
	protected String saveOrUpdate(E entity) throws BusinessException {
		if (entity.isTransient()) {
			persistenceService.create(entity);
			Messages.addGlobalInfo("message.entity.save.ok");
		} else {
			persistenceService.update(entity);
			Messages.addGlobalInfo("message.entity.update.ok");
		}

		return back();
	}

	public String back() {
		return null;
	}

	public E getInstance() throws InstantiationException,
			IllegalAccessException {
		return entityClass.newInstance();
	}

	protected User getCurrentUser() {
		return ((MeveoUser) identity.getUser()).getUser();
	}

	public void setEntity(E entity) {
		this.entity = entity;
	}

	public E getEntity() {
		return entity != null ? entity : initEntity();
	}

	public Long getObjectId() {
		return objectId;
	}

	public void setObjectId(Long objectId) {
		this.objectId = objectId;
	}

	protected String getListViewName() {
		return null;
	}

	private List<String> getFormFieldsToFetch() {
		return null;
	}

	public Map<String, Object> getFilters() {
		if (filters == null) {
			filters = new HashMap<String, Object>();
		}
		addFilter();

		return filters;
	}

	protected void addFilter() {

	}

	public Class<E> getEntityClass() {
		return entityClass;
	}

	public boolean isEdit() {
		return edit;
	}

	public void setEdit(boolean edit) {
		if (!Faces.isAjaxRequest() || !this.edit)
			this.edit = edit;
	}

	public List<E> listAll() {
		return persistenceService.list();
	}

	public String getCcid() {
		return ccid;
	}

	public void setCcid(String ccid) {
		if (!StringUtils.isBlank(ccid)) {
			this.ccid = ccid;
		}
	}

}
