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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

//FIXME: manage correctly auditability
@Entity()
@Table(name = "ENTITY_COMMENT")
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "ENTITY_COMMENT_SEQ")
public class EntityComment extends AuditableEntity {

	private static final long serialVersionUID = 1L;

	@JoinColumn(name = "ENTITY_ID")
	private Long entityId;

	@Column(name = "ENTITY_NAME", length = 50)
	private String entityName;

	@Column(name = "FIELD_NAME", length = 50)
	private String fieldName;

	@Column(name = "COMMENT", length = 500)
	private String comment;

	public EntityComment(Long id, String entityName, String fieldName, String comment) {
		super();
		this.entityId = id;
		this.entityName = entityName;
		this.fieldName = fieldName;
		this.comment = comment;
	}

	public EntityComment() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Long getEntityId() {
		return entityId;
	}

	public void setEntityId(Long entityId) {
		this.entityId = entityId;
	}

	public String getEntityName() {
		return entityName;
	}

	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

}
