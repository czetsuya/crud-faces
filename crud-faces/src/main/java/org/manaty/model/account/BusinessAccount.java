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

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.manaty.model.AuditableEntity;
import org.manaty.model.account.AccountType.NameTypeEnum;
import org.manaty.model.user.User;

@Entity
@Table(name = "MEVEO_ACCOUNT")
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "MEVEO_ACCOUNT_SEQ")
public class BusinessAccount extends AuditableEntity {

	private static final long serialVersionUID = -59836209353478741L;

	@Column(name = "CODE", length = 50, unique = true)
	private String code;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "ACCOUNT_TYPE_ID")
	private AccountType accountType;

	@Embedded
	private Name name = new Name();

	@Column(name = "NAME_FOR_SEARCH", length = 150)
	private String nameForSearch;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PARENT_ACCOUNT_ID")
	private BusinessAccount parentAccount;

	@Embedded
	private Contacts contacts = new Contacts();

	@Column(name = "extRef1", length = 50)
	private String extRef1;

	@Column(name = "extRef2", length = 50)
	private String extRef2;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "PRINCIPAL_CONTACT_ID")
	private User principalContact;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "RESPONSIBLE_CONTACT_ID")
	private User responsibleContact;

	@Column(name = "DISABLED", nullable = false)
	private boolean disabled;

	public void setCode(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}

	public void setAccountType(AccountType accountType) {
		this.accountType = accountType;
	}

	public AccountType getAccountType() {
		return accountType;
	}

	public String getNameForSearch() {
		return nameForSearch;
	}

	public void setNameForSearch(String nameForSearch) {
		this.nameForSearch = nameForSearch;
	}

	public BusinessAccount getParentAccount() {
		return parentAccount;
	}

	public void setParentAccount(BusinessAccount parentAccount) {
		this.parentAccount = parentAccount;
	}

	public Contacts getContacts() {
		if (contacts == null) {
			contacts = new Contacts();
		}
		return contacts;
	}

	public void setContacts(Contacts contacts) {
		this.contacts = contacts;
	}

	public void setExtRef1(String extRef1) {
		this.extRef1 = extRef1;
	}

	public String getExtRef1() {
		return extRef1;
	}

	public void setExtRef2(String extRef2) {
		this.extRef2 = extRef2;
	}

	public String getExtRef2() {
		return extRef2;
	}

	public User getPrincipalContact() {
		return principalContact;
	}

	public void setPrincipalContact(User principalContact) {
		this.principalContact = principalContact;
	}

	public User getResponsibleContact() {
		return responsibleContact;
	}

	public void setResponsibleContact(User responsibleContact) {
		this.responsibleContact = responsibleContact;
	}

	public boolean isDisabled() {
		return disabled;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	public boolean isActive() {
		return !disabled;
	}

	public String getBusinessName() {
		if (accountType.getNameTypeEnum() == NameTypeEnum.business) {
			return name != null ? name.getBusinessName() : null;
		} else {
			return name != null ? (name.getFirstName() == null ? "" : (name
					.getFirstName() + " ") + name.getLastName() == null ? ""
					: name.getLastName()) : null;
		}
	}

	public Name getName() {
		return name;
	}

	public void setName(Name name) {
		this.name = name;
	}
}