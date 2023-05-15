package io.github.tf-govstack.registration.dao.impl;

import static io.github.tf-govstack.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.github.tf-govstack.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import io.github.tf-govstack.kernel.core.logger.spi.Logger;
import io.github.tf-govstack.registration.config.AppConfig;
import io.github.tf-govstack.registration.context.ApplicationContext;
import io.github.tf-govstack.registration.dao.RegistrationCenterDAO;
import io.github.tf-govstack.registration.dto.RegistrationCenterDetailDTO;
import io.github.tf-govstack.registration.entity.MachineMaster;
import io.github.tf-govstack.registration.entity.RegistrationCenter;
import io.github.tf-govstack.registration.repositories.MachineMasterRepository;
import io.github.tf-govstack.registration.repositories.RegistrationCenterRepository;
import io.github.tf-govstack.registration.util.healthcheck.RegistrationSystemPropertiesChecker;

/**
 * The implementation class of {@link RegistrationCenterDAO}.
 *
 * @author Sravya Surampalli
 * @since 1.0.0
 */
@Repository
public class RegistrationCenterDAOImpl implements RegistrationCenterDAO {

	/**
	 * Instance of LOGGER
	 */
	private static final Logger LOGGER = AppConfig.getLogger(RegistrationCenterDAOImpl.class);

	/** The registrationCenter repository. */
	@Autowired
	private RegistrationCenterRepository registrationCenterRepository;

	@Autowired
	private MachineMasterRepository machineMasterRepository;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.mosip.registration.dao.RegistrationCenterDAO#getRegistrationCenterDetails
	 * (java.lang.String)
	 */
	public RegistrationCenterDetailDTO getRegistrationCenterDetails(String centerId,String langCode) {

		LOGGER.info("REGISTRATION - CENTER_NAME - REGISTRATION_CENTER_DAO_IMPL", APPLICATION_NAME,
				APPLICATION_ID, "Fetching Registration Center details");

		Optional<RegistrationCenter> registrationCenter = registrationCenterRepository
				.findByIsActiveTrueAndRegistartionCenterIdIdAndRegistartionCenterIdLangCode(centerId,langCode);
		RegistrationCenterDetailDTO registrationCenterDetailDTO = new RegistrationCenterDetailDTO();
		if (registrationCenter.isPresent()) {
			registrationCenterDetailDTO
					.setRegistrationCenterId(registrationCenter.get().getRegistartionCenterId().getId());
			registrationCenterDetailDTO.setRegistrationCenterName(registrationCenter.get().getName());
			registrationCenterDetailDTO.setRegsitrationCenterTypeCode(registrationCenter.get().getCenterTypeCode());
			registrationCenterDetailDTO.setRegistrationCenterAddrLine1(registrationCenter.get().getAddressLine1());
			registrationCenterDetailDTO.setRegistrationCenterAddrLine2(registrationCenter.get().getAddressLine2());
			registrationCenterDetailDTO.setRegistrationCenterAddrLine3(registrationCenter.get().getAddressLine3());
			registrationCenterDetailDTO.setRegistrationCenterLatitude(registrationCenter.get().getLatitude());
			registrationCenterDetailDTO.setRegistrationCenterLongitude(registrationCenter.get().getLongitude());
			registrationCenterDetailDTO.setRegistrationCenterLocationCode(registrationCenter.get().getLocationCode());
			registrationCenterDetailDTO.setRegistrationCenterContactPhone(registrationCenter.get().getContactPhone());
			registrationCenterDetailDTO.setRegistrationCenterWorkingHours(registrationCenter.get().getWorkingHours());
			registrationCenterDetailDTO.setRegistrationCenterNumberOfKiosks(registrationCenter.get().getNumberOfKiosks());
			registrationCenterDetailDTO.setRegistrationCenterPerKioskProcessTime(registrationCenter.get().getPerKioskProcessTime());
			registrationCenterDetailDTO.setRegistrationCenterHolidayLocCode(registrationCenter.get().getHolidayLocationCode());
		}

		LOGGER.info("REGISTRATION - CENTER_NAME - REGISTRATION_CENTER_DAO_IMPL", APPLICATION_NAME,
				APPLICATION_ID, "Registration Center details fetched successfully");

		return registrationCenterDetailDTO;
	}

	@Override
	public boolean isMachineCenterActive() {
		LOGGER.info("checking if Registration Center details exists and is active");

		String machineName = RegistrationSystemPropertiesChecker.getMachineId();
		MachineMaster machineMaster = machineMasterRepository.findByNameIgnoreCase(machineName.toLowerCase());

		if (machineMaster == null) {
			return false;
		}

		Optional<RegistrationCenter> result = registrationCenterRepository.findByIsActiveTrueAndRegistartionCenterIdIdAndRegistartionCenterIdLangCode(
				machineMaster.getRegCenterId(),	ApplicationContext.applicationLanguage());
		return result.isPresent();
	}

}
