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
package org.manaty.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

@SuppressWarnings("rawtypes")
@MappedSuperclass
public abstract class BaseEntity implements Serializable, IEntity, Cloneable,
		Comparable {

	private static final long serialVersionUID = -3545803775832286284L;

	/**
	 * Record identifier
	 */
	@Id
	@GeneratedValue(generator = "ID_GENERATOR")
	@Column(name = "ID")
	private Long id;

	/**
	 * Version of the information stored in the record
	 */
	@Version
	@Column(name = "VERSION")
	private Integer version;

	/**
	 * @return Record identifier
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id
	 *            Record identifier
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return Version of the information stored in the record
	 */
	public Integer getVersion() {
		return version;
	}

	/**
	 * @param version
	 *            Version of the information stored in the record
	 */
	public void setVersion(Integer version) {
		this.version = version;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

	@Override
	public int hashCode() {
		if (getId() == null)
			return super.hashCode();
		return getId().hashCode();
	}

	public boolean isTransient() {
		return id == null;
	}

	/**
	 * ImplÃ©mentation par dÃ©faut de l'equal
	 * 
	 * Attention ! : ne pas oublier de surcharger cette mÃ©thode pour prendre en
	 * compte une business key lorsque l'on ajoute par exemple des instances non
	 * persistentes Ã  une Set
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (obj == null) {
			return false;
		} else if (!(obj instanceof BaseEntity)) {
			return false;
		}
		final BaseEntity other = (BaseEntity) obj;
		if (getId() == null) {
			return false;
		} else if (!getId().equals(other.getId()))
			return false;
		return true;
	}

	/**
	 * On classe par dÃ©faut du plus rÃ©cent au plus ancien
	 */
	public int compareTo(Object o) {
		if (o == null || !(o instanceof BaseEntity))
			return 1;

		if (this == o)
			return 0;

		final BaseEntity other = (BaseEntity) o;

		if (getId() == null && other.getId() == null)
			return 0;

		if (other.getId() == null)
			return 1;
		if (getId() == null)
			return -1;

		return getId().compareTo(other.getId());
	}

}
