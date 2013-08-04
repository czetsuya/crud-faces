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

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateless
public class ValidationService {

	@PersistenceContext(unitName = "CRUDFaces")
	private EntityManager em;

	/**
	 * @see org.meveo.service.validation.ValidationServiceLocal#validateUniqueField(java.lang.String,
	 *      java.lang.String, java.lang.String, java.lang.Object)
	 */
	public boolean validateUniqueField(String className, String fieldName,
			Object id, Object value) {

		// Proxy classes contain a name in "..._$$_javassist.. format" If a
		// proxy class object claname was passed, string the ending
		// "_$$_javassist.."to obtain real class name
		int pos = className.indexOf("_$$_java");
		if (pos > 0) {
			className = className.substring(0, pos);
		}

		StringBuilder queryStringBuilder = new StringBuilder(
				"select count(*) from ").append(className).append(" where ")
				.append(fieldName).append(" = '").append(value).append("'");
		if (id != null) {
			queryStringBuilder.append(" and id != ").append(id);
		}
		Query query = em.createQuery(queryStringBuilder.toString());
		long count = (Long) query.getSingleResult();
		return count == 0L;
	}

}
