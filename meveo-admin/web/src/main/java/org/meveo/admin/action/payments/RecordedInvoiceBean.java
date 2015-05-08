/*
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.admin.action.payments;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.seam.international.status.builder.BundleKey;
import org.meveo.admin.action.BaseBean;
import org.meveo.admin.exception.BusinessException;
import org.meveo.model.payments.RecordedInvoice;
import org.meveo.service.base.PersistenceService;
import org.meveo.service.base.local.IPersistenceService;
import org.meveo.service.payments.impl.RecordedInvoiceService;
import org.omnifaces.cdi.ViewScoped;
import org.primefaces.context.RequestContext;

/**
 * Standard backing bean for {@link RecordedInvoice} (extends {@link BaseBean}
 * that provides almost all common methods to handle entities filtering/sorting
 * in datatable, their create, edit, view, delete operations). It works with
 * Manaty custom JSF components.
 */
@Named
@ViewScoped
public class RecordedInvoiceBean extends BaseBean<RecordedInvoice> {

	private static final long serialVersionUID = 1L;

	/**
	 * Injected @{link RecordedInvoice} service. Extends
	 * {@link PersistenceService}.
	 */
	@Inject
	private RecordedInvoiceService recordedInvoiceService;

	/**
	 * Constructor. Invokes super constructor and provides class type of this
	 * bean for {@link BaseBean}.
	 */
	public RecordedInvoiceBean() {
		super(RecordedInvoice.class);
	}

	/**
	 * Factory method for entity to edit. If objectId param set load that entity
	 * from database, otherwise create new.
	 * 
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	@Produces
	@Named("recordedInvoice")
	public RecordedInvoice init() {
		return initEntity();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.meveo.admin.action.BaseBean#saveOrUpdate(boolean)
	 */
	@Override
	public String saveOrUpdate(boolean killConversation)
			throws BusinessException {
		entity.getCustomerAccount().getAccountOperations().add(entity);

		return super.saveOrUpdate(killConversation);
	}

	/**
	 * @see org.meveo.admin.action.BaseBean#getPersistenceService()
	 */
	@Override
	protected IPersistenceService<RecordedInvoice> getPersistenceService() {
		return recordedInvoiceService;
	}

	public String addLitigation() {
		try {
			recordedInvoiceService.addLitigation(entity, getCurrentUser());
			messages.info(new BundleKey("messages",
					"customerAccount.addLitigationSuccessful"));
		} catch (Exception e) {
			log.error(e.getMessage());
			messages.error(e.getMessage());
		}
		return null;
	}

	public String cancelLitigation() {

		try {
			recordedInvoiceService.cancelLitigation(entity, getCurrentUser());
			messages.info(new BundleKey("messages",
					"customerAccount.cancelLitigationSuccessful"));
		} catch (Exception e) {
			log.error(e.getMessage());
			messages.error(e.getMessage());
		}
		return null;
	}

	@Override
	protected void canDelete() {
		boolean result=true;
		this.delete();
		RequestContext requestContext = RequestContext.getCurrentInstance();
		requestContext.addCallbackParam("result", result);
	}

}

