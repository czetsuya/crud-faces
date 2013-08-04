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
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.manaty.model.BaseEntity;

@Entity()
@Table(name = "MEVEO_ACCOUNT_TYPE")
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "MEVEO_ACCOUNT_TYPE_SEQ")
public class AccountType extends BaseEntity {

	private static final long serialVersionUID = 8616549142195627188L;

	public enum AccountTypeEnum {
		serviceProvider, customer
	};

	public enum NameTypeEnum {
		person, business, both;
	}

	@Enumerated(EnumType.STRING)
	@Column(name = "ACCOUNT_TYPE", length = 20)
	private AccountTypeEnum accountTypeEnum;

	@Column(name = "CODE", length = 50)
	private String code;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PARENT_ACCOUNT_TYPE_ID")
	private AccountType parentAccountType;

	@Enumerated(EnumType.STRING)
	@Column(name = "NAME_TYPE", length = 20)
	private NameTypeEnum nameTypeEnum = NameTypeEnum.business;

	@Column(name = "FIELDS_OPT", length = 500)
	private String optionalFields;

	@Column(name = "FIELDS_REQ", length = 500)
	private String requiredFields;

	public void setAccountTypeEnum(AccountTypeEnum accountTypeEnum) {
		this.accountTypeEnum = accountTypeEnum;
	}

	public AccountTypeEnum getAccountTypeEnum() {
		return accountTypeEnum;
	}

	public void setCode(String name) {
		this.code = name;
	}

	public String getCode() {
		return code;
	}

	public AccountType getParentAccountType() {
		return parentAccountType;
	}

	public NameTypeEnum getNameTypeEnum() {
		return nameTypeEnum;
	}

	public String getOptionalFields() {
		return optionalFields;
	}

	public String getRequiredFields() {
		return requiredFields;
	}
}