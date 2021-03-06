package org.meveo.api.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.meveo.api.dto.ActionStatus;
import org.meveo.api.dto.CurrencyIsoDto;
import org.meveo.api.dto.response.GetCurrencyIsoResponse;
import org.meveo.api.rest.security.RSSecured;

/**
 * Web service for managing {@link org.meveo.model.billing.Currency} 
 * 
 * @author Edward P. Legaspi
 **/
@Path("/currencyIso")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@RSSecured
public interface CurrencyIsoRs extends IBaseRs {

    /**
     * Creates tradingCurrency base on currency code. If the currency code does not exists, a currency record is created
     * 
     * @param postData
     * @return
     */
    @POST
    @Path("/")
    public ActionStatus create(CurrencyIsoDto currencyIsoDto);

    /**
     * Search currency with a given currency code.
     * 
     * @param currencyCode
     * @return
     */
    @GET
    @Path("/")
    public GetCurrencyIsoResponse find(@QueryParam("currencyCode") String currencyCode);

    /**
     * Remove currency with a given currency code.
     * 
     * @param currencyCode
     * @return
     */
    @DELETE
    @Path("/{currencyCode}")
    public ActionStatus remove(@PathParam("currencyCode") String currencyCode);

    /**
     * Modify a tradingCurrency. Same input parameter as create. The currency and tradingCurrency are created if they don't exists. The operation fails if the tradingCurrency is
     * null
     * 
     * @param postData
     * @return
     */
    @PUT
    @Path("/")
    public ActionStatus update(CurrencyIsoDto currencyIsoDto);

    @POST
    @Path("/createOrUpdate")
    public ActionStatus createOrUpdate(CurrencyIsoDto currencyIsoDto);

}
