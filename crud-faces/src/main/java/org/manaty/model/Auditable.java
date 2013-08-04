package org.manaty.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.manaty.model.user.User;

@Embeddable
public class Auditable implements Serializable {

	private static final long serialVersionUID = 1L;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CREATED", nullable = false, updatable = false)
	private Date created;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "UPDATED")
	private Date updated;

	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "CREATOR_ID", updatable = false)
	private User creator;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "UPDATER_ID")
	private User updater;

	public Auditable() {
	}

	public Auditable(User creator) {
		super();
		this.creator = creator;
		this.created = new Date();
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Date getUpdated() {
		return updated;
	}

	public void setUpdated(Date updated) {
		this.updated = updated;
	}

	public User getCreator() {
		return creator;
	}

	public void setCreator(User creator) {
		this.creator = creator;
	}

	public User getUpdater() {
		return updater;
	}

	public void setUpdater(User updater) {
		this.updater = updater;
	}

	public Date getLastModified() {
		return (updated != null) ? updated : created;
	}

	public User getLastUser() {
		return (updater != null) ? updater : creator;
	}

	public void updateWith(User currentUser) {
		this.updated = new Date();
		this.updater = currentUser;
	}
}
