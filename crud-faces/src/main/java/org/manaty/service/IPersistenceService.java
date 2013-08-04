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

import java.util.List;
import java.util.Set;

import org.manaty.bean.PaginationConfiguration;
import org.manaty.model.BaseEntity;
import org.manaty.model.IEntity;
import org.manaty.model.user.User;

/**
 * Generic interface that defines the methods to implement for every persistence
 * service.
 * 
 * @param <E>
 *            Class that inherits from {@link BaseEntity}
 */
public interface IPersistenceService<E extends IEntity> {

	/**
	 * Find an entity by its id.
	 * 
	 * @param id
	 *            Id to find entity by.
	 * @return Entity found.
	 */
	public E findById(Long id);

	/**
	 * Find an entity by its id and fetch required fields.
	 * 
	 * @param id
	 *            Id to find entity by.
	 * @param fetchFields
	 *            List of fields to fetch.
	 * @return Entity found.
	 */
	public E findById(Long id, List<String> fetchFields);

	/**
	 * Find an entity by its id.
	 * 
	 * @param id
	 *            Id to find entity by.
	 * @param refresh
	 *            Is entity refresh after load needed.
	 * @return Entity found.
	 */
	public E findById(Long id, boolean refresh);

	/**
	 * Find an entity by its id and fetch required fields.
	 * 
	 * @param id
	 *            Id to find entity by.
	 * @param fetchFields
	 *            List of fields to fetch.
	 * @param refresh
	 *            Is entity refresh after load needed.
	 * @return Entity found.
	 */
	public E findById(Long id, List<String> fetchFields, boolean refresh);

	/**
	 * Persist an entity
	 * 
	 * @param e
	 *            Entity to persist.
	 * 
	 * @throws BusinessException
	 */
	public void create(E e) throws BusinessException;

	/**
	 * Persist an entity.
	 * 
	 * @param e
	 *            Entity to persist.
	 * @param updater
	 *            User who performs entity persist.
	 * 
	 * @throws BusinessException
	 */
	public void create(E e, User creator) throws BusinessException;

	/**
	 * Update an entity.
	 * 
	 * @param e
	 *            Entity to update.
	 * 
	 * @throws BusinessException
	 */
	public void update(E e) throws BusinessException;

	/**
	 * Update an entity.
	 * 
	 * @param e
	 *            Entity to update.
	 * @param updater
	 *            User who performs entity update.
	 * 
	 * @throws BusinessException
	 */
	public void update(E e, User updater) throws BusinessException;

	/**
	 * Delete an entity.
	 * 
	 * @param id
	 *            Entity id which has to be deleted.
	 * 
	 * @throws BusinessException
	 */
	public E remove(Long id) throws BusinessException;

	/**
	 * Disable an entity.
	 * 
	 * @param id
	 *            Entity id which has to be disabled.
	 * 
	 * @throws BusinessException
	 */
	public void disable(Long id) throws BusinessException;

	/**
	 * Delete an entity.
	 * 
	 * @param e
	 *            Entity to delete.
	 * 
	 * @throws BusinessException
	 */
	public void remove(E e);

	/**
	 * Delete list of entities by provided ids.
	 * 
	 * @param ids
	 *            Entities ids to delete.
	 * 
	 * @throws BusinessException
	 */
	public void remove(Set<Long> ids);

	/**
	 * The entity class which the persistence is managed by the persistence
	 * service.
	 * 
	 * @return Entity class.
	 */
	public Class<E> getEntityClass();

	/**
	 * Load and return the complete list of the entities from database.
	 * 
	 * @return List of entities.
	 */
	public List<E> list();// ? extends E

	/**
	 * Load and return the list of the entities from database according to
	 * sorting and paging information in {@link PaginationConfiguration} object.
	 * 
	 * @return List of entities.
	 */
	public List<E> list(PaginationConfiguration config); // ? extends E

	/**
	 * Count number of entities in database.
	 * 
	 * @return Number of entities.
	 */
	public long count();

	/**
	 * Count number of filtered entities in database.
	 * 
	 * @return Number of filtered entities.
	 */
	public long count(PaginationConfiguration config);

	/**
	 * Detach an entity.
	 * 
	 * @param entity
	 *            Entity which has to be detached.
	 */
	public void detach(Object entity);

	/**
	 * Refresh entity with state from database.
	 */
	public void refresh(BaseEntity entity);
	
	public void flush();

	void disable(E e) throws BusinessException;

}