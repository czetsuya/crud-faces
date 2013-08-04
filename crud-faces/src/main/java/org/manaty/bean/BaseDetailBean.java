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

import org.manaty.model.IEntity;
import org.manaty.service.PersistenceService;
import org.manaty.util.StringUtils;

/**
 * @author Edward P. Legaspi
 * @since Jun 5, 2013
 * @description Base class for add, edit and view page.
 **/
public abstract class BaseDetailBean<E extends IEntity, P extends PersistenceService<E>>
		extends BaseBean<E, P> {

	private static final long serialVersionUID = -5944314259730535730L;

	private String lcid;
	private String backView;

	public BaseDetailBean() {
		super();
	}

	public BaseDetailBean(Class<E> entityClass, Class<P> serviceClass) {
		super(entityClass, serviceClass);
	}

	public void resetFormEntity() {
		entity = null;
		entity = getEntity();
	}

	protected String getListViewName() {
		String className = getEntityClass().getSimpleName();
		StringBuilder sb = new StringBuilder(className);
		char[] dst = new char[1];
		sb.getChars(0, 1, dst, 0);
		sb.replace(0, 1, new String(dst).toLowerCase());
		sb.append("s");
		return sb.toString();
	}

	@Override
	public String back() {
		if (!StringUtils.isBlank(backView)) {
			return backView;
		} else {
			return getListViewName();
		}
	}

	public String getBackView() {
		return backView;
	}

	public void setBackView(String backView) {
		this.backView = backView;
	}

	public String getLcid() {
		return lcid;
	}

	public void setLcid(String lcid) {
		this.lcid = lcid;
	}

}
