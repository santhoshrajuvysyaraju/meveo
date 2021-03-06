package org.meveo.api.dto.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.meveo.api.dto.CountryDto;

/**
 * @author Edward P. Legaspi
 * @since Oct 7, 2013
 **/
@XmlRootElement(name = "GetTradingCountryResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class GetTradingCountryResponse extends BaseResponse {

	private static final long serialVersionUID = -7308813550235264178L;

	private CountryDto country;

	public GetTradingCountryResponse() {
		super();
	}

	public CountryDto getCountry() {
		return country;
	}

	public void setCountry(CountryDto country) {
		this.country = country;
	}

	@Override
	public String toString() {
		return "GetTradingCountryResponse [country=" + country + ", toString()=" + super.toString() + "]";
	}

}
