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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityExistsException;

import org.manaty.model.IEntity;
import org.manaty.service.PersistenceService;
import org.manaty.util.StringUtils;
import org.omnifaces.util.Messages;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

/**
 * @author Edward P. Legaspi
 * @since Jun 5, 2013
 **/
public abstract class BaseListBean<E extends IEntity, P extends PersistenceService<E>>
		extends BaseBean<E, P> {

	private static final long serialVersionUID = -2528942065567377913L;

	private LazyDataModel<E> dataModel;
	private E[] selectedEntities;
	private DataTable dataTable;

	public BaseListBean() {
		super();
	}

	public BaseListBean(Class<E> entityClass, Class<P> serviceClass) {
		super(entityClass, serviceClass);
	}

	public LazyDataModel<E> getLazyDataModel() {
		return getLazyDataModel(filters);
	}

	public LazyDataModel<E> getLazyDataModel(Map<String, Object> inputFilters) {
		if (dataModel == null) {
			final Map<String, Object> filters = inputFilters;
			dataModel = new LazyDataModel<E>() {
				private static final long serialVersionUID = 1L;
				private Integer rowCount;
				private Integer rowIndex;

				@Override
				public List<E> load(int first, int pageSize, String sortField,
						SortOrder sortOrder, Map<String, String> loadingFilters) {

					if (!StringUtils.isBlank(getDefaultSort())
							&& StringUtils.isBlank(sortField)) {
						sortField = getDefaultSort();
					}

					Map<String, Object> copyOfFilters = new HashMap<String, Object>();
					copyOfFilters.putAll(filters);
					setRowCount((int) persistenceService
							.count(new PaginationConfiguration(first, pageSize,
									copyOfFilters, getListFieldsToFetch(),
									sortField, sortOrder)));
					if (getRowCount() > 0) {
						copyOfFilters = new HashMap<String, Object>();
						copyOfFilters.putAll(filters);
						return persistenceService
								.list(new PaginationConfiguration(first,
										pageSize, copyOfFilters,
										getListFieldsToFetch(), sortField,
										sortOrder));
					} else {
						return null;
					}
				}

				@Override
				public E getRowData(String rowKey) {
					return persistenceService.findById(Long.valueOf(rowKey));
				}

				@Override
				public Object getRowKey(E object) {
					return object.getId();
				}

				@Override
				public void setRowIndex(int rowIndex) {
					if (rowIndex == -1 || getPageSize() == 0) {
						this.rowIndex = rowIndex;
					} else {
						this.rowIndex = rowIndex % getPageSize();
					}
				}

				@SuppressWarnings("unchecked")
				@Override
				public E getRowData() {
					return ((List<E>) getWrappedData()).get(rowIndex);
				}

				@SuppressWarnings({ "unchecked" })
				@Override
				public boolean isRowAvailable() {
					if (getWrappedData() == null) {
						return false;
					}

					return rowIndex >= 0
							&& rowIndex < ((List<E>) getWrappedData()).size();
				}

				@Override
				public int getRowIndex() {
					return this.rowIndex;
				}

				@Override
				public void setRowCount(int rowCount) {
					this.rowCount = rowCount;
				}

				@Override
				public int getRowCount() {
					if (rowCount == null) {
						rowCount = (int) persistenceService.count();
					}
					return rowCount;
				}

			};
		}

		return dataModel;
	}

	protected String getDefaultSort() {
		return null;
	}

	protected List<String> getListFieldsToFetch() {
		return null;
	}

	protected String getNewViewName() {
		return getEditViewName();
	}

	protected String getEditViewName() {
		String className = entityClass.getSimpleName();
		StringBuilder sb = new StringBuilder(className);
		char[] dst = new char[1];
		sb.getChars(0, 1, dst, 0);
		sb.replace(0, 1, new String(dst).toLowerCase());
		return sb.toString();
	}

	public DataTable getDataTable() {
		return dataTable;
	}

	public void setDataTable(DataTable dataTable) {
		this.dataTable = dataTable;
	}

	public E[] getSelectedEntities() {
		return selectedEntities;
	}

	public void setSelectedEntities(E[] selectedEntities) {
		this.selectedEntities = selectedEntities;
	}

	public DataTable search() {
		dataTable.reset();
		return dataTable;
	}

	public void clean() {
		log.debug("clean filters and dataModel");
		dataModel = null;
		filters = new HashMap<String, Object>();
	}

	/**
	 * Delete Entity using it's ID. Add error message to {@link statusMessages}
	 * if unsuccessful.
	 * 
	 * @param id
	 *            Entity id to delete
	 */
	public void delete(Long id) {
		try {
			log.debug(String.format("Deleting entity %s with id = %s",
					entityClass.getName(), id));
			persistenceService.remove(id);
			Messages.addGlobalInfo("message.entity.delete.ok", new Object[] {});
		} catch (Throwable t) {
			if (t.getCause() instanceof EntityExistsException) {
				log.error(
						"delete was unsuccessful because entity is used in the system",
						t);
				Messages.addGlobalError("message.entity.delete.entityUse	",
						new Object[] {});

			} else {
				log.error("unexpected exception when deleting!", t);
				Messages.addGlobalError("message.entity.delete.unexpected",
						new Object[] {});
			}
		}
	}

	public void delete() {
		try {
			log.debug(String.format("Deleting entity %s with id = %s",
					entityClass.getName(), entity.getId()));
			persistenceService.remove((Long) entity.getId());
			Messages.addGlobalInfo("message.entity.delete.ok", new Object[] {});
		} catch (Throwable t) {
			if (t.getCause() instanceof EntityExistsException) {
				log.error(
						"delete was unsuccessful because entity is used in the system",
						t);
				Messages.addGlobalError("message.entity.delete.entityUse	",
						new Object[] {});

			} else {
				log.error("unexpected exception when deleting!", t);
				Messages.addGlobalError("message.entity.delete.unexpected",
						new Object[] {});
			}
		}
	}

}
