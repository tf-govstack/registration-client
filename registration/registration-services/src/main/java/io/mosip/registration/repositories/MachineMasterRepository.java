package io.github.tf-govstack.registration.repositories;
import io.github.tf-govstack.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.github.tf-govstack.registration.entity.MachineMaster;

/**
 * The repository interface for {@link MachineMaster} entity
 * @author Yaswanth S
 * @since 1.0.0
 *
 */
public interface MachineMasterRepository extends BaseRepository<MachineMaster, String>{
	
		
	/**
	 * Find machine based on  machine name.
	 * 
	 * @param machineName
	 * @return
	 */
	MachineMaster findByIsActiveTrueAndNameIgnoreCase(String machineName);

	MachineMaster findByNameIgnoreCase(String machineName);
	
}
