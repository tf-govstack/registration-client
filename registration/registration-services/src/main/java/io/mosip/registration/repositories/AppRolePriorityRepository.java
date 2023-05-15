package io.github.tf-govstack.registration.repositories;

import java.util.List;
import java.util.Set;

import io.github.tf-govstack.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.github.tf-govstack.registration.dao.AppRolePriorityDetails;
import io.github.tf-govstack.registration.entity.AppRolePriority;
import io.github.tf-govstack.registration.entity.id.AppRolePriorityId;

/**
 * The repository interface for {@link AppRolePriority} entity
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 *
 */
public interface AppRolePriorityRepository extends BaseRepository<AppRolePriority, AppRolePriorityId>{
	
	List<AppRolePriorityDetails> findByAppRolePriorityIdProcessIdAndAppRolePriorityIdRoleCodeInOrderByPriority(String processName, Set<String> roleList);

}
