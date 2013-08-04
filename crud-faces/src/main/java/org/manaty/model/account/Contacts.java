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

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Contacts implements Serializable {

	private static final long serialVersionUID = 809478035495341394L;

	@Column(name = "PHONE", length = 20)
	private String phone;

	@Column(name = "MOBILE", length = 20)
	private String mobile;

	@Column(name = "FAX", length = 20)
	private String fax;

	@Column(name = "SKYPE", length = 20)
	private String Skype;

	@Column(name = "POSTAL_CODE", length = 5)
	private String postalCode;

	@Column(name = "CITY", length = 20)
	private String city;

	@Column(name = "ADDRESS1", length = 255)
	private String address1;

	@Column(name = "ADDRESS2", length = 255)
	private String address2;

	@Column(name = "ADDRESS3", length = 255)
	private String address3;

	public Contacts() {
		super();
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public String getAddress1() {
		return address1;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public String getAddress2() {
		return address2;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	public String getAddress3() {
		return address3;
	}

	public void setAddress3(String address3) {
		this.address3 = address3;
	}

	public String getSkype() {
		return Skype;
	}

	public void setSkype(String skype) {
		Skype = skype;
	}

	public String getPostalCode() {
		return postalCode;
	}

	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

}
