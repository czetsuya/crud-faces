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
package org.manaty.view;

import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.faces.component.FacesComponent;
import javax.faces.context.FacesContext;

import org.primefaces.component.datatable.DataTable;

/**
 * @author Edward P. Legaspi
 * @since Jun 5, 2013
 **/
@FacesComponent(value = "ExtendedPrimefacesDatatable")
public class ExtendedPrimefacesDatatable extends DataTable {

	/**
	 * @see org.primefaces.component.datatable.DataTable#resolveSortField()
	 */
	@Override
	public String resolveStaticField(ValueExpression expression) {
		if (expression != null) {
			FacesContext context = getFacesContext();
			ELContext eLContext = context.getELContext();

			return (String) expression.getValue(eLContext);
		} else {
			return null;
		}
	}

}