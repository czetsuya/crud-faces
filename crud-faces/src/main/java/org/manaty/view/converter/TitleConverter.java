package org.manaty.view.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;

import org.manaty.service.account.TitleService;
import org.manaty.util.StringUtils;

@FacesConverter("titleConverter")
public class TitleConverter implements Converter {

	@Inject
	private TitleService titleService;

	@Override
	public Object getAsObject(FacesContext context, UIComponent component,
			String value) {
		if (StringUtils.isBlank(value))
			return "";

		String titleCode = value.replace("$$_title.", "");

		return titleService.findByCode(titleCode);
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component,
			Object value) {
		return value.toString();
	}
}
