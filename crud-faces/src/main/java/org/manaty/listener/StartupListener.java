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
package org.manaty.listener;

import java.text.MessageFormat;
import java.util.ResourceBundle;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;

import org.omnifaces.util.Messages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Edward P. Legaspi
 * @since Jun 7, 2013
 **/
@Singleton
@Startup
public class StartupListener {

	private Logger log = LoggerFactory.getLogger(StartupListener.class);

	@Inject
	private ResourceBundle resourceBundle;

	@PostConstruct
	private void init() {
		log.info("Thank you for running CRUD Faces code. For improvements you may contact the developer at czetsuya@gmail.com");

		Messages.setResolver(new Messages.Resolver() {
			public String getMessage(String message, Object... params) {
				if (resourceBundle.containsKey(message)) {
					message = resourceBundle.getString(message);
				}

				return MessageFormat.format(message, params);
			}
		});
	}

}
