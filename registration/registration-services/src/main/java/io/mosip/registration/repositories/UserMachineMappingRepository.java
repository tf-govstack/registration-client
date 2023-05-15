package io.github.tf-govstack.registration.repositories;

import java.util.List;

import io.github.tf-govstack.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.github.tf-govstack.registration.entity.UserMachineMapping;
import io.github.tf-govstack.registration.entity.id.UserMachineMappingID;

/**
 * The reposistory interface for {@link UserMachineMapping} entity
 * 
 * @author YASWANTH S
 * @since 1.0.0
 *
 */

public interface UserMachineMappingRepository extends BaseRepository<UserMachineMapping, UserMachineMappingID> {
	
	List<UserMachineMapping>findByIsActiveTrueAndUserMachineMappingIdMachineId(String machineId);

	UserMachineMapping findByUserMachineMappingIdUsrIdIgnoreCase(String userId);
}
