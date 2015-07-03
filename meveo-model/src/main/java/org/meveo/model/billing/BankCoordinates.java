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
package org.meveo.model.billing;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.Size;

@Embeddable
public class BankCoordinates implements Serializable, Cloneable {

	private static final long serialVersionUID = 1L;
	
	@Column(name = "ACCOUNT_IDENTIFIER",length = 255)
	@Size(max = 255)
	private String accountIdentifier; 

	@Column(name = "IBAN", length = 34)
	@Size(max = 34)
	private String iban;

	@Column(name = "BIC", length = 11)
	@Size(max = 11)
	private String bic;

	@Column(name = "ACCOUNT_OWNER", length = 50)
	@Size(max = 50)
	private String accountOwner;

	@Column(name = "BANK_NAME", length = 50)
	@Size(max = 50)
	private String bankName;

	@Column(name = "BANK_ID", length = 50)
	@Size(max = 50)
	private String bankId;

	@Column(name = "ISSUER_NUMBER", length = 50)
	@Size(max = 50)
	private String issuerNumber;

	@Column(name = "ISSUER_NAME", length = 50)
	@Size(max = 50)
	private String issuerName;
	
	@Column(name = "ICS", length = 35)
	@Size(max = 35)
	private String ics; //L'identifiant Créancier Sepa
	

	public BankCoordinates() {
	}

	public BankCoordinates(BankCoordinates bankCoordinates) {
		this(bankCoordinates.accountIdentifier,bankCoordinates.iban, bankCoordinates.bic,
				bankCoordinates.accountOwner, bankCoordinates.bankName);
	}

	public BankCoordinates(String accountIdentifier,String iban, String bic, String accountOwner, String bankName) {
		super();
		this.accountIdentifier = accountIdentifier;  
		this.iban = iban;
		this.bic = bic;
		this.accountOwner = accountOwner;
		this.bankName = bankName;
	}

	public String getAccountIdentifier() {
		return accountIdentifier;
	}

	public void setAccountIdentifier(String accountIdentifier) {
		this.accountIdentifier = accountIdentifier;
	}

	public String getIban() {
		return iban;
	}

	public void setIban(String iban) {
		this.iban = iban;
	}

	public String getBic() {
		return bic;
	}

	public void setBic(String bic) {
		this.bic = bic;
	}

	public String getAccountOwner() {
		return accountOwner;
	}

	public void setAccountOwner(String accountOwner) {
		this.accountOwner = accountOwner;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		BankCoordinates o = (BankCoordinates) super.clone();
		return o;
	}

	public void setBankId(String bankId) {
		this.bankId = bankId;
	}

	public String getBankId() {
		return bankId;
	}

	public void setIssuerNumber(String issuerNumber) {
		this.issuerNumber = issuerNumber;
	}

	public String getIssuerNumber() {
		return issuerNumber;
	}

	public void setIssuerName(String issuerName) {
		this.issuerName = issuerName;
	}

	public String getIssuerName() {
		return issuerName;
	}

	public String getIcs() {
		return ics;
	}

	public void setIcs(String ics) {
		this.ics = ics;
	}
	
	

}
