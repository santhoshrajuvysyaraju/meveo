package org.meveo.api.communication;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.meveo.api.BaseApi;
import org.meveo.api.dto.communication.CommunicationRequestDto;
import org.meveo.api.exception.MissingParameterException;
import org.meveo.commons.utils.StringUtils;
import org.meveo.model.communication.MeveoInstance;
import org.meveo.service.communication.impl.MeveoInstanceService;

@Stateless
public class CommunicationApi extends BaseApi {

	@Inject
	MeveoInstanceService meveoInstanceService;

	public void inboundCommunication(CommunicationRequestDto communicationRequestDto) throws MissingParameterException {
		
		if (communicationRequestDto == null || StringUtils.isBlank(communicationRequestDto.getMeveoInstanceCode())) {
			missingParameters.add("MeveoInstanceCode");
		}
		
		if (communicationRequestDto == null ||  StringUtils.isBlank(communicationRequestDto.getSubject())) {
			missingParameters.add("Subject");
		}
		if(! missingParameters.isEmpty()){
			throw new MissingParameterException(getMissingParametersExceptionMessage());
		}
		
		MeveoInstance meveoInstance	 = meveoInstanceService.findByCode(communicationRequestDto.getMeveoInstanceCode());
		if(meveoInstance != null){
			//if(meveoInstance.getStatus() == MeveoInstanceStatusEnum.UNKNOWN)
			meveoInstanceService.fireInboundCommunicationEvent(communicationRequestDto);
		}
	}

}
