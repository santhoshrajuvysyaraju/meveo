package org.meveo.api;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.meveo.api.dto.CountryDto;
import org.meveo.api.exception.MeveoApiException;
import org.meveo.asg.api.CountryEventData;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.Auditable;
import org.meveo.model.admin.Currency;
import org.meveo.model.admin.User;
import org.meveo.model.billing.Country;
import org.meveo.model.billing.TradingCountry;
import org.meveo.model.billing.TradingCurrency;
import org.meveo.model.crm.Provider;
import org.meveo.service.admin.impl.CountryService;
import org.meveo.service.admin.impl.CurrencyService;
import org.meveo.service.admin.impl.LanguageService;
import org.meveo.service.admin.impl.TradingCurrencyService;
import org.meveo.service.admin.impl.UserService;
import org.meveo.service.billing.impl.TradingCountryService;
import org.meveo.service.crm.impl.ProviderService;

/**
 * @author Edward P. Legaspi
 * @since Oct 4, 2013
 **/
@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class CountryServiceApi extends BaseApi {

	@Inject
	private CountryService countryService;

	@Inject
	private TradingCountryService tradingCountryService;

	@Inject
	private CurrencyService currencyService;

	@Inject
	private LanguageService languageService;

	@Inject
	private ProviderService providerService;

	@Inject
	private UserService userService;

	@Inject
	private TradingCurrencyService tradingCurrencyService;

	public void create(CountryDto countryDto) throws MeveoApiException {
		if (!StringUtils.isBlank(countryDto.getCountryCode())
				&& !StringUtils.isBlank(countryDto.getName())
				&& !StringUtils.isBlank(countryDto.getCurrencyCode())) {

			// If countryCode exist in the trading country table
			// ("billing_trading_country"), return error.
			Provider provider = providerService.findById(countryDto
					.getProviderId());
			User currentUser = userService.findById(countryDto
					.getCurrentUserId());
			TradingCountry tradingCountry = tradingCountryService
					.findByTradingCountryCode(countryDto.getCountryCode(),
							provider);

			if (StringUtils.isBlank(countryDto.getCurrencyCode())) {
				throw new MeveoApiException("Currency code="
						+ countryDto.getCurrencyCode() + " does not exists.");
			}

			if (tradingCountry == null) {
				// check currency
				Country country = new Country();

				country = countryService
						.findByCode(countryDto.getCountryCode());
				Currency currency = null;
				Auditable auditable = new Auditable();

				// If country code doesn't exist in the reference table, create
				// the country in this table ("adm_country") with the currency
				// code for the default provider.
				if (country == null) {
					// If country code don't exist in the reference table,
					// create the country in this table ("adm_country") with the
					// currency code for the default provider.
					country = new Country();
					auditable.setCreated(new Date());
					auditable.setCreator(currentUser);
					country.setDescriptionEn(countryDto.getName());
					country.setCountryCode(countryDto.getCountryCode());
				} else {
					// If country code exist in the reference table but the
					// currencyCode values in the two tables are different,
					// change the value of Currencycode in the country reference
					auditable.setUpdated(new Date());
					auditable.setUpdater(currentUser);
				}
				country.setAuditable(auditable);

				if (countryDto.getCurrencyCode() != null) { //
					currency = currencyService.findByCode(countryDto
							.getCurrencyCode());
					// If currencyCode don't exist in reference table
					// ("adm_currency"), return error.
					if (currency == null) {
						throw new MeveoApiException("Currency code="
								+ countryDto.getCurrencyCode()
								+ " does not exists.");
					}
				} else {
					if (provider.getCurrency() != null) {
						currency = provider.getCurrency();
					}
				}
				country.setCurrency(currency);

				if (country.isTransient()) {
					countryService.create(em, country, currentUser, provider);
				} else {
					// countryService.update(em, country, currentUser);
				}

				Auditable auditableTrading = new Auditable();
				auditableTrading.setCreated(new Date());
				auditableTrading.setCreator(currentUser);

				// If country don't exist in the trading country table, create
				// the country in this table ("billing_trading_country").
				tradingCountry = new TradingCountry();
				tradingCountry.setCountry(country);
				tradingCountry.setProvider(provider);
				tradingCountry.setActive(true);
				tradingCountry.setPrDescription(countryDto.getName());
				tradingCountry.setAuditable(auditableTrading);
				tradingCountryService.create(em, tradingCountry, currentUser,
						provider);

				// If currencyCode exist in reference table ("adm_currency") and
				// don't exist in the trading currency table, create the
				// currency in the trading currency table
				// ('billing_trading_currency").
				if (!StringUtils.isBlank(countryDto.getCurrencyCode())
						&& tradingCurrencyService.findByTradingCurrencyCode(
								countryDto.getCurrencyCode(), provider) != null) {
					TradingCurrency tradingCurrency = new TradingCurrency();
					tradingCurrency.setActive(true);
					tradingCurrency.setCurrency(currency);
					tradingCurrency.setAuditable(auditableTrading);
					tradingCurrency.setCurrencyCode(countryDto
							.getCurrencyCode());
					tradingCurrency.setPrDescription(countryDto
							.getCurrencyCode());
					tradingCurrencyService.create(em, tradingCurrency,
							currentUser, provider);
				}
			} else {
				throw new MeveoApiException("Trading country code="
						+ tradingCountry.getCountryCode() + " already exists.");
			}

		} else {
			StringBuilder sb = new StringBuilder(
					"The following parameters are required ");
			List<String> missingFields = new ArrayList<String>();

			if (StringUtils.isBlank(countryDto.getCountryCode())) {
				missingFields.add("countryCode");
			}
			if (StringUtils.isBlank(countryDto.getName())) {
				missingFields.add("name");
			}
			if (StringUtils.isBlank(countryDto.getCurrencyCode())) {
				missingFields.add("currencyCode");
			}
			if (missingFields.size() > 1) {
				sb.append(org.apache.commons.lang.StringUtils.join(
						missingFields.toArray(), ", "));
			} else {
				sb.append(missingFields.get(0));
			}
			sb.append(".");

			throw new MeveoApiException(sb.toString());
		}
	}

	public CountryDto find(String countryCode) throws MeveoApiException {
		if (!StringUtils.isBlank(countryCode)) {
			Country country = countryService.findByCode(countryCode);
			if (country != null) {
				return new CountryDto(country);
			}

			throw new MeveoApiException("Country code " + countryCode
					+ " does not exists.");
		} else {
			StringBuilder sb = new StringBuilder(
					"The following parameters are required ");
			List<String> missingFields = new ArrayList<String>();

			if (StringUtils.isBlank(countryCode)) {
				missingFields.add("countryCode");
			}
			if (missingFields.size() > 1) {
				sb.append(org.apache.commons.lang.StringUtils.join(
						missingFields.toArray(), ", "));
			} else {
				sb.append(missingFields.get(0));
			}
			sb.append(".");

			throw new MeveoApiException(sb.toString());
		}
	}

	public void remove(String countryCode, String currencyCode, Long providerId)
			throws MeveoApiException {
		Provider provider = providerService.findById(providerId);

		if (!StringUtils.isBlank(countryCode)
				&& !StringUtils.isBlank(currencyCode)) {
			TradingCountry tradingCountry = tradingCountryService
					.findByTradingCountryCode(countryCode, provider);
			Currency currency = currencyService.findByCode(currencyCode);
			if (tradingCountry != null && currency != null) {
				if (tradingCountry != null) {
					tradingCountryService.remove(tradingCountry);
				}
			} else {
				if (tradingCountry == null) {
					throw new MeveoApiException("Trading Country code="
							+ countryCode + " does not exists.");
				} else {
					throw new MeveoApiException("Currency code=" + currencyCode
							+ " does not exists.");
				}
			}
		} else {
			StringBuilder sb = new StringBuilder(
					"The following parameters are required ");
			List<String> missingFields = new ArrayList<String>();

			if (StringUtils.isBlank(countryCode)) {
				missingFields.add("countryCode");
			}
			if (StringUtils.isBlank(currencyCode)) {
				missingFields.add("currencyCode");
			}
			if (missingFields.size() > 1) {
				sb.append(org.apache.commons.lang.StringUtils.join(
						missingFields.toArray(), ", "));
			} else {
				sb.append(missingFields.get(0));
			}
			sb.append(".");

			throw new MeveoApiException(sb.toString());
		}
	}

	public void update(CountryDto countryDto) throws MeveoApiException {
		Provider provider = providerService
				.findById(countryDto.getProviderId());
		User currentUser = userService.findById(countryDto.getCurrentUserId());

		if (!StringUtils.isBlank(countryDto.getCountryCode())
				&& !StringUtils.isBlank(countryDto.getCurrencyCode())) {

			Currency currency = currencyService.findByCode(countryDto
					.getCurrencyCode());
			TradingCountry tradingCountry = tradingCountryService
					.findByTradingCountryCode(countryDto.getCountryCode(),
							provider);

			if (currency != null && tradingCountry != null) {
				Country country = countryService.findByCode(em,
						countryDto.getCountryCode());
				if (country != null
						&& !StringUtils.isBlank(countryDto.getName())) {
					if (!country.getDescriptionEn()
							.equals(countryDto.getName())) {
						country.setDescriptionEn(countryDto.getName());
						countryService.update(em, country, currentUser);
					}
				} else {
					throw new MeveoApiException("Country code="
							+ countryDto.getCountryCode() + " does not exists.");
				}

				TradingCurrency tradingCurrency = tradingCurrencyService
						.findByTradingCurrencyCode(
								countryDto.getCurrencyCode(), provider);
				if (tradingCurrency == null) {
					Auditable auditableTrading = new Auditable();
					auditableTrading.setCreated(new Date());
					auditableTrading.setCreator(currentUser);

					tradingCurrency = new TradingCurrency();
					tradingCurrency.setActive(true);
					tradingCurrency.setCurrency(currency);
					tradingCurrency.setAuditable(auditableTrading);
					tradingCurrency.setCurrencyCode(countryDto
							.getCurrencyCode());
					tradingCurrency.setPrDescription(countryDto
							.getCurrencyCode());
					tradingCurrencyService.create(em, tradingCurrency,
							currentUser, provider);
				}
			} else {
				if (currency == null) {
					throw new MeveoApiException("Currency code="
							+ countryDto.getCurrencyCode()
							+ " does not exists.");
				}
				if (tradingCountry == null) {
					throw new MeveoApiException("Trading country code="
							+ countryDto.getCountryCode() + " does not exists.");
				}
			}
		} else {
			StringBuilder sb = new StringBuilder(
					"The following parameters are required ");
			List<String> missingFields = new ArrayList<String>();

			if (StringUtils.isBlank(countryDto.getCountryCode())) {
				missingFields.add("countryCode");
			}
			if (StringUtils.isBlank(countryDto.getCurrencyCode())) {
				missingFields.add("currencyCode");
			}
			if (missingFields.size() > 1) {
				sb.append(org.apache.commons.lang.StringUtils.join(
						missingFields.toArray(), ", "));
			} else {
				sb.append(missingFields.get(0));
			}
			sb.append(".");

			throw new MeveoApiException(sb.toString());
		}
	}

	public void create(CountryEventData eventData, Long providerId, Long userId)
			throws MeveoApiException {
		if (!StringUtils.isBlank(eventData.getCountryId())
				&& !StringUtils.isBlank(eventData.getName())
				&& !StringUtils.isBlank(eventData.getCurrencyCode())) {

			// If countryCode exist in the trading country table
			// ("billing_trading_country"), return error.
			Provider provider = providerService.findById(providerId);
			User currentUser = userService.findById(userId);
			TradingCountry tradingCountry = tradingCountryService
					.findByTradingCountryCode(eventData.getCountryId(),
							provider);

			if (StringUtils.isBlank(eventData.getCurrencyCode())) {
				throw new MeveoApiException("Currency code="
						+ eventData.getCurrencyCode() + " does not exists.");
			}

			if (tradingCountry == null) {
				// check currency
				Country country = new Country();

				country = countryService.findByCode(eventData.getCountryId());
				Currency currency = null;
				Auditable auditable = new Auditable();

				// If country code doesn't exist in the reference table, create
				// the country in this table ("adm_country") with the currency
				// code for the default provider.
				if (country == null) {
					// If country code don't exist in the reference table,
					// create the country in this table ("adm_country") with the
					// currency code for the default provider.
					country = new Country();
					auditable.setCreated(new Date());
					auditable.setCreator(currentUser);
					country.setDescriptionEn(eventData.getName());
					country.setCountryCode(eventData.getCountryId());
				} else {
					// If country code exist in the reference table but the
					// currencyCode values in the two tables are different,
					// change the value of Currencycode in the country reference
					auditable.setUpdated(new Date());
					auditable.setUpdater(currentUser);
				}
				country.setAuditable(auditable);

				if (eventData.getCurrencyCode() != null) { //
					currency = currencyService.findByCode(eventData
							.getCurrencyCode());
					// If currencyCode don't exist in reference table
					// ("adm_currency"), return error.
					if (currency == null) {
						throw new MeveoApiException("Currency code="
								+ eventData.getCurrencyCode()
								+ " does not exists.");
					}
				} else {
					if (provider.getCurrency() != null) {
						currency = provider.getCurrency();
					}
				}
				country.setCurrency(currency);

				if (country.isTransient()) {
					countryService.create(em, country, currentUser, provider);
				} else {
					// countryService.update(em, country, currentUser);
				}

				Auditable auditableTrading = new Auditable();
				auditableTrading.setCreated(new Date());
				auditableTrading.setCreator(currentUser);

				// If country don't exist in the trading country table, create
				// the country in this table ("billing_trading_country").
				tradingCountry = new TradingCountry();
				tradingCountry.setCountry(country);
				tradingCountry.setProvider(provider);
				tradingCountry.setActive(true);
				tradingCountry.setPrDescription(eventData.getName());
				tradingCountry.setAuditable(auditableTrading);
				tradingCountryService.create(em, tradingCountry, currentUser,
						provider);

				// If currencyCode exist in reference table ("adm_currency") and
				// don't exist in the trading currency table, create the
				// currency in the trading currency table
				// ('billing_trading_currency").
				if (!StringUtils.isBlank(eventData.getCurrencyCode())
						&& tradingCurrencyService.findByTradingCurrencyCode(
								eventData.getCurrencyCode(), provider) != null) {
					TradingCurrency tradingCurrency = new TradingCurrency();
					tradingCurrency.setActive(true);
					tradingCurrency.setCurrency(currency);
					tradingCurrency.setAuditable(auditableTrading);
					tradingCurrency
							.setCurrencyCode(eventData.getCurrencyCode());
					tradingCurrency.setPrDescription(eventData
							.getCurrencyCode());
					tradingCurrencyService.create(em, tradingCurrency,
							currentUser, provider);
				}
			} else {
				throw new MeveoApiException("Trading country code="
						+ tradingCountry.getCountryCode() + " already exists.");
			}

		} else {
			StringBuilder sb = new StringBuilder(
					"The following parameters are required ");
			List<String> missingFields = new ArrayList<String>();

			if (StringUtils.isBlank(eventData.getCountryId())) {
				missingFields.add("countryCode");
			}
			if (StringUtils.isBlank(eventData.getName())) {
				missingFields.add("name");
			}
			if (StringUtils.isBlank(eventData.getCurrencyCode())) {
				missingFields.add("currencyCode");
			}
			if (missingFields.size() > 1) {
				sb.append(org.apache.commons.lang.StringUtils.join(
						missingFields.toArray(), ", "));
			} else {
				sb.append(missingFields.get(0));
			}
			sb.append(".");

			throw new MeveoApiException(sb.toString());
		}
	}

	public void remove(String countryCode, Long providerId)
			throws MeveoApiException {
		Provider provider = providerService.findById(providerId);

		if (!StringUtils.isBlank(countryCode)) {
			TradingCountry tradingCountry = tradingCountryService
					.findByTradingCountryCode(countryCode, provider);
			if (tradingCountry != null) {
				if (tradingCountry != null) {
					tradingCountryService.remove(tradingCountry);
				}
			} else {
				if (tradingCountry == null) {
					throw new MeveoApiException("Trading Country code="
							+ countryCode + " does not exists.");
				}
			}
		} else {
			StringBuilder sb = new StringBuilder(
					"The following parameters are required ");
			List<String> missingFields = new ArrayList<String>();

			if (StringUtils.isBlank(countryCode)) {
				missingFields.add("countryCode");
			}
			if (missingFields.size() > 1) {
				sb.append(org.apache.commons.lang.StringUtils.join(
						missingFields.toArray(), ", "));
			} else {
				sb.append(missingFields.get(0));
			}
			sb.append(".");

			throw new MeveoApiException(sb.toString());
		}
	}

	public void update(CountryEventData eventData, Long providerId, Long userId)
			throws MeveoApiException {
		Provider provider = providerService.findById(providerId);
		User currentUser = userService.findById(userId);

		if (!StringUtils.isBlank(eventData.getCountryId())
				&& !StringUtils.isBlank(eventData.getCurrencyCode())) {

			Currency currency = currencyService.findByCode(eventData
					.getCurrencyCode());
			TradingCountry tradingCountry = tradingCountryService
					.findByTradingCountryCode(eventData.getCountryId(),
							provider);

			if (currency != null && tradingCountry != null) {
				Country country = countryService.findByCode(em,
						eventData.getCountryId());
				if (country != null
						&& !StringUtils.isBlank(eventData.getName())) {
					if (!country.getDescriptionEn().equals(eventData.getName())) {
						country.setDescriptionEn(eventData.getName());
						countryService.update(em, country, currentUser);
					}
				} else {
					throw new MeveoApiException("Country code="
							+ eventData.getCountryId() + " does not exists.");
				}

				TradingCurrency tradingCurrency = tradingCurrencyService
						.findByTradingCurrencyCode(eventData.getCurrencyCode(),
								provider);
				if (tradingCurrency == null) {
					Auditable auditableTrading = new Auditable();
					auditableTrading.setCreated(new Date());
					auditableTrading.setCreator(currentUser);

					tradingCurrency = new TradingCurrency();
					tradingCurrency.setActive(true);
					tradingCurrency.setCurrency(currency);
					tradingCurrency.setAuditable(auditableTrading);
					tradingCurrency
							.setCurrencyCode(eventData.getCurrencyCode());
					tradingCurrency.setPrDescription(eventData
							.getCurrencyCode());
					tradingCurrencyService.create(em, tradingCurrency,
							currentUser, provider);
				}
			} else {
				if (currency == null) {
					throw new MeveoApiException("Currency code="
							+ eventData.getCurrencyCode() + " does not exists.");
				}
				if (tradingCountry == null) {
					throw new MeveoApiException("Trading country code="
							+ eventData.getCountryId() + " does not exists.");
				}
			}
		} else {
			StringBuilder sb = new StringBuilder(
					"The following parameters are required ");
			List<String> missingFields = new ArrayList<String>();

			if (StringUtils.isBlank(eventData.getCountryId())) {
				missingFields.add("countryCode");
			}
			if (StringUtils.isBlank(eventData.getCurrencyCode())) {
				missingFields.add("currencyCode");
			}
			if (missingFields.size() > 1) {
				sb.append(org.apache.commons.lang.StringUtils.join(
						missingFields.toArray(), ", "));
			} else {
				sb.append(missingFields.get(0));
			}
			sb.append(".");

			throw new MeveoApiException(sb.toString());
		}
	}

}
