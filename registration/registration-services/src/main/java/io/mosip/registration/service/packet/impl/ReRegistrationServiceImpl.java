package io.github.tf-govstack.registration.service.packet.impl;

import static io.github.tf-govstack.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.github.tf-govstack.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.github.tf-govstack.kernel.core.logger.spi.Logger;
import io.github.tf-govstack.registration.config.AppConfig;
import io.github.tf-govstack.registration.constants.LoggerConstants;
import io.github.tf-govstack.registration.constants.RegistrationClientStatusCode;
import io.github.tf-govstack.registration.constants.RegistrationConstants;
import io.github.tf-govstack.registration.dao.RegistrationDAO;
import io.github.tf-govstack.registration.dto.PacketStatusDTO;
import io.github.tf-govstack.registration.entity.Registration;
import io.github.tf-govstack.registration.service.BaseService;
import io.github.tf-govstack.registration.service.packet.ReRegistrationService;

/**
 * Implementation class for {@link ReRegistrationService}
 * 
 * @author saravanakumar gnanaguru
 * @since 1.0.0
 */
@Service
public class ReRegistrationServiceImpl extends BaseService implements ReRegistrationService {
	
	/**
	 * Instance of {@link MosipLogger}
	 */
	private static final Logger LOGGER = AppConfig.getLogger(ReRegistrationServiceImpl.class);

	@Autowired
	private RegistrationDAO registrationDAO;


	/*
	 * (non-Javadoc)
	 * 
	 * @see io.github.tf-govstack.registration.service.impl.ReRegistrationService#
	 * getAllReRegistrationPackets()
	 */
	@Override
	public List<PacketStatusDTO> getAllReRegistrationPackets() {
		LOGGER.info(LoggerConstants.LOG_GET_RE_REGISTER_PKT, APPLICATION_NAME, APPLICATION_ID,
				"Getting all the re-registration packets from the table");
		
		List<Registration> reRegisterPackets = registrationDAO.getAllReRegistrationPackets(RegistrationClientStatusCode.UPLOADED_SUCCESSFULLY.getCode(), 
				RegistrationConstants.PACKET_REJECTED_STATUS);
		List<PacketStatusDTO> uiPacketDto = new ArrayList<>();
		for (Registration reRegisterPacket : reRegisterPackets) {
			PacketStatusDTO packetStatusDTO = new PacketStatusDTO();
			packetStatusDTO.setFileName(reRegisterPacket.getAppId());
			packetStatusDTO.setPacketId(reRegisterPacket.getPacketId());
			packetStatusDTO.setPacketPath(reRegisterPacket.getAckFilename());
			packetStatusDTO.setCreatedTime(regDateTimeConversion(reRegisterPacket.getCrDtime().toString()));
			uiPacketDto.add(packetStatusDTO);
		}
		LOGGER.info(LoggerConstants.LOG_GET_RE_REGISTER_PKT, APPLICATION_NAME, APPLICATION_ID,
				"Fetching from the table finished");
		return uiPacketDto;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.github.tf-govstack.registration.service.impl.ReRegistrationService#
	 * updateReRegistrationStatus(java.util.Map)
	 */
	@Override
	public boolean updateReRegistrationStatus(Map<String, String> reRegistrationStatus) {
		LOGGER.info(LoggerConstants.LOG_UPADTE_RE_REGISTER_PKT, APPLICATION_NAME, APPLICATION_ID,
				"Update the registration status of the packet in the table");
		for (Map.Entry<String, String> reRegistration : reRegistrationStatus.entrySet()) {
			PacketStatusDTO registration = new PacketStatusDTO();
			registration.setPacketId(reRegistration.getKey());
			registration.setPacketClientStatus(RegistrationClientStatusCode.RE_REGISTER.getCode());
			registration.setClientStatusComments("Re-Register-" + reRegistration.getValue());
			registrationDAO.updateRegStatus(registration);
		}
		LOGGER.info(LoggerConstants.LOG_UPADTE_RE_REGISTER_PKT, APPLICATION_NAME, APPLICATION_ID,
				"All the reregistered packets are updated in the table");
		return true;
	}
	
}
