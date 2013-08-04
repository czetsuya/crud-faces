package org.manaty.service.account;

import javax.ejb.Stateless;

import org.manaty.model.account.Title;
import org.manaty.service.PersistenceService;

/**
 * @author Edward P. Legaspi
 * @since Jun 12, 2013
 **/
@Stateless
public class TitleService extends PersistenceService<Title> {

	private static final long serialVersionUID = 7314009150198113556L;

	public Title findByCode(String code) {
		Title title = null;
		try {
			title = (Title) getEntityManager()
					.createQuery("from Title t where t.code=:code")
					.setParameter("code", code).getSingleResult();
		} catch (Exception e) {
			return null;
		}

		return title;
	}

}
