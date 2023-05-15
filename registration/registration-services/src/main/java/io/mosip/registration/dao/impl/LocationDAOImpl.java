package io.github.tf-govstack.registration.dao.impl;

import static io.github.tf-govstack.registration.constants.RegistrationConstants.APPLICATION_ID;
import static io.github.tf-govstack.registration.constants.RegistrationConstants.APPLICATION_NAME;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import io.github.tf-govstack.kernel.core.logger.spi.Logger;
import io.github.tf-govstack.registration.config.AppConfig;
import io.github.tf-govstack.registration.dao.LocationDAO;
import io.github.tf-govstack.registration.entity.Location;
import io.github.tf-govstack.registration.repositories.LocationRepository;

/**
 * implementation class of {@link LocationDAO}
 * 
 * @author Brahmananda Reddy
 * @since 1.0.0
 *
 */
@Repository
public class LocationDAOImpl implements LocationDAO {
	/** instance of {@link LocationRepository} */
	@Autowired
	private LocationRepository locationRepository;
	/** instance of {@link Logger} */
	private static final Logger LOGGER = AppConfig.getLogger(LocationDAOImpl.class);

	/**
	 * (non-javadoc)
	 * 
	 * @see io.github.tf-govstack.registration.dao.LocationDAO#getLocations()
	 */
	@Override
	public List<Location> getLocations() {
		LOGGER.info("REGISTRATION-PACKET_CREATION-LocationDAO", APPLICATION_NAME,
				APPLICATION_ID, "fetching the locations");

		return locationRepository.findAll();

	}

}
