package io.github.tf-govstack.registration.repositories;

import java.util.List;

import io.github.tf-govstack.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.github.tf-govstack.registration.entity.SyncControl;

/**
 * Repository interface for Sync Job.
 *
 * @author Sreekar Chukka
 */
public interface SyncJobControlRepository extends BaseRepository<SyncControl, String>{

	/* (non-Javadoc)
	 * @see org.springframework.data.jpa.repository.JpaRepository#findAll()
	 */
	@Override
	List<SyncControl> findAll();
	
	SyncControl findBySyncJobId(String syncJobId);
}
