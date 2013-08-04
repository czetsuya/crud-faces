package org.manaty.security;

import org.jboss.seam.faces.security.AccessDeniedView;
import org.jboss.seam.faces.security.LoginView;
import org.jboss.seam.faces.view.config.ViewConfig;
import org.jboss.seam.faces.view.config.ViewPattern;
import org.jboss.seam.security.annotations.LoggedIn;

@ViewConfig
public interface PagesConfig {

	static enum Pages1 {

		@ViewPattern("/*")
		@LoginView("/login.jsf?faces-redirect=true")
		@AccessDeniedView("/errors/403.xhtml")
		ALL,

		@ViewPattern("/errors/*")
		// @Admin(restrictAtPhase=RESTORE_VIEW)
		ERRORS,

		@ViewPattern("/home.xhtml")
		// @Admin(restrictAtPhase=RESTORE_VIEW)
		HOME,

		@ViewPattern("/changePassword.xhtml")
		@LoggedIn()
		CHANGE_PSWD,

		@ViewPattern("/administration/*")
		@LoggedIn()
		// @Admin(restrictAtPhase=RESTORE_VIEW)
		ADMIN

	}

}
