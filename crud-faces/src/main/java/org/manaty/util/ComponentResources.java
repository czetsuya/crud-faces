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
package org.manaty.util;

import java.io.Serializable;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.faces.context.FacesContext;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ComponentResources implements Serializable {

	private static final long serialVersionUID = 1L;

	private Locale locale = Locale.ENGLISH;

	@Produces
	public ResourceBundle getResourceBundle() {
		ResourceBundle result = null;
		if (FacesContext.getCurrentInstance() != null) {
			try {
				locale = FacesContext.getCurrentInstance().getViewRoot()
						.getLocale();
			} catch (Exception e) {
			}
		}
		result = ResourceBundle.getBundle("messages", locale);
		return result;
	}

	@Produces
	@ApplicationScoped
	@MeveoParamBean
	@Named
	public ParamBean getParamBean() {
		return ParamBean.getInstance();
	}

	@Produces
	public Logger createLogger(InjectionPoint injectionPoint) {
		return LoggerFactory.getLogger(injectionPoint.getMember()
				.getDeclaringClass().getName());
	}

}