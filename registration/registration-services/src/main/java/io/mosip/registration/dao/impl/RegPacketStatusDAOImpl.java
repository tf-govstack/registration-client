package io.github.tf-govstack.registration.dao.impl;

import static io.github.tf-govstack.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.github.tf-govstack.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import io.github.tf-govstack.kernel.core.logger.spi.Logger;
import io.github.tf-govstack.registration.config.AppConfig;
import io.github.tf-govstack.registration.constants.RegistrationClientStatusCode;
import io.github.tf-govstack.registration.dao.RegPacketStatusDAO;
import io.github.tf-govstack.registration.entity.Registration;
import io.github.tf-govstack.registration.repositories.RegistrationRepository;

/**
 * The implementation class of {@link RegPacketStatusDAO}.
 *
 * @author Himaja Dhanyamraju
 */
@Repository
public class RegPacketStatusDAOImpl implements RegPacketStatusDAO {

	/** The registration repository. */
	@Autowired
	private RegistrationRepository registrationRepository;
	
	/**
	 * Object for Logger
	 */
	private static final Logger LOGGER = AppConfig.getLogger(RegPacketStatusDAOImpl.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.github.tf-govstack.registration.dao.RegPacketStatusDAO#getPacketIdsByStatusUploaded()
	 */
	@Override
	public List<Registration> getPacketIdsByStatusUploadedOrExported() {
		LOGGER.info("REGISTRATION - PACKET_STATUS_SYNC - REG_PACKET_STATUS_DAO", APPLICATION_NAME, APPLICATION_ID,
				"getting packets by status uploaded-successfully has been started");

		return registrationRepository.findByClientStatusCodeOrClientStatusCommentsOrderByCrDtime(
				RegistrationClientStatusCode.UPLOADED_SUCCESSFULLY.getCode(),
				RegistrationClientStatusCode.EXPORT.getCode());
	}
	
	@Override
	public List<Registration> getPacketIdsByStatusExported() {
		LOGGER.info("Getting packets by status comment - EXPORTED has been started");

		return registrationRepository
				.findByClientStatusCommentsOrderByCrDtime(RegistrationClientStatusCode.EXPORT.getCode());

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.github.tf-govstack.registration.dao.RegPacketStatusDAO#update(io.github.tf-govstack.registration.
	 * entity.Registration)
	 */
	@Override
	public Registration update(Registration registration) {
		LOGGER.info("REGISTRATION - PACKET_STATUS_SYNC - REG_PACKET_STATUS_DAO", APPLICATION_NAME, APPLICATION_ID,
				"Update registration has been started");
		return registrationRepository.update(registration);

	}

	@Override
	public void delete(Registration registration) {
		LOGGER.info("Delete registration has been started");

		/* Delete Registartion */
		registrationRepository.deleteById(registration.getPacketId());
	}

}
