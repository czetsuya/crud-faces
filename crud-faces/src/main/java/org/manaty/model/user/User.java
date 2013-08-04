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
package org.manaty.model.user;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.manaty.model.AuditableEntity;
import org.manaty.model.account.AccountType;
import org.manaty.model.account.BusinessAccount;
import org.manaty.model.account.Contacts;
import org.manaty.model.account.Name;
import org.manaty.model.constraint.Email;

@Entity
@Table(name = "MEVEO_USER")
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "MEVEO_USER_SEQ")
public class User extends AuditableEntity {

	private static final long serialVersionUID = 315516639923338933L;

	@Embedded
	private Name name = new Name();

	@Embedded
	private Contacts contact = new Contacts();

	@NotNull
	@Column(name = "USERNAME", length = 70, unique = true)
	private String username;

	@Column(name = "PASSWORD", length = 50)
	private String password;

	@NotNull
	@Column(name = "EMAIL", length = 100, nullable = false)
	@Email
	private String email;

	@Column(name = "PREFERRED_LOCALE", length = 5)
	private String preferredLocale;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
	@JoinColumn(name = "ACCOUNT_ID")
	private BusinessAccount businessAccount;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "MEVEO_USER_ROLE", joinColumns = @JoinColumn(name = "USER_ID"), inverseJoinColumns = @JoinColumn(name = "ROLE_ID"))
	// @ForeignKey(name = "TO_USER_FK", inverseName = "TO_ROLE_FK")
	// @org.hibernate.annotations.Cache(usage =
	// org.hibernate.annotations.CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
	private List<Role> roles = new ArrayList<Role>();

	@Temporal(TemporalType.DATE)
	@Column(name = "LAST_PASSWORD_MODIFICATION")
	private Date lastPasswordModification;

	@Column(name = "DISABLED", nullable = false)
	private boolean disabled;

	@Transient
	private String newPassword;

	@Transient
	private String newPasswordConfirmation;

	public User() {
	}

	public User(Name name, String username, String newPassword, String email,
			BusinessAccount businessAccount) {
		super();
		this.name = name;
		this.username = username;
		this.newPassword = newPassword;
		this.email = email;
		this.businessAccount = businessAccount;
	}

	public List<Role> getRoles() {
		return roles;
	}

	public void setRoles(List<Role> val) {
		this.roles = val;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Date getLastPasswordModification() {
		return lastPasswordModification;
	}

	public void setLastPasswordModification(Date lastPasswordModification) {
		this.lastPasswordModification = lastPasswordModification;
	}

	public boolean isPasswordExpired(int expiracyInDays) {
		boolean result = true;

		if (lastPasswordModification != null) {
			long diffMilliseconds = System.currentTimeMillis()
					- lastPasswordModification.getTime();
			result = (expiracyInDays - diffMilliseconds / (24 * 3600 * 1000L)) < 0;
		}

		return result;
	}

	public Name getName() {
		return name;
	}

	public void setName(Name name) {
		this.name = name;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public String getNewPasswordConfirmation() {
		return newPasswordConfirmation;
	}

	public void setNewPasswordConfirmation(String newPasswordConfirmation) {
		this.newPasswordConfirmation = newPasswordConfirmation;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = StringUtils.isBlank(email) ? null : email;
	}

	public String getPreferredLocale() {
		return preferredLocale;
	}

	public void setPreferredLocale(String preferredLocale) {
		this.preferredLocale = preferredLocale;
	}

	public void setBusinessAccount(BusinessAccount businessAccount) {
		this.businessAccount = businessAccount;
	}

	public BusinessAccount getBusinessAccount() {
		return businessAccount;
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

	public boolean isNewPasswordSet() {
		// return !StringUtil.isBlank(newPassword);
		return !(newPassword == null || "".equals(newPassword));
	}

	public Contacts getContact() {
		return contact;
	}

	public void setContact(Contacts contact) {
		this.contact = contact;
	}

	public String getRolesLabel() {
		StringBuffer sb = new StringBuffer();
		if (roles != null)
			for (Role r : roles) {
				if (sb.length() != 0)
					sb.append(", ");
				sb.append(r.getDescription());
			}
		return sb.toString();
	}

	public String getShortRolesLabel() {
		StringBuffer sb = new StringBuffer();
		if (roles != null)
			for (Role r : roles) {
				if (sb.length() != 0) {
					sb.append(", ");
				}
				sb.append(r.getDescription());
			}
		return sb.toString();
	}

	public boolean hasRole(String role) {
		boolean result = false;
		if (role != null && roles != null) {
			for (Role r : roles) {
				result = role.equalsIgnoreCase(r.getName());
				if (result) {
					break;
				}
			}
		}
		return result;
	}

	/**
	 * Get a list of account types that user can manage - collect from all user
	 * roles
	 * 
	 * @return
	 */
	public Set<AccountType> getManageableAccountTypes() {
		Set<AccountType> types = new HashSet<AccountType>();

		for (Role role : roles) {
			types.addAll(role.getManageableAccountTypes());
		}

		return types;
	}

	@Override
	public String toString() {
		return "User [name=" + name + ", username=" + username + ", password="
				+ password + ", email=" + email + ", preferredLocale="
				+ preferredLocale + ", businessAccount="
				+ (businessAccount != null ? businessAccount.getId() : null)
				+ ", roles=" + roles + ", disabled=" + disabled + "]";
	}

}