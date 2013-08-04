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
import java.util.List;

import org.manaty.bean.PaginationConfiguration;
import org.manaty.service.IPersistenceService;
import org.primefaces.model.LazyDataModel;

public class PaginationDataModel<T extends IEntity> extends LazyDataModel<T> {

	private static final long serialVersionUID = -5965132119478883892L;

	private IPersistenceService<T> service;

	@SuppressWarnings("unused")
	private List<String> fetchFields = new ArrayList<String>();

	public PaginationDataModel() {
		super();
	}

	/**
	 * Setup ordering of fields
	 * 
	 * @param defaultSortField
	 *            Field that should be sorted by default (or null if none)
	 * @param sortAscending
	 *            Sort order for default sorting field
	 * @param unsortedFields
	 *            Remaining fields
	 */
	public PaginationDataModel(String defaultSortField, boolean sortAscending,
			String... unsortedFields) {
		super();
		// FIXME : edward please have a look to this, how do we implement this
		// in primefaces
		/*
		 * for (String fieldName : unsortedFields) { sortOrders.put(fieldName,
		 * SortOrder.unsorted); }
		 * 
		 * if (defaultSortField != null && defaultSortField.length() > 0) {
		 * sortOrders.put(defaultSortField, sortAscending ? SortOrder.ascending
		 * : SortOrder.descending); }
		 */
	}

	/**
	 * Constructor.
	 * 
	 * @param service
	 *            Persistence service for concrete entity implementation. For
	 */
	public PaginationDataModel(IPersistenceService<T> service) {
		this.service = service;
	}

	protected List<T> loadData(PaginationConfiguration paginatingData) {
		List<T> result = (List<T>) service.list(paginatingData);
		setRowCount((int) service.count(paginatingData));
		setPageSize(paginatingData.getNumberOfRows());
		return result;
	}

}
