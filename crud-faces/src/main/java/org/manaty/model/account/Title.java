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
package org.manaty.model.account;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.manaty.model.BaseEntity;

@Entity
@Table(name = "MEVEO_TITLE")
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "MEVEO_TITLE_SEQ")
public class Title extends BaseEntity {

	private static final long serialVersionUID = -6137499337325838882L;

	@Column(name = "CODE", length = 4, unique = true)
	String code;

	@Column(name = "IS_COMPANY")
	boolean isCompany;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public boolean isCompany() {
		return isCompany;
	}

	public void setCompany(boolean isCompany) {
		this.isCompany = isCompany;
	}

	@Override
	public String toString() {
		return "$$_title." + code;
	}

}
