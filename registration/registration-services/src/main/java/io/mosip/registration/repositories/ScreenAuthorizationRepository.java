package io.github.tf-govstack.registration.repositories;

import java.util.List;
import java.util.Set;

import io.github.tf-govstack.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.github.tf-govstack.registration.dao.ScreenAuthorizationDetails;
import io.github.tf-govstack.registration.entity.ScreenAuthorization;
import io.github.tf-govstack.registration.entity.id.ScreenAuthorizationId;

/**
 * The repository interface for {@link ScreenAuthorization} entity
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 *
 */

public interface ScreenAuthorizationRepository
		extends BaseRepository<ScreenAuthorization, ScreenAuthorizationId> {

	/**
	 * This method returns the list of {@link ScreenAuthorizationDetails} based
	 * on role code
	 * 
	 * @param roleCode
	 *            the roleCode
	 * @return the list of {@link ScreenAuthorizationDetails}
	 */
	Set<ScreenAuthorizationDetails> findByScreenAuthorizationIdRoleCodeInAndIsPermittedTrueAndIsActiveTrue(List<String> roleCode);

}
