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
import org.meveo.api.dto.SellerDto;
import org.meveo.api.dto.response.GetSellerResponse;
import org.meveo.api.dto.response.SellerCodesResponseDto;
import org.meveo.api.dto.response.SellerResponseDto;
import org.meveo.api.rest.security.RSSecured;

/**
 * Web service for managing {@link org.meveo.model.admin.Seller}.
 * 
 * @author Edward P. Legaspi
 **/
@Path("/seller")
@Consumes({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@Produces({ MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML })
@RSSecured
public interface SellerRs extends IBaseRs {

    /**
     * Create seller.
     * 
     * @param postData
     * @return
     */
    @Path("/")
    @POST
    ActionStatus create(SellerDto postData);

    /**
     * Update seller.
     * 
     * @param postData
     * @return
     */
    @Path("/")
    @PUT
    ActionStatus update(SellerDto postData);

    /**
     * Search for seller with a given code.
     * 
     * @param sellerCode
     * @return
     */
    @Path("/")
    @GET
    GetSellerResponse find(@QueryParam("sellerCode") String sellerCode);

    /**
     * Remove seller with a given code.
     * 
     * @param sellerCode
     * @return
     */
    @Path("/{sellerCode}")
    @DELETE
    ActionStatus remove(@PathParam("sellerCode") String sellerCode);

    /**
     * Search for seller with a given code.
     * 
     * @param sellerCode
     * @return
     */
    @Path("/list")
    @GET
    SellerResponseDto list();

    @Path("/listSellerCodes")
    @GET
    SellerCodesResponseDto listSellerCodes();

    @Path("/createOrUpdate")
    @POST
    ActionStatus createOrUpdate(SellerDto postData);

}
