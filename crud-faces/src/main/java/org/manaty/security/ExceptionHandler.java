package org.manaty.security;

import java.io.IOException;

import javax.enterprise.context.NonexistentConversationException;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import org.jboss.seam.security.AuthorizationException;
import org.jboss.solder.exception.control.CaughtException;
import org.jboss.solder.exception.control.Handles;
import org.jboss.solder.exception.control.HandlesExceptions;
import org.jboss.solder.servlet.http.ContextPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@HandlesExceptions
public class ExceptionHandler {

	@Inject
	@ContextPath
	private String contextPath;

	private Logger log = LoggerFactory.getLogger(ExceptionHandler.class);

	public void handleAuthorizationException(
			@Handles CaughtException<AuthorizationException> evt) {

		evt.handled();

		try {
			FacesContext.getCurrentInstance().getExternalContext()
					.redirect(contextPath + "/errors/403.jsf");

		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	public void handleInvalidConversationException(
			@Handles CaughtException<NonexistentConversationException> evt) {

		evt.handled();

		try {
			FacesContext.getCurrentInstance().getExternalContext()
					.redirect(contextPath + "/errors/sessionExpired.jsf");

		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	public void handleViewExpiredException(
			@Handles CaughtException<javax.faces.application.ViewExpiredException> evt) {

		evt.handled();

		try {
			FacesContext.getCurrentInstance().getExternalContext()
					.redirect(contextPath + "/errors/sessionExpired.jsf");
			
		} catch (IOException e) {
			log.error(e.getMessage());
		} catch (Exception e) {
			log.error(e.getMessage());
		}
	}

	// public void handleIllegalStateException(@Handles
	// CaughtException<IllegalStateException> evt) {
	//
	// evt.handled();
	//
	// try {
	// facesContext.getExternalContext().redirect(contextPath +
	// "/errors/sessionExpired.jsf");
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// }

}