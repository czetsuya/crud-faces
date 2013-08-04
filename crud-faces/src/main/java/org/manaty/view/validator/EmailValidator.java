package org.manaty.view.validator;

import java.text.MessageFormat;
import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.manaty.util.StringUtils;

@FacesValidator("emailValidator")
public class EmailValidator implements Validator {

	@Inject
	private transient ResourceBundle resourceMessages;

	public void validate(FacesContext context, UIComponent component,
			Object value) throws ValidatorException {

		if (!StringUtils.isBlank(value)) {
			try {
				InternetAddress emailAddr = new InternetAddress(
						value.toString());
				emailAddr.validate();
			} catch (AddressException ex) {
				FacesMessage facesMessage = new FacesMessage();
				String message = resourceMessages
						.getString("message.error.invalidEmail");
				message = MessageFormat.format(message,
						getLabel(context, component));
				facesMessage.setDetail(message);
				facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);

				throw new ValidatorException(facesMessage);
			}
		}

	}

	private Object getLabel(FacesContext context, UIComponent component) {

		Object o = component.getAttributes().get("label");
		if (o == null || (o instanceof String && ((String) o).length() == 0)) {
			o = component.getValueExpression("label");
		}
		// Use the "clientId" if there was no label specified.
		if (o == null) {
			o = component.getClientId(context);
		}
		return o;
	}

}
