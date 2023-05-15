package io.github.tf-govstack.registration.dao.impl;

import static io.github.tf-govstack.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.github.tf-govstack.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import io.github.tf-govstack.kernel.core.logger.spi.Logger;
import io.github.tf-govstack.registration.config.AppConfig;
import io.github.tf-govstack.registration.dao.ScreenAuthorizationDAO;
import io.github.tf-govstack.registration.dao.ScreenAuthorizationDetails;
import io.github.tf-govstack.registration.dto.AuthorizationDTO;
import io.github.tf-govstack.registration.repositories.ScreenAuthorizationRepository;

/**
 * The implementation class of {@link ScreenAuthorizationDAO}.
 *
 * @author Sravya Surampalli
 * @since 1.0.0
 */
@Repository
public class ScreenAuthorizationDAOImpl implements ScreenAuthorizationDAO {

	/**
	 * Instance of LOGGER
	 */
	private static final Logger LOGGER = AppConfig.getLogger(ScreenAuthorizationDAOImpl.class);

	/** The screenAuthorization repository. */
	@Autowired
	private ScreenAuthorizationRepository screenAuthorizationRepository;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mosip.registration.dao.RegistrationScreenAuthorizationDAO#
	 * getScreenAuthorizationDetails(java.lang.String)
	 */
	public AuthorizationDTO getScreenAuthorizationDetails(List<String> roleCode) {

		LOGGER.info("REGISTRATION - SCREEN_AUTHORIZATION - REGISTRATION_SCREEN_AUTHORIZATION_DAO_IMPL",
				APPLICATION_NAME, APPLICATION_ID, "Fetching List of Screens to be authorized");

		AuthorizationDTO authorizationDTO = new AuthorizationDTO();
		Set<ScreenAuthorizationDetails> authorizationList = screenAuthorizationRepository
				.findByScreenAuthorizationIdRoleCodeInAndIsPermittedTrueAndIsActiveTrue(roleCode);

		authorizationDTO.setAuthorizationScreenId(
				authorizationList.stream().map(auth -> auth.getScreenAuthorizationId().getScreenId())
						.collect(Collectors.toSet()));
		authorizationDTO.setAuthorizationRoleCode(roleCode);
		authorizationDTO.setAuthorizationIsPermitted(true);

		LOGGER.info("REGISTRATION - SCREEN_AUTHORIZATION - REGISTRATION_SCREEN_AUTHORIZATION_DAO_IMPL",
				APPLICATION_NAME, APPLICATION_ID, "List of Screens to be authorized are fetched successfully");

		return authorizationDTO;
	}
}
