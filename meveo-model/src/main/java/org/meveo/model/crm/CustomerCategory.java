/*
 * (C) Copyright 2015-2016 Opencell SAS (http://opencellsoft.com/) and contributors.
 * (C) Copyright 2009-2014 Manaty SARL (http://manaty.net/) and contributors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * This program is not suitable for any direct or indirect application in MILITARY industry
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.meveo.model.crm;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Size;

import org.meveo.model.BusinessEntity;
import org.meveo.model.ExportIdentifier;

@Entity
@ExportIdentifier({ "code", "provider" })
@Table(name = "CRM_CUSTOMER_CATEGORY", uniqueConstraints = @UniqueConstraint(columnNames = { "CODE", "PROVIDER_ID" }))
@SequenceGenerator(name = "ID_GENERATOR", sequenceName = "CRM_CUSTOMER_CATEGORY_SEQ")
public class CustomerCategory extends BusinessEntity {

	private static final long serialVersionUID = 1L;

	@Column(name = "EXONERATED_FROM_TAXES")
	private boolean exoneratedFromTaxes=false ;
	
	@Column(name = "EXONERATION_TAX_EL", length = 2000)
	@Size(max = 2000)
	private String exonerationTaxEl;
	
	@Column(name = "EXONERATION_REASON", length = 255)
    @Size(max = 255)
	private String exonerationReason;

	public boolean getExoneratedFromTaxes() {
		return exoneratedFromTaxes;
	}

	public void setExoneratedFromTaxes(boolean exoneratedFromTaxes) {
		this.exoneratedFromTaxes = exoneratedFromTaxes;
	}

	/**
	 * @return the exonerationTaxEl
	 */
	public String getExonerationTaxEl() {
		return exonerationTaxEl;
	}

	/**
	 * @param exonerationTaxEl the exonerationTaxEl to set
	 */
	public void setExonerationTaxEl(String exonerationTaxEl) {
		this.exonerationTaxEl = exonerationTaxEl;
	}

	/**
	 * @return the exonerationReason
	 */
	public String getExonerationReason() {
		return exonerationReason;
	}

	/**
	 * @param exonerationReason the exonerationReason to set
	 */
	public void setExonerationReason(String exonerationReason) {
		this.exonerationReason = exonerationReason;
	}
	
	
	
}
