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
package org.manaty.view.composite;

import java.util.Map;

import javax.faces.component.FacesComponent;

/**
 * Backing UINamingContainer for searchField.xhtml composite component.
 */
@FacesComponent(value = "searchField")
public class SearchFieldCompositeComponent extends
		BackingBeanBasedCompositeComponent {

	/**
	 * Helper method to get filters from backing bean.
	 */
	public Map<String, Object> getFilters() {
		return super.getBackingBeanFromParentOrCurrent().getFilters();
	}

	public String getFromRangeSearchFilterName() {
		return "fromRange-" + getAttributes().get("field");
	}

	public String getToRangeSearchFilterName() {
		return "toRange-" + getAttributes().get("field");
	}

	public String getFromRangeSearchFilterNameFromParent() {
		return "fromRange-"
				+ getCompositeComponentParent(this).getAttributes()
						.get("field");
	}

	public String getToRangeSearchFilterNameFromParent() {
		return "toRange-"
				+ getCompositeComponentParent(this).getAttributes()
						.get("field");
	}
}
