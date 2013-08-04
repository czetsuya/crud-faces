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
package org.manaty.view.converter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;

import org.manaty.util.ParamBean;

@FacesConverter("dateConverter")
public class DateConverter implements Converter {

	@Inject
	private ParamBean paramBean;

	@Override
	public Object getAsObject(FacesContext facesContext,
			UIComponent uIComponent, String str) {
		String dateFormat = paramBean.getProperty("format.date", "dd/MM/yyyy");
		DateFormat df = new SimpleDateFormat(dateFormat, FacesContext
				.getCurrentInstance().getViewRoot().getLocale());

		try {
			return df.parse(str);
		} catch (ParseException e) {
			return "";
		}
	}

	@Override
	public String getAsString(FacesContext facesContext,
			UIComponent uIComponent, Object obj) {
		String dateFormat = paramBean.getProperty("format.date", "dd/MM/yyyy");
		DateFormat df = new SimpleDateFormat(dateFormat, FacesContext
				.getCurrentInstance().getViewRoot().getLocale());

		return df.format(obj);
	}

}
