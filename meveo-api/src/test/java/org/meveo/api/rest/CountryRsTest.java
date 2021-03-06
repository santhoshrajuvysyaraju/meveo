package org.meveo.api.rest;

import java.util.logging.Logger;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.extension.rest.client.ArquillianResteasyResource;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.junit.InSequence;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.meveo.api.CountryIsoApi;
import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.ActionStatusEnum;
import org.meveo.api.dto.CountryIsoDto;
import org.meveo.api.dto.response.GetCountryIsoResponse;
import org.meveo.api.rest.impl.CountryIsoRsImpl;
import org.meveo.service.admin.impl.CountryService;
import org.meveo.service.admin.impl.CurrencyService;
import org.meveo.service.admin.impl.TradingCurrencyService;
import org.meveo.service.billing.impl.TradingCountryService;

/**
 * @author Edward P. Legaspi
 **/
@RunWith(Arquillian.class)
public class CountryRsTest extends BaseRsTest {

	private static Logger log = Logger.getLogger(CountryRsTest.class.getName());

	@BeforeClass
	public static void init() {
		log.info("beforeClass");
	}

	@Deployment
	public static Archive<?> createTestArchive() {
		WebArchive result = ShrinkWrap
				.create(WebArchive.class, "country-api.war");

		result = initArchive(result);

		// country
		result = result.addClasses(CountryIsoRsImpl.class, CountryIsoRs.class,
				GetCountryIsoResponse.class, CountryIsoApi.class,
				CountryService.class, TradingCountryService.class,
				CurrencyService.class, TradingCurrencyService.class);

		return result;
	}

	@RunAsClient
	@Test
	public void testVersion(
			@ArquillianResteasyResource("api/rest") CountryIsoRs resource) {
		ActionStatus result = resource.index();
		log.info("response=" + result);
		Assert.assertEquals(result.getStatus(), ActionStatusEnum.SUCCESS);
	}

	@RunAsClient
	@Test
	@InSequence(100)
	public void testCreate(
			@ArquillianResteasyResource("api/rest") CountryIsoRs resource) {
		CountryIsoDto countryDto = new CountryIsoDto();
		countryDto.setCountryCode("PH");
		countryDto.setCurrencyCode("PHP");
		countryDto.setLanguageCode("ENG");
		countryDto.setNameEn("Philippines");

		ActionStatus result = resource.create(countryDto);
		log.info("response=" + result);
		Assert.assertEquals(result.getStatus(), ActionStatusEnum.SUCCESS);
	}

	@RunAsClient
	@Test
	@InSequence(101)
	public void testCreateAlreadyExists(
			@ArquillianResteasyResource("api/rest") CountryIsoRs resource) {
		CountryIsoDto countryDto = new CountryIsoDto();
		countryDto.setCountryCode("PH");
		countryDto.setCurrencyCode("PHP");
		countryDto.setLanguageCode("ENG");
		countryDto.setNameEn("Philippines");

		ActionStatus result = resource.create(countryDto);
		log.info("response=" + result);
		Assert.assertEquals(result.getStatus(), ActionStatusEnum.FAIL);
	}

	@RunAsClient
	@Test
	@InSequence(102)
	public void testFind(
			@ArquillianResteasyResource("api/rest") CountryIsoRs resource) {
		GetCountryIsoResponse result = resource.find("PH");
		log.info("response=" + result);
		Assert.assertEquals(result.getActionStatus().getStatus(),
				ActionStatusEnum.SUCCESS);
	}

	@RunAsClient
	@Test
	@InSequence(103)
	public void testFindDoesNotExists(
			@ArquillianResteasyResource("api/rest") CountryIsoRs resource) {
		GetCountryIsoResponse result = resource.find("NONE");
		log.info("response=" + result);
		Assert.assertEquals(result.getActionStatus().getStatus(),
				ActionStatusEnum.FAIL);
	}

	@RunAsClient
	@Test
	@InSequence(104)
	public void testUpdate(
			@ArquillianResteasyResource("api/rest") CountryIsoRs resource) {
		CountryIsoDto countryDto = new CountryIsoDto();
		countryDto.setCountryCode("PH");
		countryDto.setCurrencyCode("PHP");
		countryDto.setLanguageCode("ENG");
		countryDto.setNameEn("Philippines-Updated");

		ActionStatus result = resource.update(countryDto);
		log.info("response=" + result);
		Assert.assertEquals(result.getStatus(), ActionStatusEnum.SUCCESS);
	}

	@RunAsClient
	@Test
	@InSequence(105)
	public void testUpdateDoesNotExists(
			@ArquillianResteasyResource("api/rest") CountryIsoRs resource) {
		CountryIsoDto countryDto = new CountryIsoDto();
		countryDto.setCountryCode("PH-NONE");
		countryDto.setCurrencyCode("PHP-NONE");
		countryDto.setLanguageCode("ENG");
		countryDto.setNameEn("Philippines-Updated");

		ActionStatus result = resource.update(countryDto);
		log.info("response=" + result);
		Assert.assertEquals(result.getStatus(), ActionStatusEnum.FAIL);
	}

	@RunAsClient
	@Test
	@InSequence(106)
	public void testRemove(
			@ArquillianResteasyResource("api/rest") CountryIsoRs resource) {
		ActionStatus result = resource.remove("PH");
		log.info("response=" + result);
		Assert.assertEquals(result.getStatus(), ActionStatusEnum.SUCCESS);
	}

	@RunAsClient
	@Test
	@InSequence(107)
	public void testRemoveDoesNotExists(
			@ArquillianResteasyResource("api/rest") CountryIsoRs resource) {
		ActionStatus result = resource.remove("PH-NONE");
		log.info("response=" + result);
		Assert.assertEquals(result.getStatus(), ActionStatusEnum.FAIL);
	}

}
